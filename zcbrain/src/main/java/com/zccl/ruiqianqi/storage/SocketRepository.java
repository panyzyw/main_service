package com.zccl.ruiqianqi.storage;

import com.zccl.ruiqianqi.domain.model.ServerAddr;
import com.zccl.ruiqianqi.domain.repository.ISocketRepository;
import com.zccl.ruiqianqi.presentation.converter.Converter;
import com.zccl.ruiqianqi.storage.db.MyDbFlow;
import com.zccl.ruiqianqi.storage.db.ServerBean;

/**
 * Created by ruiqianqi on 2017/4/12 0012.
 */

public class SocketRepository implements ISocketRepository {
    @Override
    public ServerAddr queryServerAddr(String flagVersion) {
        ServerBean serverBean = MyDbFlow.queryServerBean(flagVersion);
        return Converter.serverBean2Addr(serverBean);
    }
}
