LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := libfunction
LOCAL_MODULE_FILENAME := libfunction

LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog -lz -lc \
                -lutils -lcutils -lnativehelper

LOCAL_WHOLE_STATIC_LIBRARIES := tools entry

#LOCAL_SHARED_LIBRARIES := libnativehelper libcutils libutils

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
                    $(LOCAL_PATH)/../../header/tools \
                    $(LOCAL_PATH)/../../prebuilt/ffmpeg/include

#LOCAL_CFLAGS += -O0 -g
#libandroid_runtime \
# Define FM_AUDIO_PATH for configuring FM audio path.
# Valid values includes 0(ROUTE_NONE),1(ROUTE_DAC),2(ROUTE_I2S).
# If the flag is not defined here 1(ROUTE_DAC) will be taken as default.
#LOCAL_CFLAGS += -DFM_AUDIO_PATH=1

LOCAL_WHOLE_STATIC_LIBRARIES += avutil avformat avcodec swresample avfilter swscale postproc avdevice

include $(BUILD_SHARED_LIBRARY)