#!/bin/bash

args=$1
list=(${args//,/ })
PPTX_NAME=${list[0]}
PNG_DIR=${list[1]}

echo "PPTX_NAME: "$PPTX_NAME
echo "PNG_DIR: "$PNG_DIR

gcloud auth activate-service-account --key-file $GOOGLE_APPLICATION_CREDENTIALS
gsutil cp gs://slide2vr-pptx/${PPTX_NAME} ./slide.pptx

pptx2png ./slide.pptx

gsutil -m cp -R dist/* gs://slide2vr-slides/${PNG_DIR}/