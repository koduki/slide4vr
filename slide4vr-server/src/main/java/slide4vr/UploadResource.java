package slide4vr;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Query;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

@Path("/slide")
public class UploadResource {

    private static final String BASE_URL = "https://storage.googleapis.com";

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
    @ConfigProperty(name = "slide4vr.pptx2png.dir")
    String dir;

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response list() throws IOException {
        var datastore = DatastoreOptions.getDefaultInstance().getService();
        var query = Query.newEntityQueryBuilder()
                .setKind("Slide")
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
    public Response get(@PathParam("key") String key) throws IOException {
        var result = new ArrayList<String>();

        for (var x : readBucket(key)) {
            result.add(BASE_URL + "/" + bucketSlideName + "/" + x.getName());
        }

        var item = Map.of("whiteboard", Map.of("source_urls", result));
        var printer = new DefaultPrettyPrinter();
        printer.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);

        var json = new ObjectMapper().writer(printer).writeValueAsString(item);

        return Response.ok(json)
                .build();
    }

    private Iterable<Blob> readBucket(String key) {
        var storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        var bucket = storage.get(bucketSlideName);
        var option = Storage.BlobListOption.prefix(dir + "/" + key);

        return bucket.list(option).iterateAll();
    }

    @POST
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @Produces(MediaType.APPLICATION_JSON)
    public Response upload(@MultipartForm SlideFormBean slide) throws IOException {
        var data = slide.getSlide();
        var key = UUID.randomUUID().toString();

        System.out.println("hoge3: " + slide.getTitle());

        upload2gcs(data);
        storeData(key, slide);
        callPptx2pngAPI(key);

        return Response.ok(
                String.format("{message:'%s', data-size:'%d'}",
                        slide.getTitle(),
                        slide.getSlide().length))
                .build();
    }

    private void callPptx2pngAPI(String key) {
        var dirName = dir + "/" + key;
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
                .uri(URI.create(pptx2pngUrl + "?args=" + dirName))
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(System.out::println)
                .join();
    }

    private void upload2gcs(byte[] data) {
        var storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        var blobId = BlobId.of(bucketPptxName, objectName);
        var blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, data);
    }

    private void storeData(String key, SlideFormBean slide) {
        var tz = TimeZone.getTimeZone("UTC");
        var df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);

        var datastore = DatastoreOptions.getDefaultInstance().getService();
        var slideKey = datastore.newKeyFactory().setKind("Slide").newKey(key);
        var task = Entity.newBuilder(slideKey)
                .set("title", slide.getTitle())
                .set("created_at", df.format(new Date()))
                .build();
        datastore.put(task);
    }
}
