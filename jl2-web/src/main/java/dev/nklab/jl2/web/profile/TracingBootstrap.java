/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.nklab.jl2.web.profile;

import java.io.IOException;
import javax.enterprise.context.Dependent;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 *
 * @author koduki
 */
@Dependent
public class TracingBootstrap {

    @ConfigProperty(name = "dev.nklab.profile.gcp.projectid")
    String projectId;
    @ConfigProperty(name = "dev.nklab.profile.trace")
    boolean isTrace;

    public TracingBootstrap() {
    }
    

    public void init() throws IOException {
        DistributedTracer.trace()
                .isTrace(isTrace)
                .init(projectId);
    }
}
