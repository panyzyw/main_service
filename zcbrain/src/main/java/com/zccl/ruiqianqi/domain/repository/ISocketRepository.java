package com.zccl.ruiqianqi.domain.repository;

import com.zccl.ruiqianqi.domain.model.ServerAddr;

/**
 * Created by ruiqianqi on 2017/4/12 0012.
 */

public interface ISocketRepository {
    /**
     * 根据服务器版本，查询服务器地址端口信息
     * @param flagVersion
     * @return
     */
    ServerAddr queryServerAddr(String flagVersion);
}
