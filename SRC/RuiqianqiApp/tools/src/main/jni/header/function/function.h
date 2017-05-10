//
// Created by ruiqianqi on 2016/9/18 0018.
//

#ifndef JNI_FUNCTION_H_
#define JNI_FUNCTION_H_

#ifdef __cplusplus
extern "C" {
#endif

/**
 * 返回底层接口类型
 */
const char * getABI();

/**
 * 打印点东西
 */
void printMsg();

/**
 * 检测五麦能否重置
 * @return 0:fail
 *         1:success
 */
int reset5Mic();

#ifdef __cplusplus
};
#endif


#endif //JNI_FUNCTION_H_
