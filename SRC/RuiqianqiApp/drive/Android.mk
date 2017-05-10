LOCAL_PATH:= $(call my-dir)


include $(CLEAR_VARS)


LOCAL_PACKAGE_NAME := com.zccl.ruiqianqi.move
LOCAL_PROGUARD_ENABLED := disabled


java_path := src/main/java
aidl_path := src/main/aidl
res_path := src/main/res
assets_path := src/main/assets

oem_ext_src_dirs := src/main/$(robot_ver)/control/java src/main/$(robot_ver)/move/java

src_dirs := $(java_path) \
	$(aidl_path) \
	oem_ext_src_dirs
	
res_dirs := $(res_path)
	
assets_dirs := $(assets_path) 
	
	
LOCAL_SRC_FILES := $(call all-java-files-under, $(src_dirs))
LOCAL_RESOURCE_DIR := $(addprefix $(LOCAL_PATH)/, $(res_dirs))
LOCAL_ASSET_DIR := $(addprefix $(LOCAL_PATH)/, $(assets_dirs))


#使用指定目录下的manifest文件（如果不与mk文件在同一目录的话必须定义）
LOCAL_MANIFEST_FILE := src/main/AndroidManifest.xml


# APT生成的代码
ext_src_dirs := build/generated/source/apt/P_armv7/release \
	build/generated/source/buildConfig/P_armv7/release
LOCAL_SRC_FILES += $(call all-java-files-under, $(ext_src_dirs))

	

# 引用系统动态库
LOCAL_JNI_SHARED_LIBRARIES += libhdmictl

	
# 静态jar包
LOCAL_STATIC_JAVA_LIBRARIES := \
    android-support-v4 \
	android-support-v7-appcompat \
    android-support-v13 \
	MotorService 
	

include $(BUILD_PACKAGE)
	

include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
    MotorService:src/main/$(robot_ver)/move/libs/MotorService.jar
	
include $(BUILD_MULTI_PREBUILT)	



include $(CLEAR_VARS)
LOCAL_MODULE := libhdmictl
LOCAL_SRC_FILES_32 := src/main/$(robot_ver)/control/libs/armeabi-v7a/libhdmictl.so
LOCAL_MULTILIB := 32
LOCAL_MODULE_CLASS := SHARED_LIBRARIES
LOCAL_MODULE_SUFFIX := .so
include $(BUILD_PREBUILT)

