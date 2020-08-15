/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slide4vr.apps;

import static cn.orz.pascal.jl2.Extentions.*;
import com.google.cloud.tasks.v2.CloudTasksClient;
import com.google.cloud.tasks.v2.HttpMethod;
import com.google.cloud.tasks.v2.QueueName;
import com.google.cloud.tasks.v2.Task;
import javax.enterprise.context.Dependent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import dev.nklab.jl2.profile.Trace;
import dev.nklab.jl2.logging.Logger;
import dev.nklab.jl2.profile.CloudTasksHttpRequest;
import java.io.IOException;
import java.io.UncheckedIOException;

/**
 *
 * @author koduki
 */
@Dependent
public class Pptx2pngService {

    private final Logger logger = Logger.getLogger("slide4vr");

    @ConfigProperty(name = "slide4vr.gcp.projectid")
    String projectId;
    @ConfigProperty(name = "slide4vr.gcp.cloudtasks.qid")
    String queueId;
    @ConfigProperty(name = "slide4vr.gcp.cloudtasks.location")
    String locationId;
    @ConfigProperty(name = "slide4vr.pptx2png.url")
    String pptx2pngUrl;

    @Trace
    public void request(String id, String key) {
        try {
            var pptxName = id + "/" + key + ".pptx";
            var pngDir = id + "/" + key;

            callAPI(projectId, locationId, queueId, pptxName, pngDir);

        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public void callAPI(String projectId, String locationId, String queueId, String pptxName, String pngDir) throws IOException {
        try ( var client = CloudTasksClient.create()) {
            var queuePath = QueueName.of(projectId, locationId, queueId).toString();
            var taskBuilder = Task.newBuilder()
                    .setHttpRequest(CloudTasksHttpRequest.newBuilder()
                            .setUrl(pptx2pngUrl + "/?args=" + pptxName + "," + pngDir)
                            .setHttpMethod(HttpMethod.GET)
                            .build());

            // Send create task request.
            var task = client.createTask(queuePath, taskBuilder.build());
            logger.debug("call-pptx2png", $("taskid", task.getName()));
        }
    }

}
