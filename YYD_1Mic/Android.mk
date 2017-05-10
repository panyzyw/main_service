LOCAL_PATH:= $(call my-dir)

ifeq ($(YYD_5MIC_SUPPORT), yes)
	include $(LOCAL_PATH)/five.mk
else
	include $(LOCAL_PATH)/one.mk
endif