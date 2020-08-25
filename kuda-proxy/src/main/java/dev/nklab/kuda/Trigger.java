/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.nklab.kuda;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import javax.enterprise.context.Dependent;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 *
 * @author koduki
 */
@Dependent
public class Trigger {

    @ConfigProperty(name = "kudaproxy.trigger.url")
    String triggerUrl;

    public Trigger() {
    }

    public String callTrigger(Map<String, Object> params) throws JsonProcessingException {
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(new ObjectMapper().writeValueAsString(params)))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .uri(URI.create(triggerUrl))
                .build();

//       return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(System.out::println);

        return "done async";
    }
}
