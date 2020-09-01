/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.nklab.jl2.web.profile;

import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 *
 * @author koduki
 */
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
@Trace
public class TraceIntersepter {

    @ConfigProperty(name = "dev.nklab.profile.trace")
    boolean isTrace;

    @AroundInvoke
    public Object invoke(InvocationContext ic) throws Exception {
        var classAndMethod = ic.getTarget().getClass()
                .getSuperclass().getName()
                + "#" + ic.getMethod().getName();
        return DistributedTracer.trace().isTrace(isTrace).apply(classAndMethod, (t) -> {
            try {
                return ic.proceed();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}
