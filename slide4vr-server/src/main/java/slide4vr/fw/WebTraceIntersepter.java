/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slide4vr.fw;

import io.opencensus.trace.Tracing;
import io.opencensus.trace.propagation.TextFormat;
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

    private static final TextFormat textFormat = Tracing.getPropagationComponent().getTraceContextFormat();
    private static final TextFormat.Getter<HttpServletRequest> getter = new TextFormat.Getter<HttpServletRequest>() {
        @Override
        public String get(HttpServletRequest httpRequest, String s) {
            return httpRequest.getHeader(s);
        }
    };

    @ConfigProperty(name = "slide4vr.profile.trace")
    boolean isTrace;

    @Inject
    HttpServletRequest req;

    @AroundInvoke
    public Object invoke(InvocationContext ic) throws Exception {
        if (isTrace && req.getHeader("traceparent") != null) {
            var spanContext = textFormat.extract(req, getter);
            var url = req.getRequestURI();
            var method = req.getMethod();

            return DistributedTrace.trace(method + "#" + url, spanContext, () -> {
                try {
                    return ic.proceed();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
        } else {
            return ic.proceed();
        }
    }
}
