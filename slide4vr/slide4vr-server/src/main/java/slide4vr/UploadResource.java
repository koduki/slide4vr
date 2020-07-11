package slide4vr;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/upload")
public class UploadResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello2() {
        return "hello";
    }
}
