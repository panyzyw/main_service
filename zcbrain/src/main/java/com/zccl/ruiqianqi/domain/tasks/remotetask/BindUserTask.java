package com.zccl.ruiqianqi.domain.tasks.remotetask;

import com.zccl.ruiqianqi.presentation.presenter.BindUserPresenter;

import static com.zccl.ruiqianqi.config.RemoteProtocol.A_BINDER_USER_DELETE;
import static com.zccl.ruiqianqi.config.RemoteProtocol.A_BINDER_USER_QUERY;

/**
 * Created by ruiqianqi on 2017/5/5 0005.
 */

public class BindUserTask extends BaseTask {

    private BindUserPresenter bup;

    public BindUserTask(){
        bup = new BindUserPresenter();
    }

    @Override
    public void run() {
        // 查询绑定用户列表返回
        if(A_BINDER_USER_QUERY.equals(cmd)){
            bup.queryBindUserResult(result);
        }
        // 删除绑定用户
        else if(A_BINDER_USER_DELETE.equals(cmd)){
            bup.deleteBindUserResult(result);
        }
    }
}
