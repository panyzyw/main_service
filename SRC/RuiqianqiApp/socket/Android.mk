LOCAL_PATH:= $(call my-dir)


include $(CLEAR_VARS)


LOCAL_PACKAGE_NAME := com.zccl.ruiqianqi.data.socket
LOCAL_PROGUARD_ENABLED := disabled

tools_dir := ../tools

java_path := src/main/java
aidl_path := src/main/aidl
res_path := src/main/res
assets_path := src/main/assets

oem_ext_src_dirs := src/main/ext_netty3/java

src_dirs := $(java_path) \
	$(aidl_path) \
	$(tools_dir)/$(java_path) \
	$(tools_dir)/$(aidl_path) \
	oem_ext_src_dirs	
	
res_dirs := $(res_path) \
	$(tools_dir)/$(res_path)

assets_dirs := $(assets_path) \
	$(tools_dir)/$(assets_path)
	
LOCAL_SRC_FILES := $(call all-java-files-under, $(src_dirs))
LOCAL_RESOURCE_DIR := $(addprefix $(LOCAL_PATH)/, $(res_dirs))
LOCAL_ASSET_DIR := $(addprefix $(LOCAL_PATH)/, $(assets_dirs))


#使用指定目录下的manifest文件（如果不与mk文件在同一目录的话必须定义）
LOCAL_MANIFEST_FILE := src/main/AndroidManifest.xml


# APT生成的代码
ext_src_dirs := build/generated/source/apt/P_armv7/release \
	build/generated/source/buildConfig/P_armv7/release
LOCAL_SRC_FILES += $(call all-java-files-under, $(ext_src_dirs))


# 引用其他工程
LOCAL_AAPT_FLAGS := \
    --auto-add-overlay \
    --extra-packages com.zccl.ruiqianqi.tools
	
	
# 静态jar包
LOCAL_STATIC_JAVA_LIBRARIES := \
    android-support-v4 \
	android-support-v7-appcompat \
    android-support-v13 \
	eventbus \
	gson \
	commons-io \
	netty

include $(BUILD_PACKAGE)
	

include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
    eventbus:src/main/libs/eventbus-3.0.0.jar \
	gson:src/main/libs/gson-2.8.0.jar \
	commons-io:src/main/libs/commons-io-2.5.jar \
	netty:src/main/ext_netty3/libs/gson-2.8.0.jar \
include $(BUILD_MULTI_PREBUILT)	


