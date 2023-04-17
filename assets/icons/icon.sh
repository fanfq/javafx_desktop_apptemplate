#!/bin/bash

echo "icon.png 1024*1024 resize [16*16 - 512*512] "

if [ ! -d png  ];then
  mkdir png
fi
if [ ! -d icons.iconset  ];then
  mkdir icons.iconset
fi

sips -z 16 16     icon.png --out icons.iconset/icon_16x16.png
sips -z 32 32     icon.png --out icons.iconset/icon_16x16@2x.png
sips -z 32 32     icon.png --out icons.iconset/icon_32x32.png
sips -z 64 64     icon.png --out icons.iconset/icon_32x32@2x.png
sips -z 64 64     icon.png --out icons.iconset/icon_64x64.png
sips -z 128 128   icon.png --out icons.iconset/icon_64x64@2x.png
sips -z 128 128   icon.png --out icons.iconset/icon_128x128.png
sips -z 256 256   icon.png --out icons.iconset/icon_128x128@2x.png
sips -z 256 256   icon.png --out icons.iconset/icon_256x256.png
sips -z 512 512   icon.png --out icons.iconset/icon_256x256@2x.png
sips -z 512 512   icon.png --out icons.iconset/icon_512x512.png
sips -z 1024 1024   icon.png --out icons.iconset/icon_512x512@2x.png

echo "create icon.icns"
iconutil -c icns icons.iconset -o icon.icns

sips -z 16 16     icon.png --out png/icon_16x16.png
sips -z 32 32     icon.png --out png/icon_32x32.png
sips -z 64 64     icon.png --out png/icon_64x64.png
sips -z 128 128   icon.png --out png/icon_128x128.png
sips -z 256 256   icon.png --out png/icon_256x256.png
sips -z 512 512   icon.png --out png/icon_512x512.png
sips -z 512 512   icon.png --out png/icon.png

echo "create ico"
if [ ! -d ico  ];then
  mkdir ico
fi
icotool -c -o ico/icon_16x16.ico png/icon_16x16.png
icotool -c -o ico/icon_32x32.ico png/icon_32x32.png
icotool -c -o ico/icon_64x64.ico png/icon_64x64.png
icotool -c -o ico/icon_128x128.ico png/icon_128x128.png
icotool -c -o ico/icon_256x256.ico png/icon_256x256.png
icotool -c -o ico/icon_512x512.ico png/icon_512x512.png
icotool -c -o ico/icon.ico png/icon_512x512.png


