LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := libentry
LOCAL_MODULE_FILENAME := entry

LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog -lz -lc

# 自定义变量的使用
ZCCL_SRC_PATH := $(LOCAL_PATH)
# wildcard : 扩展通配符，用于查找一个目录下的所有符合条件的文件
ZCCL_CPP_LIST :=$(wildcard $(ZCCL_SRC_PATH)/*.cpp)
ZCCL_CPP_LIST +=$(wildcard $(ZCCL_SRC_PATH)/*.c)
#ZCCL_CPP_LIST +=$(wildcard $(ZCCL_SRC_PATH)/*.s)


# 编译源文件列表
LOCAL_SRC_FILES := $(ZCCL_CPP_LIST:$(LOCAL_PATH)/%=%)
#$(warning $(LOCAL_SRC_FILES))
#$(info $(LOCAL_SRC_FILES))


# 编译相关的头文件目录
LOCAL_C_INCLUDES := $(LOCAL_PATH) \
                    $(LOCAL_PATH)/../../header \
                    $(LOCAL_PATH)/../../header/function \
                    $(LOCAL_PATH)/../../header/tools

LOCAL_CFLAGS += -std=c11
LOCAL_CPPFLAGS := -std=c++11 -frtti -fexceptions
ifeq ($(NDK_DEBUG),1)
  	LOCAL_CFLAGS +=  $(CFLAGS)
	LOCAL_CPPFLAGS += $(CPPFLAGS)
  	LOCAL_CXXFLAGS += $(CPPFLAGS)
endif
LOCAL_ALLOW_UNDEFINED_SYMBOLS :=true
ifeq ($(TARGET_ARCH_ABI), armeabi-v7a)
    LOCAL_ARM_NEON :=true
endif

#include $(BUILD_STATIC_LIBRARY)