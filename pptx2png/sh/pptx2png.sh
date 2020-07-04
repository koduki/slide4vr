#!/bin/bash

# init
INPUT=arm.pptx
rm -rf dist
mkdir -p tmp dist

# convert
libreoffice --nolockcheck --nologo --headless --norestore --language=ja --nofirststartwizard --convert-to pdf --outdir tmp $INPUT
pdftoppm tmp/arm.pdf tmp/image
mogrify -format png tmp/image*

# cleanup
mv tmp/*.png dist/
rm -rf tmp/