//
// Created by ruiqianqi on 2017/2/20 0020.
//

#ifndef RUIQIANQIAPP_MEDIA_H
#define RUIQIANQIAPP_MEDIA_H


#ifdef __cplusplus
extern "C" {
#endif

#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libswscale/swscale.h>
#include <libswresample/swresample.h>

#ifdef __cplusplus
};
#endif

extern void initAudio();

#endif //RUIQIANQIAPP_MEDIA_H
