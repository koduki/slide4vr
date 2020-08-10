package slide4vr.apps;

import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.PathElement;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.StructuredQuery;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import javax.enterprise.context.Dependent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import dev.nklab.jl2.profile.Trace;

/**
 *
 * @author koduki
 */
@Dependent
public class SlideService {

    private static final String BASE_URL = "https://storage.googleapis.com";

    @ConfigProperty(name = "slide4vr.gcp.projectid")
    String projectId;
    @ConfigProperty(name = "slide4vr.gcp.bucketname.pptx")
    String bucketPptxName;
    @ConfigProperty(name = "slide4vr.gcp.bucketname.slide")
    String bucketSlideName;
    @ConfigProperty(name = "slide4vr.pptx2png.object")
    String objectName;

    @Trace
    public void upload(String id, String key, byte[] data) {
        var storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        var blobId = BlobId.of(bucketPptxName, id + "/" + key + ".pptx");
        var blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, data);
    }

    @Trace
    public void create(String id, String key, SlideFormBean slide) {
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
    public Map<String, Map<String, List<String>>> getSlide(String id, String key) {
        var storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        var bucket = storage.get(bucketSlideName);
        var option = Storage.BlobListOption.prefix(id + "/" + key);

        var items = bucket.list(option).iterateAll();

        var result = (List<String>) new ArrayList<String>();
        for (var x : items) {
            result.add(BASE_URL + "/" + bucketSlideName + "/" + x.getName());
        }
        var item = Map.of("whiteboard", Map.of("source_urls", result));

        return item;
    }

    @Trace
    public List<Map<String, String>> listSlides(String id) {
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
        return result;
    }
}
