# README

## deploy proxy

```bash
gcloud run deploy endpoint-pptx2png \
    --region us-central1 \
    --image="gcr.io/endpoints-release/endpoints-runtime-serverless:2" \
    --allow-unauthenticated \
    --platform managed 
```

```bash
gcloud run deploy endpoint-pptx2png \
    --region us-central1 \
    --image="gcr.io/slide2vr/endpoints-runtime-serverless:2.14.0-endpoint-pptx2png-dnb6froqha-uc.a.run.app-2020-07-27r0" \
    --set-env-vars=ESPv2_ARGS=^++^--cors_preset=basic++--tracing_outgoing_context=traceparent++--tracing_incoming_context=traceparent++--tracing_sample_rate=1.0 \
    --allow-unauthenticated \
    --platform managed 
```
2020-07-27r0]

## Deploy OpenAPI 2 (Swagger2)

```bash
$ gcloud endpoints services deploy openapi-run-v2.yaml
```