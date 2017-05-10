//
// Created by ruiqianqi on 2017/2/20 0020.
//
#include <public.h>
#include "media.h"

#undef TAG
#define TAG "media.cpp"

void initAudio(){

    av_register_all();

    AVCodec * pCodec = avcodec_find_decoder(AV_CODEC_ID_MP1);
    if (!pCodec) {
        LOGE("avcodec_find_decoder failed");
        return;
    }

    AVCodecContext * pContext = avcodec_alloc_context3(pCodec);
    if (!pContext) {
        LOGE("avcodec_alloc_context3 failed");
        return;
    }

    pContext->channels = 1;
    pContext->sample_fmt = AV_SAMPLE_FMT_FLTP;
    pContext->sample_rate = 16000;
    pContext->codec_type = AVMEDIA_TYPE_AUDIO;

    if (avcodec_open2(pContext, pCodec, NULL) < 0) {
        LOGD("avcodec_open2 failed");
        return;
    }

    AVFrame * pAvFrame = av_frame_alloc();
    SwrContext * pSwrContext = swr_alloc();

    /*
    pSwrContext = swr_alloc_set_opts(pSwrContext,
                                     3, AV_SAMPLE_FMT_S16, 16000,
                                     3, AV_SAMPLE_FMT_FLTP, 16000,
                                     0, NULL);
                                     */

}

