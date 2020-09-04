/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.nklab.kuda.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import static dev.nklab.jl2.Extentions.$;
import dev.nklab.jl2.web.logging.Logger;
import dev.nklab.jl2.web.mp.Config;
import dev.nklab.jl2.web.profile.DistributedTracer;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 *
 * @author koduki
 */
@Dependent
public class Trigger {

    private final Logger logger = Logger.getLogger("kuda-core");

    @Inject
    FlowService flowService;

    public Trigger() {
    }

    public String callTrigger(Map<String, Object> params, String condKey) throws JsonProcessingException {
        var flowName = Config.get("kuda.core.flow", "");
        var endpointName = Config.get("kuda.core.endpoint", "");
        var isTrace = Config.get("dev.nklab.profile.trace", false);

        logger.debug("params", $("params", params.toString()));
        logger.debug("config", $("flowName", flowName), $("endpointName", endpointName), $("isTrace", String.valueOf(isTrace)), $("condKey", condKey));

        var targetUrls = flowService.getTriggers(flowName, endpointName);
        var traceparent = DistributedTracer.trace().isTrace(isTrace).getTraceparent();

        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(new ObjectMapper().writeValueAsString(params)))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .uri(URI.create(targetUrls.get(condKey).get("trigger-url")));

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
