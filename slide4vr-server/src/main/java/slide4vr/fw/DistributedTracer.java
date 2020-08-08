/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slide4vr.fw;

import io.opencensus.exporter.trace.stackdriver.StackdriverTraceConfiguration;
import io.opencensus.exporter.trace.stackdriver.StackdriverTraceExporter;
import io.opencensus.trace.Tracing;
import io.opencensus.trace.propagation.SpanContextParseException;
import io.opencensus.trace.propagation.TextFormat;
import io.opencensus.trace.samplers.Samplers;
import java.io.IOException;
import java.util.function.Supplier;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author koduki
 */
public class DistributedTracer {

    private static final TextFormat textFormat = Tracing.getPropagationComponent().getTraceContextFormat();
    private static final TextFormat.Getter<HttpServletRequest> getter = new TextFormat.Getter<HttpServletRequest>() {
        @Override
        public String get(HttpServletRequest httpRequest, String s) {
            return httpRequest.getHeader(s);
        }
    };

    private boolean isTrace = true;

    private DistributedTracer() {

    }

    public static DistributedTracer trace() {
        return new DistributedTracer();
    }

    public DistributedTracer isTrace(boolean isTrace) {
        this.isTrace = isTrace;
        return this;
    }

    public void init(String projectId) throws IOException {
        if (isTrace) {
            StackdriverTraceExporter.createAndRegister(
                    StackdriverTraceConfiguration.builder()
                            .setProjectId(projectId)
                            .build());
        }
    }

    public <R> R apply(String name, Supplier<R> callback) {
        if (isTrace) {
            try ( var ss = Tracing.getTracer()
                    .spanBuilder(name)
                    .setRecordEvents(true)
                    .setSampler(Samplers.alwaysSample())
                    .startScopedSpan()) {
                return callback.get();
            }
        } else {
            return callback.get();
        }
    }

    public <R> R apply(String name, HttpServletRequest request, Supplier<R> callback) {
        if (isTrace && request.getHeader("traceparent") != null) {
            try {
                var spanContext = textFormat.extract(request, getter);
                try ( var ss = Tracing.getTracer()
                        .spanBuilderWithRemoteParent(name, spanContext)
                        .setRecordEvents(true)
                        .setSampler(Samplers.alwaysSample())
                        .startScopedSpan()) {
                    return callback.get();
                }
            } catch (SpanContextParseException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            return callback.get();
        }
    }
}
