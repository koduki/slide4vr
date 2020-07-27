/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slide4vr.fw;

import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 *
 * @author koduki
 */
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
@Trace
public class TraceIntersepter {

    @AroundInvoke
    public Object invoke(InvocationContext ic) throws Exception {
        var classAndMethod = ic.getTarget().getClass()
                .getSuperclass().getName()
                + "#" + ic.getMethod().getName();
        return DistributedTrace.trace(classAndMethod, () -> {
            try {
                return ic.proceed();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}
