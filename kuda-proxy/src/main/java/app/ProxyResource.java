package app;

import dev.nklab.kuda.Trigger;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/")
public class ProxyResource {

    @Inject
    Trigger trigger;

    @ConfigProperty(name = "kudaproxy.target.url")
    String targetUrl;

    @ConfigProperty(name = "kudaproxy.target.method")
    String targetMethod;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String forward(Map<String, Object> params) throws JsonProcessingException, IOException, InterruptedException {
        // Parse request
        System.out.println(params.get("key"));
        var targetParams = (Map<String, String>) params.get("targetParams");

        var query = "";
        if ("GET".equals(targetMethod)) {
            query = String.join("&",
                    targetParams.entrySet().stream()
                            .map(x -> x.getKey() + "=" + x.getValue()).collect(Collectors.toList())
            );
        }

        // Exec target application
        callTarget(targetMethod, query);

        return trigger.callTrigger(params);
    }

    void callTarget(String targetMethod, String query) throws InterruptedException, IOException {
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .uri(URI.create(targetUrl + "/?" + query))
                .build();
        var res = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("http_code:" + res.statusCode() + ", body:" + res.body());
    }
}
