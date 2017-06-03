LOCAL_PATH:= $(call my-dir)


include $(CLEAR_VARS)


LOCAL_PACKAGE_NAME := com.zccl.ruiqianqi.tools
LOCAL_PROGUARD_ENABLED := disabled


java_path := src/main/java
aidl_path := src/main/aidl
res_path := src/main/res
assets_path := src/main/assets

src_dirs := $(java_path) \
	$(aidl_path)
	
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


	
# 静态jar包
LOCAL_STATIC_JAVA_LIBRARIES := \
    android-support-v4 \
	android-support-v7-appcompat \
    android-support-v13 \
	rxjava \
	gson \
	okhttp \
	logging \
	retrofit \
	converter-scalars \
	converter-gson \
	converter-protobuf \
	adapter-rxjava
	
# 静态aar包
LOCAL_STATIC_JAVA_AAR_LIBRARIES := \
	rxandroid	

include $(BUILD_PACKAGE)
	

include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
    rxjava:src/main/libs/rxjava-1.1.8.jar \
    rxandroid:src/main/libs/rxandroid-1.2.1.aar \
	gson:src/main/libs/gson-2.8.0.jar \
	okhttp:src/main/libs/okhttp-3.6.0.jar \	
	logging:src/main/libs/logging-interceptor-3.6.0.jar \
	retrofit:src/main/libs/retrofit-2.1.0.jar \
	converter-scalars:src/main/libs/converter-scalars-2.1.0.jar \
	converter-gson:src/main/libs/converter-gson-2.1.0.jar \
	converter-protobuf:src/main/libs/adapter-rxjava-2.1.0.jar \
	adapter-rxjava:src/main/libs/converter-protobuf-2.1.0.jar
	
include $(BUILD_MULTI_PREBUILT)	


