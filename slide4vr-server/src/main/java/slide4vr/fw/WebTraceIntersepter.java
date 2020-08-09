/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slide4vr.fw;

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

    @ConfigProperty(name = "slide4vr.profile.trace")
    boolean isTrace;

    @Inject
    HttpServletRequest req;

    @AroundInvoke
    public Object invoke(InvocationContext ic) throws Exception {
        var url = req.getRequestURI();
        var method = req.getMethod();

        return DistributedTracer.trace().isTrace(isTrace).apply(method + "#" + url, req, () -> {
            try {
                return ic.proceed();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}
