/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.nklab.jl2.web.profile;

import static dev.nklab.jl2.Extentions.*;
import dev.nklab.jl2.web.logging.Logger;
import io.opencensus.common.Scope;
import io.opencensus.contrib.http.util.HttpTraceAttributeConstants;
import io.opencensus.exporter.trace.stackdriver.StackdriverTraceConfiguration;
import io.opencensus.exporter.trace.stackdriver.StackdriverTraceExporter;
import io.opencensus.implcore.trace.propagation.TraceContextFormat;
import io.opencensus.trace.AttributeValue;
import io.opencensus.trace.Span;
import io.opencensus.trace.SpanBuilder;
import io.opencensus.trace.SpanContext;
import io.opencensus.trace.Tracing;
import io.opencensus.trace.propagation.SpanContextParseException;
import io.opencensus.trace.propagation.TextFormat;
import io.opencensus.trace.samplers.Samplers;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
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

    public <T, R> R apply(String name, Function<Optional<Span>, R> callback) {
        if (isTrace) {
            try ( var ss = Tracing.getTracer()
                    .spanBuilder(name)
                    .setRecordEvents(true)
                    .setSampler(Samplers.alwaysSample())
                    .startScopedSpan()) {
                return callback.apply(Optional.of(Tracing.getTracer().getCurrentSpan()));
            }
        } else {
            return callback.apply(Optional.empty());
        }
    }

    public <T, R> R apply(String name, HttpServletRequest request, Function<Optional<Span>, R> callback) {
        var host = request.getLocalName() + ":" + request.getLocalPort();
        var method = request.getMethod();
        var path = request.getContextPath();

        return apply(name, request, host, method, path, callback);
    }

    public <T, R> R apply(String name, HttpServletRequest request, String host, String method, String path, Function<Optional<Span>, R> callback) throws RuntimeException {
        if (isTrace) {
            try ( var ss = startScopedSpan(name, request)) {
                var span = Tracing.getTracer().getCurrentSpan();
                span.putAttribute(HttpTraceAttributeConstants.HTTP_HOST, AttributeValue.stringAttributeValue(host));
                span.putAttribute(HttpTraceAttributeConstants.HTTP_METHOD, AttributeValue.stringAttributeValue(method));
                span.putAttribute(HttpTraceAttributeConstants.HTTP_PATH, AttributeValue.stringAttributeValue(path));

                return callback.apply(Optional.of(span));
            }
        } else {
            return callback.apply(Optional.empty());
        }
    }

    Scope startScopedSpan(String name, HttpServletRequest request) {
        try {
            SpanBuilder builder;
            if (request.getHeader("traceparent") != null) {
                var spanContext = TEXT_FORMAT.extract(request, GETTER);
                builder = Tracing.getTracer().spanBuilderWithRemoteParent(name, spanContext);
            } else {
                builder = Tracing.getTracer().spanBuilder(name);
            }

            return builder.setRecordEvents(true)
                    .setSampler(Samplers.alwaysSample())
                    .startScopedSpan();

        } catch (SpanContextParseException ex) {
            throw new RuntimeException(ex);
        }
    }
}
