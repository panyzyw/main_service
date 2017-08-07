package com.zccl.ruiqianqi.mind.voice.alexa.beans.event;

import java.util.ArrayList;

/**
 * Created by ruiqianqi on 2017/2/15 0015.
 *
 * <a href="https://developer.amazon.com/public/solutions/alexa/alexa-voice-service/reference/speechrecognizer">context</a>
 */

public class RecognizeEvent {
    public ArrayList<Object> context = new ArrayList<>();
    public RecognizeMsg event = new RecognizeMsg();
}
