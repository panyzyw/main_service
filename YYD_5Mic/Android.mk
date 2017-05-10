LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_PACKAGE_NAME := YYDRobotVoiceMainService
LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, java)
LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/res
LOCAL_ASSET_DIR := $(LOCAL_PATH)/assets
#LOCAL_CERTIFICATE := vendor/yongyida/cert/master/masterKey

LOCAL_PROGUARD_ENABLED := disabled

LOCAL_STATIC_JAVA_LIBRARIES += \
	android-support-v4 \
	libservice1 \
	libservice2 \
	libservice3 \
	libservice4 \
	libservice5 \
	libservice6 \
	libservice7 \
	libservice8 \
	libservice9

LOCAL_JNI_SHARED_LIBRARIES += \
	libalsa-jni \
	libcae_release \
	libyydctl \
	libmsc \
	libYYDNDK \
	libmictest

LOCAL_MULTILIB := 32
LOCAL_PREBUILT_JNI_LIBS:= libs/armeabi/libcae.so
	
include $(BUILD_PACKAGE)

include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
	libservice1:libs/alsarecorder.jar \
	libservice2:libs/Cae.jar \
	libservice3:libs/gson-2.2.4.jar \
	libservice4:libs/MotorService.jar \
	libservice5:libs/Msc.jar \
	libservice6:libs/netty-3.10.5.Final.jar \
	libservice7:libs/servlet-api.jar \
	libservice8:libs/xUtils-2.6.14.jar \
	libservice9:libs/RobotIDHelper.jar
	
include $(BUILD_MULTI_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE := libalsa-jni
LOCAL_SRC_FILES_32 := libs/armeabi/libalsa-jni.so
LOCAL_MULTILIB := 32
LOCAL_MODULE_CLASS := SHARED_LIBRARIES
LOCAL_MODULE_SUFFIX := .so
include $(BUILD_PREBUILT)



include $(CLEAR_VARS)
LOCAL_MODULE := libcae_release
LOCAL_SRC_FILES_32 := libs/armeabi/libcae_release.so
LOCAL_MULTILIB := 32
LOCAL_MODULE_CLASS := SHARED_LIBRARIES
LOCAL_MODULE_SUFFIX := .so
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE := libyydctl
LOCAL_SRC_FILES_32 := libs/armeabi/libyydctl.so
LOCAL_MULTILIB := 32
LOCAL_MODULE_CLASS := SHARED_LIBRARIES
LOCAL_MODULE_SUFFIX := .so
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE := libmsc
LOCAL_SRC_FILES_32 := libs/armeabi/libmsc.so
LOCAL_MULTILIB := 32
LOCAL_MODULE_CLASS := SHARED_LIBRARIES
LOCAL_MODULE_SUFFIX := .so
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE := libYYDNDK
LOCAL_SRC_FILES_32 := libs/armeabi/libYYDNDK.so
LOCAL_MULTILIB := 32
LOCAL_MODULE_CLASS := SHARED_LIBRARIES
LOCAL_MODULE_SUFFIX := .so
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE := libmictest
LOCAL_SRC_FILES_32 := libs/armeabi/libmictest.so
LOCAL_MULTILIB := 32
LOCAL_MODULE_CLASS := SHARED_LIBRARIES
LOCAL_MODULE_SUFFIX := .so
include $(BUILD_PREBUILT)
