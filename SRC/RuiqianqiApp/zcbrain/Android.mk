LOCAL_PATH:= $(call my-dir)


include $(CLEAR_VARS)


LOCAL_PACKAGE_NAME := YYDRobotVoiceMainService
LOCAL_PROGUARD_ENABLED := disabled
LOCAL_MODULE_TAGS := optional
LOCAL_CERTIFICATE := platform


flytek_dir := ../flytek
drive_dir := ../drive
socket_dir := ../socket
zcui_dir := ../zcui


java_path := src/main/java
aidl_path := src/main/aidl
res_path := src/main/res
assets_path := src/main/assets


src_dirs := $(java_path) \
	$(aidl_path) \
    $(flytek_dir)/$(java_path) \
	$(flytek_dir)/$(aidl_path) \
    $(drive_dir)/$(java_path) \
	$(drive_dir)/$(aidl_path) \
	$(socket_dir)/$(java_path) \
	$(socket_dir)/$(aidl_path) \
	$(zcui_dir)/$(java_path) \
	$(zcui_dir)/$(aidl_path)

	
res_dirs := $(res_path) \
    $(flytek_dir)/$(res_path) \
    $(drive_dir)/$(res_path) \
    $(socket_dir)/$(res_path) \
	$(zcui_dir)/$(res_path)

	
assets_dirs := $(assets_path) \
    $(flytek_dir)/$(assets_path) \
    $(drive_dir)/$(assets_path) \
    $(socket_dir)/$(assets_path) \
	$(zcui_dir)/$(assets_path)


LOCAL_SRC_FILES := $(call all-java-files-under, $(src_dirs))
LOCAL_RESOURCE_DIR := $(addprefix $(LOCAL_PATH)/, $(res_dirs))
LOCAL_ASSET_DIR := $(addprefix $(LOCAL_PATH)/, $(assets_dirs))

LOCAL_SDK_VERSION := current

#使用指定目录下的manifest文件（如果不与mk文件在同一目录的话必须定义）
LOCAL_MANIFEST_FILE := src/main/AndroidManifest.xml


# APT生成的代码
ext_src_dirs := build/generated/source/apt/P_armv7/release \
	build/generated/source/buildConfig/P_armv7/release
LOCAL_SRC_FILES += $(call all-java-files-under, $(ext_src_dirs))


# 引用其他工程
LOCAL_AAPT_FLAGS := \
    --auto-add-overlay \
    --extra-packages com.zccl.ruiqianqi.mind.voice \
    --extra-packages com.zccl.ruiqianqi.move \
    --extra-packages com.zccl.ruiqianqi.data.socket \
	--extra-packages com.zccl.ruiqianqi.zcui

	
# 静态jar包
LOCAL_STATIC_JAVA_LIBRARIES := \
	RobotIDHelper \
	butterknife \
	dbflow \
	dbflow-core \
	leakcanary-android-no-op
	


# 静态aar包
#LOCAL_STATIC_JAVA_AAR_LIBRARIES := \
#	butterknife \
#	dbflow \
#	leakcanary-android-no-op

include $(BUILD_PACKAGE)
	

include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
    RobotIDHelper:src/main/libs/RobotIDHelper.jar \
	butterknife:src/main/libs/butterknife-8.5.1.jar \
	dbflow:src/main/libs/dbflow-3.1.1.jar \
    dbflow-core:src/main/libs/dbflow-core-3.1.1.jar \
    leakcanary-android-no-op:src/main/libs/leakcanary-android-no-op-1.3.1.jar
include $(BUILD_MULTI_PREBUILT)	


include $(call all-makefiles-under,$(LOCAL_PATH))
