

## 快速生成Mac图标 .icns

创建 `icons.iconset` 文件夹用来放置不同尺寸的图标

注：此文件夹名必须以.iconset结尾，否则生成icns文件时会报错，报错信息为 `invalid iconset`

注：icon.png 替换为你的图片文件路径，尺寸要求为1024x1024

```shell
#!/bin/bash

echo "第一步 生成不同尺寸的png图标 icon.png 1024*1024 resize [16*16 - 512*512] "

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

echo "第二步 生成icns图标 create icon.icns"
iconutil -c icns icons.iconset -o icon.icns
```


## 生成Windows图标 .ico

```
echo "第一步 安装icoutils"
brew install icoutils

echo "第二步 生成ico图标"
icotool -c -o icon.ico icon.png


icotool -c -o ico/icon_16x16.ico icons.iconset/icon_16x16.png
icotool -c -o ico/icon_32x32.ico icons.iconset/icon_32x32.png
icotool -c -o ico/icon_64x64.ico icons.iconset/icon_64x64.png
icotool -c -o ico/icon_128x128.ico icons.iconset/icon_128x128.png
icotool -c -o ico/icon_256x256.ico icons.iconset/icon_256x256.png
icotool -c -o ico/icon_512x512.ico icons.iconset/icon_512x512.png
icotool -c -o ico/icon.ico icons.iconset/icon_512x512.png
```