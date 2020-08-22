/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.nklab.jl2.profile;

import static cn.orz.pascal.jl2.Extentions.*;
import dev.nklab.jl2.logging.Logger;
import io.opencensus.exporter.trace.stackdriver.StackdriverTraceConfiguration;
import io.opencensus.exporter.trace.stackdriver.StackdriverTraceExporter;
import io.opencensus.implcore.trace.propagation.TraceContextFormat;
import io.opencensus.trace.SpanContext;
import io.opencensus.trace.Tracing;
import io.opencensus.trace.propagation.SpanContextParseException;
import io.opencensus.trace.propagation.TextFormat;
import io.opencensus.trace.samplers.Samplers;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author koduki
 */
public class DistributedTracer {

    private final Logger logger = Logger.getLogger("dev.nklab.jl2.profile");

    private static final TextFormat TEXT_FORMAT = Tracing.getPropagationComponent().getTraceContextFormat();
    private static final TextFormat.Getter<HttpServletRequest> GETTER = new TextFormat.Getter<HttpServletRequest>() {
        @Override
        public String get(HttpServletRequest httpRequest, String s) {
            return httpRequest.getHeader(s);
        }
    };
    private static final TextFormat.Setter<Map<String, String>> SETTER = new TextFormat.Setter<Map<String, String>>() {
        @Override
        public void put(Map<String, String> carrier, String key, String value) {
            carrier.put(key, value);
        }
    };

    private boolean isTrace = true;

    private DistributedTracer() {

    }

    public Optional<String> getTraceparent() {
        if (isTrace) {
            var traceContextFormat = new TraceContextFormat();

            var current = Tracing.getTracer().getCurrentSpan().getContext();
            var carrier = new LinkedHashMap<String, String>();
            traceContextFormat.inject(SpanContext.create(current.getTraceId(), current.getSpanId(), current.getTraceOptions(), current.getTracestate()),
                    carrier,
                    SETTER);
            var traceparent = carrier.get("traceparent");

            return Optional.of(traceparent);
        } else {
            return Optional.empty();
        }
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
            logger.info("init", $("msg", "GCP Cloud Tracing"));
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
        if (isTrace) {
            try {
                if (request.getHeader("traceparent") != null) {
                    var spanContext = TEXT_FORMAT.extract(request, GETTER);
                    try ( var ss = Tracing.getTracer()
                            .spanBuilderWithRemoteParent(name, spanContext)
                            .setRecordEvents(true)
                            .setSampler(Samplers.alwaysSample())
                            .startScopedSpan()) {
                        return callback.get();
                    }
                } else {
                    return apply(name, callback);
                }
            } catch (SpanContextParseException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            return callback.get();
        }
    }
}
