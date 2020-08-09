package slide4vr.apps;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.PathElement;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.StructuredQuery;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import io.opencensus.contrib.http.jaxrs.JaxrsClientFilter;
import io.quarkus.security.Authenticated;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import slide4vr.fw.Trace;
import slide4vr.fw.WebTrace;

@Path("/slide")
public class UploadResource {

    private static final String BASE_URL = "https://storage.googleapis.com";

    @Inject
    JsonWebToken jwt;

    @ConfigProperty(name = "slide4vr.gcp.projectid")
    String projectId;
    @ConfigProperty(name = "slide4vr.gcp.bucketname.pptx")
    String bucketPptxName;
    @ConfigProperty(name = "slide4vr.gcp.bucketname.slide")
    String bucketSlideName;
    @ConfigProperty(name = "slide4vr.pptx2png.object")
    String objectName;
    @ConfigProperty(name = "slide4vr.pptx2png.url")
    String pptx2pngUrl;

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @WebTrace
    @Authenticated
    public Response list(@Context SecurityContext ctx) throws IOException {
        var id = ctx.getUserPrincipal().getName();
        System.out.println("id: " + id);

        var datastore = DatastoreOptions.getDefaultInstance().getService();
//        var query = Query.newGqlQueryBuilder(Query.ResultType.ENTITY,
//                "SELECT * FROM Slide WHERE __key__ HAS ANCESTOR KEY(User, @id)")
//                .setBinding("id", id)
//                .build();
        var query = Query.newEntityQueryBuilder()
                .setKind("Slide")
                .setFilter(StructuredQuery.PropertyFilter.hasAncestor(
                        datastore.newKeyFactory().setKind("User").newKey(id)))
                .build();

        var result = new ArrayList<Map<String, String>>();

        var slides = datastore.run(query);
        while (slides.hasNext()) {
            var slide = slides.next();
            result.add(Map.of(
                    "key", slide.getKey().getName(),
                    "title", slide.getString("title"),
                    "created_at", slide.getString("created_at")
            ));
        }

        return Response.ok(new ObjectMapper().writeValueAsString(result))
                .build();
    }

    @GET
    @Path("{key}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @WebTrace
    @Authenticated
    public Response get(@Context SecurityContext ctx, @PathParam("key") String key) throws IOException {
        var id = ctx.getUserPrincipal().getName();
        var result = new ArrayList<String>();

        for (var x : readBucket(id, key)) {
            result.add(BASE_URL + "/" + bucketSlideName + "/" + x.getName());
        }

        var item = Map.of("whiteboard", Map.of("source_urls", result));
        var printer = new DefaultPrettyPrinter();
        printer.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);

        var json = new ObjectMapper().writer(printer).writeValueAsString(item);

        return Response.ok(json)
                .build();
    }

    @POST
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @Produces(MediaType.APPLICATION_JSON)
    @WebTrace
    @Authenticated
    public Response upload(@Context SecurityContext ctx, @MultipartForm SlideFormBean slide) throws IOException {
        var id = ctx.getUserPrincipal().getName();

        var data = slide.getSlide();
        var key = UUID.randomUUID().toString();

        upload2gcs(id, data);
        storeData(id, key, slide);
        callPptx2pngAPI(id, key);

        return Response.ok(
                String.format("{message:'%s', data-size:'%d'}",
                        slide.getTitle(),
                        slide.getSlide().length))
                .build();
    }

    @Trace
    public void callPptx2pngAPI(String id, String key) {
        var dirName = id + "/" + key;

        var target = ClientBuilder.newClient()
                .target(pptx2pngUrl)
                .path("/")
                .queryParam("args", dirName);

        target.register(JaxrsClientFilter.class);
        target.request(MediaType.APPLICATION_JSON)
                .get(new GenericType<String>() {
                });
    }

    @Trace
    public void upload2gcs(String id, byte[] data) {
        var storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        var blobId = BlobId.of(bucketPptxName, id + "/" + objectName);
        var blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, data);
    }

    @Trace
    public void storeData(String id, String key, SlideFormBean slide) {
        var tz = TimeZone.getTimeZone("UTC");
        var df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);

        var datastore = DatastoreOptions.getDefaultInstance().getService();
        var slideKey = datastore.newKeyFactory()
                .addAncestors(PathElement.of("User", id))
                .setKind("Slide")
                .newKey(key);
        var task = Entity.newBuilder(slideKey)
                .set("title", slide.getTitle())
                .set("created_at", df.format(new Date()))
                .build();
        datastore.put(task);
    }

    @Trace
    public Iterable<Blob> readBucket(String id, String key) {
        var storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        var bucket = storage.get(bucketSlideName);
        var option = Storage.BlobListOption.prefix(id + "/" + key);

        return bucket.list(option).iterateAll();
    }
}
