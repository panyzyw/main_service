LOCAL_PATH:= $(call my-dir)

#ifeq ($(CUSTOMER_PRODUCT), y50bpro)
#	include $(LOCAL_PATH)/YYD_5Mic/Android.mk
#else if ($(CUSTOMER_PRODUCT), y20c)
#	include $(LOCAL_PATH)/YYD_1Mic/Android.mk
#endif

include $(LOCAL_PATH)/APK/Android.mk