#!/bin/bash

# init
INPUT=$1
rm -rf dist
mkdir -p tmp dist

# convert
pdftoppm $INPUT tmp/image
mogrify -format png tmp/image*

# cleanup
mv tmp/*.png dist/
rm -rf tmp/
