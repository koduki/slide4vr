/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.storage.StorageOptions;
import dev.nklab.jl2.collections.CollectionUtils;
import dev.nklab.jl2.web.profile.WebTrace;
import dev.nklab.kuda.core.FlowService;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 *
 * @author koduki
 */
@Path("/management")
public class ManagemnetResource {

    @ConfigProperty(name = "kuda.manager.gcp.projectid")
    String projectId;
    @ConfigProperty(name = "kuda.manager.flowbucket")
    String bucketPath;

    @Inject
    FlowService flowService;

    @POST
    @Path("load")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
//    @WebTrace
    public Response load() throws JsonProcessingException {
        var storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        var bucket = storage.get(bucketPath);
        var flowFiles = bucket.list().iterateAll();
        var r = CollectionUtils.toStream(flowFiles).map(flowYaml -> {
            var data = flowYaml.getContent();
            try ( var input = new ByteArrayInputStream(data)) {
                return CollectionUtils.concat(flowService.store(input), Map.of("file", flowYaml.getName()));
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }).collect(Collectors.toList());
        return Response.ok(new ObjectMapper().writeValueAsString(r))
                .build();
    }

    @GET
    @Path("flows")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
//    @WebTrace
    public Response listflow() throws JsonProcessingException {
        return Response.ok(new ObjectMapper().writeValueAsString(flowService.list()))
                .build();
    }

    @GET
    @Path("triggers/{flowName}/{endpointName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @WebTrace
    public Response get(@PathParam("flowName") String flowName, @PathParam("endpointName") String endpointName) throws JsonProcessingException {
        var triggers = flowService.getTriggers(flowName, endpointName);
        return Response.ok(new ObjectMapper().writeValueAsString(triggers))
                .build();
    }

}
