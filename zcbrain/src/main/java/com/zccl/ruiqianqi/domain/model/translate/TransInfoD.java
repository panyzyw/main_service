package com.zccl.ruiqianqi.domain.model.translate;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ruiqianqi on 2016/8/12 0012.
 */
public class TransInfoD {

    @SerializedName("errorCode")
    private int errorCode;

    @SerializedName("query")
    private String query;

    @SerializedName("translation")
    private List<String> translation;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<String> getTranslation() {
        return translation;
    }

    public void setTranslation(List<String> translation) {
        this.translation = translation;
    }
}
