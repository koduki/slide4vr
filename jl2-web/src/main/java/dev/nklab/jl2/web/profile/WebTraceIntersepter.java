/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.nklab.jl2.web.profile;

import io.opencensus.contrib.http.util.HttpTraceAttributeConstants;
import io.opencensus.trace.AttributeValue;
import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 *
 * @author koduki
 */
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
@WebTrace
public class WebTraceIntersepter {

    @ConfigProperty(name = "dev.nklab.profile.trace")
    boolean isTrace;

    @ConfigProperty(name = "dev.nklab.profile.appname")
    String appName;

    @Inject
    HttpServletRequest req;

    @AroundInvoke
    public Object invoke(InvocationContext ic) throws Exception {
        var url = req.getRequestURL().toString();
        var name = appName + ":" + ic.getTarget().getClass()
                .getSuperclass().getName()
                + "#" + ic.getMethod().getName();

        return DistributedTracer.trace().isTrace(isTrace).apply(name, req, (t) -> {
            try {
                if (t.isPresent()) {
                    var span = t.get();
                    span.putAttribute(HttpTraceAttributeConstants.HTTP_URL, AttributeValue.stringAttributeValue(url));
                }

                return ic.proceed();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}
