/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slide4vr.fw;

import io.opencensus.trace.SpanContext;
import io.opencensus.trace.Tracing;
import io.opencensus.trace.samplers.Samplers;
import java.util.function.Supplier;

/**
 *
 * @author koduki
 */
public class DistributedTrace {

    public static <R> R trace(String name, Supplier<R> callback) {
        try ( var ss = Tracing.getTracer()
                .spanBuilder(name)
                .setRecordEvents(true)
                .setSampler(Samplers.alwaysSample())
                .startScopedSpan()) {
            return callback.get();
        }
    }

    public static <R> R trace(String name, SpanContext context, Supplier<R> callback) {
        try ( var ss = Tracing.getTracer()
                .spanBuilderWithRemoteParent(name, context)
                .setRecordEvents(true)
                .setSampler(Samplers.alwaysSample())
                .startScopedSpan()) {
            return callback.get();
        }
    }
}
