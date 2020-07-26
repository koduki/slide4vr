#!/bin/bash

# init
INPUT=$1
rm -rf dist
mkdir -p tmp dist

# convert
libreoffice --nolockcheck --nologo --headless --norestore --language=ja --nofirststartwizard --convert-to pdf --outdir tmp $INPUT
pdftoppm $(ls -1 tmp/*.pdf) tmp/image
mogrify -format png tmp/image*

# cleanup
mv tmp/*.png dist/
rm -rf tmp/