package com.zccl.ruiqianqi.presentation.converter;

import com.zccl.ruiqianqi.domain.model.ServerAddr;
import com.zccl.ruiqianqi.storage.db.ServerBean;

/**
 * Created by ruiqianqi on 2017/4/12 0012.
 */

public class Converter {

    /**
     * 将外层的模型转换为内层模型
     * @param serverBean
     * @return
     */
    public static ServerAddr serverBean2Addr(ServerBean serverBean){
        if(null == serverBean)
            return null;
        ServerAddr serverAddr = new ServerAddr();
        serverAddr.setFlagVersion(serverBean.flagVersion);
        serverAddr.setHttpRequest(serverBean.httpRequest);
        serverAddr.setHttpResource(serverBean.httpResource);
        serverAddr.setTcpRequest(serverBean.tcpRequest);
        serverAddr.setTcpPort(serverBean.tcpPort);
        return serverAddr;
    }

}
