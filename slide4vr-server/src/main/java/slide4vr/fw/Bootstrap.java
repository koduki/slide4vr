/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slide4vr.fw;

import io.opencensus.exporter.trace.stackdriver.StackdriverTraceConfiguration;
import io.opencensus.exporter.trace.stackdriver.StackdriverTraceExporter;
import java.io.IOException;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 *
 * @author koduki
 */
@ApplicationScoped
public class Bootstrap {

    @ConfigProperty(name = "slide4vr.gcp.projectid")
    String projectId;
    @ConfigProperty(name = "slide4vr.profile.trace")
    boolean isTrace;

    public void handle(@Observes @Initialized(ApplicationScoped.class) Object event) throws IOException {
        if (isTrace) {
            StackdriverTraceExporter.createAndRegister(
                    StackdriverTraceConfiguration.builder()
                            .setProjectId(projectId)
                            .build());
        }
    }
}
