LOCAL_PATH:= $(call my-dir)


include $(CLEAR_VARS)


LOCAL_PACKAGE_NAME := com.zccl.ruiqianqi.zcui
# 关闭混淆
LOCAL_PROGUARD_ENABLED := disabled
LOCAL_CERTIFICATE := platform


tools_dir := ../tools
plugin_dir := ../plugin


java_path := src/main/java
aidl_path := src/main/aidl
res_path := src/main/res
assets_path := src/main/assets


src_dirs := $(java_path) \
	$(aidl_path) \
    $(tools_dir)/$(java_path) \
	$(tools_dir)/$(aidl_path) \
    $(plugin_dir)/$(java_path) 

	
res_dirs := $(res_path) \
    $(tools_dir)/$(res_path)

	
assets_dirs := $(assets_path) \
    $(tools_dir)/$(assets_path)


LOCAL_SRC_FILES := $(call all-java-files-under, $(src_dirs))

# 资源的引用
LOCAL_RESOURCE_DIR := $(addprefix $(LOCAL_PATH)/, $(res_dirs)) \
    frameworks/support/design/res \
	frameworks/support/v7/appcompat/res \
	frameworks/support/v7/preference/res \
	frameworks/support/v14/preference/res
			
LOCAL_ASSET_DIR := $(addprefix $(LOCAL_PATH)/, $(assets_dirs))


#使用指定目录下的manifest文件（如果不与mk文件在同一目录的话必须定义）
LOCAL_MANIFEST_FILE := src/main/AndroidManifest.xml


# APT生成的代码
ext_src_dirs := build/generated/source/apt/P_armv7/release \
	build/generated/source/buildConfig/P_armv7/release
LOCAL_SRC_FILES += $(call all-java-files-under, $(ext_src_dirs))


# 引用其他工程，资源的编译
LOCAL_AAPT_FLAGS := \
    --auto-add-overlay \
	--extra-packages android.support.design \
	--extra-packages android.support.v7.appcompat \
    --extra-packages android.support.v7.preference \
	--extra-packages android.support.v14.preference \
    --extra-packages com.zccl.ruiqianqi.tools 

	
# 静态jar包，源代码的引用
LOCAL_STATIC_JAVA_LIBRARIES := \
	android-support-annotations \
	android-support-multidex \
    android-support-v4 \
	android-support-v13 \
	android-support-design \
	android-support-v7-appcompat \
	android-support-v7-preference \
	android-support-v14-preference \
	glide \
	fragmentargs \
	eventbus



# 静态aar包
LOCAL_STATIC_JAVA_AAR_LIBRARIES := \
	butterknife \
	crashreport_upgrade \
	nativecrashreport \
	rxbinding \
	rxbinding-appcompat-v7 \
	rxbinding-design \
	rxlifecycle \
	rxlifecycle-components
	

include $(BUILD_PACKAGE)
	

include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
	glide:src/main/libs/glide-3.7.0.jar \
	fragmentargs:src/main/libs/annotation-3.0.2.jar \
	eventbus:src/main/libs/eventbus-3.0.0.jar \
    butterknife:src/main/libs/butterknife-8.5.1.aar \
    crashreport_upgrade:src/main/libs/crashreport_upgrade-1.2.5.aar \
    nativecrashreport:src/main/libs/nativecrashreport-3.1.2.aar \
	rxbinding:src/main/libs/rxbinding-0.4.0.aar \
	rxbinding-appcompat-v7:src/main/libs/rxbinding-appcompat-v7-0.4.0.aar \
	rxbinding-design:src/main/libs/rxbinding-design-0.4.0.aar \
	rxlifecycle:src/main/libs/rxlifecycle-0.6.1.aar \
	rxlifecycle-components:src/main/libs/rxlifecycle-components-0.6.1.aar
include $(BUILD_MULTI_PREBUILT)	

include $(call all-makefiles-under,$(LOCAL_PATH))
