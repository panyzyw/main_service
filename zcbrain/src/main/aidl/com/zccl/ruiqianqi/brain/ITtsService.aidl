package com.zccl.ruiqianqi.brain;

import com.zccl.ruiqianqi.brain.ITtsBack;

interface ITtsService {
    void startTTS(String text, String tag, ITtsBack callback);
    void pauseTTS();
    void resumeTTS();
    void stopTTS();
}
