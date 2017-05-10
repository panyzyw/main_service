#include "test.h"
#include <stdio.h>
#include <stdlib.h>

#ifdef __cplusplus
extern "C"{
#endif

void test(){
#define MY_PRINTF_VERSION 1

#if MY_PRINTF_VERSION == 1
	#undef MY_PRINTF_VERSION
	#define MY_PRINTF_VERSION  2
#endif

#if MY_PRINTF_VERSION == 1
#elif MY_PRINTF_VERSION == 2
#elif MY_PRINTF_VERSION == 3
#else
#endif

#ifdef MY_PRINTF_VERSION
#else
#endif

#ifndef MY_PRINTF_VERSION
#else
#endif

#define MY_PRINTF_STANDARD
#if defined(MY_PRINTF_STANDARD)
#elif defined(MY_PRINTF_VERSION)
#else
#endif

}

int main(int argc, char * argv[]){
	return EXIT_SUCCESS;
}

#ifdef __cplusplus
}
#endif