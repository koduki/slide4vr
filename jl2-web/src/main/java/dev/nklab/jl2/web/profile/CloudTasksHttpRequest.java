/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.nklab.jl2.web.profile;

import com.google.cloud.tasks.v2.HttpRequest;
import org.eclipse.microprofile.config.ConfigProvider;

/**
 *
 * @author koduki
 */
public class CloudTasksHttpRequest {

    public static HttpRequest.Builder newBuilder() {
        var isTrace = ConfigProvider.getConfig().getOptionalValue("dev.nklab.profile.trace", Boolean.class);

        var traceparent = DistributedTracer.trace().isTrace(isTrace.orElse(false)).getTraceparent();
        var requestBuilder = HttpRequest.newBuilder();
        return (traceparent.isPresent())
                ? requestBuilder.putHeaders("traceparent", traceparent.get())
                : requestBuilder;

    }
}
