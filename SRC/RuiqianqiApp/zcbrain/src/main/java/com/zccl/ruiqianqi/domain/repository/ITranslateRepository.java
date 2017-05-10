package com.zccl.ruiqianqi.domain.repository;

import rx.Observable;

/**
 * Created by ruiqianqi on 2016/7/18 0018.
 */
public interface ITranslateRepository {
    String translate(String words);
    Observable<String> translateRx(String words);
}
