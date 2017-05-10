//
// Created by ruiqianqi on 2016/9/27 0027.
//
#include "com_zccl_ruiqianqi_tools_jni_NdkTools.h"
#include "function.h"
#include "dpp_config.h"
#include "public.h"
#include "MyConfig.h"

#undef TAG
#define TAG "main.cpp"

#ifdef __cplusplus
extern "C" {
#endif

void Java_com_zccl_ruiqianqi_tools_jni_NdkTools_bridgeInit
        (JNIEnv *env, jclass jObj, jobject jBridgeObj, jstring jMethodName) {

    const char *methodName = env->GetStringUTFChars(jMethodName, NULL);
    env->DeleteLocalRef(jMethodName);

    jclass jBridgeClazz = env->GetObjectClass(jBridgeObj);
    //构造一个全局引用
    CONFIG->jBridgeObj = env->NewGlobalRef(jBridgeObj);
    //拿到JAVA层对象的ID
    CONFIG->jBridgeID = env->GetMethodID(jBridgeClazz, methodName, "(Ljava/lang/String;)V");

    free((char *) methodName);
    env->DeleteLocalRef(jBridgeClazz);
}

/*
 * Class:     com_zccl_ruiqianqi_tools_jni_NdkTools
 * Method:    getABI
 * Signature: ()Ljava/lang/String;
 */
jstring Java_com_zccl_ruiqianqi_tools_jni_NdkTools_getABI
        (JNIEnv *env, jclass) {

#if defined(__arm__)
#if defined(__ARM_ARCH_7A__)
#if defined(__ARM_NEON__)
#if defined(__ARM_PCS_VFP)
#define ABI "armeabi-v7a/NEON (hard-float)"
#else
#define ABI "armeabi-v7a/NEON"
#endif
#else
#if defined(__ARM_PCS_VFP)
#define ABI "armeabi-v7a (hard-float)"
#else
#define ABI "armeabi-v7a"
#endif
#endif
#else
#define ABI "armeabi"
#endif
#elif defined(__i386__)
#define ABI "x86"
#elif defined(__x86_64__)
#define ABI "x86_64"
#elif defined(__mips64)  /* mips64el-* toolchain defines __mips__ too */
#define ABI "mips64"
#elif defined(__mips__)
#define ABI "mips"
#elif defined(__aarch64__)
#define ABI "arm64-v8a"
#else
#define ABI "unknown"
#endif

    return env->NewStringUTF("Compiled with ABI " ABI ".");
}

/*
 * Class:     com_zccl_ruiqianqi_tools_jni_NdkTools
 * Method:    print
 * Signature: ()V
 */
void Java_com_zccl_ruiqianqi_tools_jni_NdkTools_print
        (JNIEnv *env, jclass jObj) {
    return printMsg();
}

/*
 * Class:     com_zccl_ruiqianqi_tools_jni_NdkTools
 * Method:    reset5Mic
 * Signature: ()I
 */
jint Java_com_zccl_ruiqianqi_tools_jni_NdkTools_reset5Mic
        (JNIEnv *env, jclass jObj) {
    return reset5Mic();
}

/*
 * Class:     com_zccl_ruiqianqi_tools_jni_NdkTools
 * Method:    HdmiPowerOFF
 * Signature: ()I
 */
jint Java_com_zccl_ruiqianqi_tools_jni_NdkTools_HdmiPowerOFF
        (JNIEnv *, jclass) {
    int ret;
    char buf[3];
    buf[0]='D';

    int fd=open("/dev/hdmi_ctl",O_RDWR);
    ret=write(fd,buf,sizeof(buf));
    close(fd);

    return ret;
}

/*
 * Class:     com_zccl_ruiqianqi_tools_jni_NdkTools
 * Method:    HdmiPowerON
 * Signature: ()I
 */
jint Java_com_zccl_ruiqianqi_tools_jni_NdkTools_HdmiPowerON
        (JNIEnv *, jclass) {
    int ret;
    struct dpp_config dpp3438;

    char buf[3];
    buf[0]='C';

    int fd=open("/dev/hdmi_ctl",O_RDWR);
    ret=write(fd,buf,sizeof(buf));
    close(fd);
    return ret;
}

/*
 * Class:     com_zccl_ruiqianqi_tools_jni_NdkTools
 * Method:    HdmiSwitchInternal
 * Signature: (I)I
 */
jint Java_com_zccl_ruiqianqi_tools_jni_NdkTools_HdmiSwitchInternal
        (JNIEnv *, jclass, jint arg) {
    int ret;
    char buf[3];
    buf[0]='A';
    buf[1]=arg;
    if(arg == 0)
    {
        int fd=open("/dev/hdmi_ctl",O_RDWR);
        ret=write(fd,buf,sizeof(buf));
        close(fd);
        return ret;
    }
    return -1;
}

/*
 * Class:     com_zccl_ruiqianqi_tools_jni_NdkTools
 * Method:    HdmiSwitchExternal
 * Signature: (I)I
 */
jint Java_com_zccl_ruiqianqi_tools_jni_NdkTools_HdmiSwitchExternal
        (JNIEnv *, jclass, jint arg) {
    int ret;
    char buf[3];
    buf[0]='B';
    buf[1]=arg;
    if(arg ==1)
    {
        int fd=open("/dev/hdmi_ctl",O_RDWR);
        ret=write(fd,buf,sizeof(buf));
        close(fd);
        return ret;
    }
    return -1;
}

/*
 * Class:     com_zccl_ruiqianqi_tools_jni_NdkTools
 * Method:    HdmiDppPowerON
 * Signature: ()I
 */
jint Java_com_zccl_ruiqianqi_tools_jni_NdkTools_HdmiDppPowerON
        (JNIEnv *, jclass) {
    int ret;
    int fd=open("/dev/hdmi_ctl",O_RDWR);
    ret=ioctl(fd,DPP3438_POWER_ON,NULL);
    close(fd);
    return ret;
}

/*
 * Class:     com_zccl_ruiqianqi_tools_jni_NdkTools
 * Method:    HdmiDppPowerOFF
 * Signature: ()I
 */
jint Java_com_zccl_ruiqianqi_tools_jni_NdkTools_HdmiDppPowerOFF
        (JNIEnv *, jclass) {
    int ret;
    int fd=open("/dev/hdmi_ctl",O_RDWR);
    ret=ioctl(fd,DPP3438_POWER_OFF,NULL);
    close(fd);
    return ret;
}

/*
 * Class:     com_zccl_ruiqianqi_tools_jni_NdkTools
 * Method:    HdmiDppPowerRate
 * Signature: (I)I
 */
jint Java_com_zccl_ruiqianqi_tools_jni_NdkTools_HdmiDppPowerRate
        (JNIEnv *, jclass, jint arg) {
    int ret;
    struct dpp_config dpp3438;
    dpp3438.power_rate=arg;
    int fd=open("/dev/hdmi_ctl",O_RDWR);
    ret=ioctl(fd,DPP3438_POWER_RATE,&dpp3438);
    close(fd);
    return ret;
}

/*
 * Class:     com_zccl_ruiqianqi_tools_jni_NdkTools
 * Method:    HdmiDppConfig
 * Signature: (IIIIII)I
 */
jint Java_com_zccl_ruiqianqi_tools_jni_NdkTools_HdmiDppConfig
        (JNIEnv *, jclass, jint l_throw,jint m_throw,jint l_DMD,jint m_DMD,jint l_PP,jint m_PP) {
    int ret;
    struct dpp_config dpp3438;

    dpp3438.l_throw=l_throw;
    dpp3438.m_throw=m_throw;
    dpp3438.l_DMD=l_DMD;
    dpp3438.m_DMD=m_DMD;
    dpp3438.l_PP=l_PP;
    dpp3438.m_PP=m_PP;

    int fd=open("/dev/hdmi_ctl",O_RDWR);
    ret=ioctl(fd,DPP3438_CORRECT,&dpp3438);
    close(fd);
    return ret;
}

/*
 * Class:     com_zccl_ruiqianqi_tools_jni_NdkTools
 * Method:    StmPinCtl
 * Signature: (II)I
 */
jint Java_com_zccl_ruiqianqi_tools_jni_NdkTools_StmPinCtl
        (JNIEnv *, jclass, jint pin, jint val) {
    struct stm_config stm_ctl;
    int ret;
    stm_ctl.pin=pin;
    stm_ctl.val=val;

    int fd=open("/dev/hdmi_ctl",O_RDWR);
    ret=ioctl(fd,STM_POWER_CTL,&stm_ctl);
    close(fd);

    return ret;
}

/*
 * Class:     com_zccl_ruiqianqi_tools_jni_NdkTools
 * Method:    SystemBootUp
 * Signature: (I)I
 */
jint Java_com_zccl_ruiqianqi_tools_jni_NdkTools_SystemBootUp
        (JNIEnv *, jclass, jint arg) {
    int ret;
    char buf[3];
    buf[0] = 'E';
    int fd = 0;
    if (arg == 1) {
        buf[1] = 'D';
        fd = open("/dev/hdmi_ctl", O_RDWR);
        ret = write(fd, buf, sizeof(buf));
        close(fd);
        return ret;
    }
    else if (arg == 0) {
        buf[1] = 'E';
        fd = open("/dev/hdmi_ctl", O_RDWR);
        ret = write(fd, buf, sizeof(buf));
        close(fd);
        return ret;
    }
    return -1;
}

/*
 * Class:     com_zccl_ruiqianqi_tools_jni_NdkTools
 * Method:    HdmiDppPowerStatus
 * Signature: ()I
 */
jint Java_com_zccl_ruiqianqi_tools_jni_NdkTools_HdmiDppPowerStatus
        (JNIEnv *, jclass) {
    int ret;
    int power_status = 0;

    int fd = open("/dev/hdmi_ctl", O_RDWR);
    ret = ioctl(fd, DPP3438_STATUS, &power_status);
    close(fd);

    return power_status & 0x1;
}

/*
 * Class:     com_zccl_ruiqianqi_tools_jni_NdkTools
 * Method:    ProjectionStatus
 * Signature: ()I
 */
jint Java_com_zccl_ruiqianqi_tools_jni_NdkTools_ProjectionStatus
        (JNIEnv *, jclass) {
    int ret;
    int pro_status = 0;

    int fd = open("/dev/hdmi_ctl", O_RDWR);
    ret = ioctl(fd, DPP3438_STATUS, &pro_status);
    close(fd);

    return (pro_status & 0x2) ? 1 : 0;
}

/*
 * Class:     com_zccl_ruiqianqi_tools_jni_NdkTools
 * Method:    CheckAcCharger
 * Signature: ()I
 */
jint Java_com_zccl_ruiqianqi_tools_jni_NdkTools_CheckAcCharger
        (JNIEnv *, jclass) {
    int ret = 0;
    int chargertype = 0;

    int fd = open("/dev/hdmi_ctl", O_RDWR);
    ret = ioctl(fd, CHECK_CHARGER_TYPE, &chargertype);
    close(fd);

    return chargertype;
}

/*
 * Class:     com_zccl_ruiqianqi_tools_jni_NdkTools
 * Method:    GsensorDataSwitch
 * Signature: (I)I
 */
jint Java_com_zccl_ruiqianqi_tools_jni_NdkTools_GsensorDataSwitch
        (JNIEnv *env, jclass clazz, jint sw) {
    int ret;
    char buf[3];
    buf[0] = 'E';
    if (sw == 1)
        buf[1] = 'H';
    else
        buf[1] = 'G';

    int fd = open("/dev/hdmi_ctl", O_RDWR);
    ret = write(fd, buf, sizeof(buf));
    close(fd);
    return ret;
}

/*
 * Class:     com_zccl_ruiqianqi_tools_jni_NdkTools
 * Method:    GetGsensorData
 * Signature: ([I)I
 */
jint Java_com_zccl_ruiqianqi_tools_jni_NdkTools_GetGsensorData
        (JNIEnv *env, jclass clazz, jintArray gdata) {
    int ret, i;
    int buf[3];
    jint *carr;
    carr = env->GetIntArrayElements(gdata, NULL);
    if (carr == NULL) {
        return 0; /* exception occurred */
    }

    int fd = open("/dev/hdmi_ctl", O_RDWR);
    ret = ioctl(fd, READ_GSENSOR_DATA, buf);
    close(fd);

    for (i = 0; i < 3; i++) {
        carr[i] = buf[i];
    }

    env->ReleaseIntArrayElements(gdata, carr, 0);
    return ret;
}

#ifdef __cplusplus
}
#endif
