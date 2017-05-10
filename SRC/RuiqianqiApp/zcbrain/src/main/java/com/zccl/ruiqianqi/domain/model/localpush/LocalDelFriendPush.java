package com.zccl.ruiqianqi.domain.model.localpush;

import static com.zccl.ruiqianqi.config.LocalProtocol.REMOVE_FRIEND_PUSH;
import static com.zccl.ruiqianqi.config.RemoteProtocol.RET_SUCCESS;

/**
 * Created by ruiqianqi on 2017/3/17 0017.
 */

public class LocalDelFriendPush {
    private String ret = RET_SUCCESS;
    private String cmd = REMOVE_FRIEND_PUSH;
}
