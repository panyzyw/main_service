LOCAL_PATH:= $(call my-dir)


include $(CLEAR_VARS)


LOCAL_PACKAGE_NAME := com.zccl.ruiqianqi.mind.voice
LOCAL_PROGUARD_ENABLED := disabled

allinone_dir := ../allinone


java_path := src/main/java
aidl_path := src/main/aidl
res_path := src/main/res
assets_path := src/main/assets


src_dirs := $(java_path) \
	$(aidl_path) \
    $(allinone_dir)/$(java_path) \
	$(allinone_dir)/$(aidl_path) 

	
res_dirs := $(res_path) \
    $(allinone_dir)/$(res_path) 

	
assets_dirs := $(assets_path) \
    $(allinone_dir)/$(assets_path) 


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
    --extra-packages com.zccl.ruiqianqi.mind.voice.allinone 

	
# 静态jar包
LOCAL_STATIC_JAVA_LIBRARIES := \
    android-support-v4 \
	android-support-v7-appcompat \
    android-support-v13 \
	rxjava
	

# 静态aar包
LOCAL_STATIC_JAVA_AAR_LIBRARIES := \
	rxandroid
	

include $(BUILD_PACKAGE)
	

include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
    rxjava:src/main/libs/rxjava-1.1.8.jar \
    rxandroid:src/main/libs/rxandroid-1.2.1.aar

	
include $(BUILD_MULTI_PREBUILT)	
