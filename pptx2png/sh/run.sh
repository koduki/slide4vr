#!/bin/bash

DIR=$1

gcloud auth activate-service-account --key-file $GOOGLE_APPLICATION_CREDENTIALS
gsutil cp gs://slide2vr-pptx/slide.pptx ./slide.pptx

pptx2png ./slide.pptx

gsutil -m cp -R dist/* gs://slide2vr-slides/${DIR}/