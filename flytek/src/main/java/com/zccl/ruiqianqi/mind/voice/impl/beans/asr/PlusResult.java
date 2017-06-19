package com.zccl.ruiqianqi.mind.voice.impl.beans.asr;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ruiqianqi on 2016/8/29 0029.
 */
public class PlusResult {
    @SerializedName("ret")
    private int ret;

    @SerializedName("result")
    private Result result;

    public int getRet() {
        return ret;
    }

    public Result getResult() {
        return result;
    }

    public class Result{
        @SerializedName("version")
        private int version;

        @SerializedName("tts")
        private List<TTS> tts;

        @SerializedName("asr")
        private List<ASR> asr;

        public int getVersion() {
            return version;
        }

        public List<TTS> getTts() {
            return tts;
        }

        public List<ASR> getAsr() {
            return asr;
        }
    }

    public class TTS{
        @SerializedName("age")
        private String age;

        @SerializedName("nickname")
        private String nickname;

        @SerializedName("name")
        private String name;

        @SerializedName("language")
        private String language;

        @SerializedName("accent")
        private String accent;

        @SerializedName("sex")
        private String sex;

        @SerializedName("selected")
        private String selected;

        public String getAge() {
            return age;
        }

        public String getNickname() {
            return nickname;
        }

        public String getName() {
            return name;
        }

        public String getLanguage() {
            return language;
        }

        public String getAccent() {
            return accent;
        }

        public String getSex() {
            return sex;
        }

        public String getSelected() {
            return selected;
        }
    }

    public class ASR{
        @SerializedName("domain")
        private String domain;

        @SerializedName("name")
        private String name;

        @SerializedName("language")
        private String language;

        @SerializedName("accent")
        private String accent;

        @SerializedName("samplerate")
        private String samplerate;

        public String getDomain() {
            return domain;
        }

        public String getName() {
            return name;
        }

        public String getLanguage() {
            return language;
        }

        public String getAccent() {
            return accent;
        }

        public String getSamplerate() {
            return samplerate;
        }
    }
}
