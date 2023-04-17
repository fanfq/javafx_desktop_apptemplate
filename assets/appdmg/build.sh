#!/bin/bash

TOP_DIR=$(pwd)
echo "----TOP_DIR----"
echo $TOP_DIR

SRC_DIR=${TOP_DIR}/AutoLaunchMain
PRODUCT_NAME=AutoLaunchMain

CONFIGURATION=Release
BUILD_SCHEME=${PRODUCT_NAME}_dmg

# 使用workspace管理的项目
#CLEAN_COMMAND="xcodebuild -workspace ${PRODUCT_NAME}.xcworkspace -scheme ${BUILD_SCHEME} -configuration ${CONFIGURATION} -sdk macosx"
#BUILD_COMMAND="xcodebuild -workspace ${PRODUCT_NAME}.xcworkspace -scheme ${BUILD_SCHEME} -configuration ${CONFIGURATION} -sdk macosx"

# 通过xcodeproj管理的项目
CLEAN_COMMAND="xcodebuild -project ${PRODUCT_NAME}.xcodeproj -scheme ${BUILD_SCHEME} -configuration ${CONFIGURATION} -sdk macosx"
BUILD_COMMAND="xcodebuild -project ${PRODUCT_NAME}.xcodeproj -scheme ${BUILD_SCHEME} -configuration ${CONFIGURATION} -sdk macosx"

$CLEAN_COMMAND clean
$BUILD_COMMAND

echo "Build succeed"