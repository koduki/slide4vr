package app;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class EventResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String invoke() throws InterruptedException {
        
        for (int i = 1; i <= 5; i++) {
            System.out.println("wait " + i + " sec");
            Thread.sleep(i * 1000L);
        }
        return "finish long process";
    }
}
