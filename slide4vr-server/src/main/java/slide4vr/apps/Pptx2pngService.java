/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slide4vr.apps;

import com.fasterxml.jackson.core.JsonProcessingException;
import javax.enterprise.context.Dependent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import dev.nklab.jl2.profile.Trace;
import dev.nklab.jl2.logging.Logger;
import dev.nklab.kuda.Trigger;
import java.io.UncheckedIOException;
import java.util.Map;
import javax.inject.Inject;

/**
 *
 * @author koduki
 */
@Dependent
public class Pptx2pngService {

    private final Logger logger = Logger.getLogger("slide4vr");

    @Inject
    Trigger trigger;

    @ConfigProperty(name = "slide4vr.gcp.projectid")
    String projectId;
    @ConfigProperty(name = "slide4vr.gcp.cloudtasks.qid")
    String queueId;
    @ConfigProperty(name = "slide4vr.gcp.cloudtasks.location")
    String locationId;

    @Trace
    public void request(String userId, String key) {
        try {
            var pptxName = userId + "/" + key + ".pptx";
            var pngDir = userId + "/" + key;

            var params = Map.of(
                    "userId", userId,
                    "key", key,
                    "targetParams", Map.of("args", pptxName + "," + pngDir)
            );

            trigger.callTrigger(params);
        } catch (JsonProcessingException ex) {
            throw new UncheckedIOException(ex);
        }
    }

}
