#include "function.h"
#include "dpp_config.h"
#include "public.h"

#undef TAG
#define TAG "function.cpp"


/**
 * 返回底层接口类型
 */
const char * getABI(){
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
    return "Compiled with ABI " ABI ".";
}

/**
 * 打印点东西
 */
void printMsg(){
    LOGE("from function.cpp");
}

/**
 * 检测五麦能否重置
 * @return 1:success 0:fail
 */
int reset5Mic(){
    char buf[3];
    buf[0] = 'E';
    buf[1] = 'F';
    int ret = 0;
    int fd = open("/dev/hdmi_ctl", O_RDWR);
    if(fd > 0){
        ret = write(fd, buf, sizeof(buf));
        close(fd);
    } else{
        fd = open("/dev/misc_yyd", O_RDWR);
        if(fd > 0){
            buf[0] = 'B';
            buf[1] = 'A';
            ret = write(fd, buf, sizeof(buf));
            close(fd);
        }
    }
    return ret;
}

