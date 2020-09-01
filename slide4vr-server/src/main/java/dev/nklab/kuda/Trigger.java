/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.nklab.kuda;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.nklab.jl2.profile.DistributedTracer;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import javax.enterprise.context.Dependent;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 *
 * @author koduki
 */
@Dependent
public class Trigger {
    
    @ConfigProperty(name = "kudaproxy.trigger.url1")
    String triggerUrl1;
    
    @ConfigProperty(name = "kudaproxy.trigger.url2")
    String triggerUrl2;
    
    public Trigger() {
    }
    
    public String callTrigger(Map<String, Object> params, String predicate) throws JsonProcessingException {
        var targetUrls = Map.of(
                "pdf", triggerUrl1,
                "pptx", triggerUrl2
        );
        
        var isTrace = ConfigProvider.getConfig().getOptionalValue("dev.nklab.profile.trace", Boolean.class);
        var traceparent = DistributedTracer.trace().isTrace(isTrace.orElse(false)).getTraceparent();
        
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(new ObjectMapper().writeValueAsString(params)))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .uri(URI.create(targetUrls.get(predicate)));
        
        if (traceparent.isPresent()) {
            request.header("traceparent", traceparent.get());
        }
//       return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        client.sendAsync(request.build(), HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(System.out::println);
        
        return "done async";
    }
}
