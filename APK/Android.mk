LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := YYDRobotVoiceMainService
LOCAL_MODULE_TAGS := optional	
 
LOCAL_SRC_FILES := $(WAKE_UP_NAME).apk
LOCAL_MODULE_CLASS := APPS
LOCAL_MODULE_SUFFIX := $(COMMON_ANDROID_PACKAGE_SUFFIX)

LOCAL_MULTILIB :=32
LOCAL_PREBUILT_JNI_LIBS:= \
  @lib/armeabi-v7a/libalsa-jni.so\
  @lib/armeabi-v7a/libBugly.so \
  @lib/armeabi-v7a/libcae.so \
  @lib/armeabi-v7a/libhdmictl.so \
  @lib/armeabi-v7a/libmictest.so \
  @lib/armeabi-v7a/libmsc.so 
  
LOCAL_CERTIFICATE := platform
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE := libalsa-jni
LOCAL_SRC_FILES_32 := lib/armeabi-v7a/libalsa-jni.so
LOCAL_MULTILIB := 32
LOCAL_MODULE_CLASS := SHARED_LIBRARIES
LOCAL_MODULE_SUFFIX := .so
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE := libcae
LOCAL_SRC_FILES_32 := lib/armeabi-v7a/libcae.so
LOCAL_MULTILIB := 32
LOCAL_MODULE_CLASS := SHARED_LIBRARIES
LOCAL_MODULE_SUFFIX := .so
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE := libhdmictl
LOCAL_SRC_FILES_32 := libs/armeabi/libhdmictl.so
LOCAL_MULTILIB := 32
LOCAL_MODULE_CLASS := SHARED_LIBRARIES
LOCAL_MODULE_SUFFIX := .so
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE := libmsc
LOCAL_SRC_FILES_32 := lib/armeabi-v7a/libmsc.so
LOCAL_MULTILIB := 32
LOCAL_MODULE_CLASS := SHARED_LIBRARIES
LOCAL_MODULE_SUFFIX := .so
include $(BUILD_PREBUILT)