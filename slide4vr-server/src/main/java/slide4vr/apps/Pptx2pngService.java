/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slide4vr.apps;

import io.opencensus.contrib.http.jaxrs.JaxrsClientFilter;
import javax.enterprise.context.Dependent;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import dev.nklab.jl2.profile.Trace;

/**
 *
 * @author koduki
 */
@Dependent
public class Pptx2pngService {

    @ConfigProperty(name = "slide4vr.pptx2png.url")
    String pptx2pngUrl;

    @Trace
    public void request(String id, String key) {
        var pptxName = id + "/" + key + ".pptx";
        var pngDir = id + "/" + key;

        var target = ClientBuilder.newClient()
                .target(pptx2pngUrl)
                .path("/")
                .queryParam("args", pptxName + "," + pngDir);

        target.register(JaxrsClientFilter.class);
        target.request(MediaType.APPLICATION_JSON)
                .get(new GenericType<String>() {
                });
    }
}
