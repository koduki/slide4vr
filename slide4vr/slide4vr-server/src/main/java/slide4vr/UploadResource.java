package slide4vr;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

@Path("/slide")
public class UploadResource {

    @POST
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @Produces({MediaType.APPLICATION_JSON})
    public Response upload(@MultipartForm SlideFormBean slide) {
        System.out.println(slide.getSlide().length);
        return Response.ok(
                String.format("{message:'%s', data-size:'%d'}",
                        slide.getTitle(),
                        slide.getSlide().length))
                .build();
    }
}
