package com.zccl.ruiqianqi.domain.tasks.remotetask;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;

import com.zccl.ruiqianqi.presentation.presenter.StatePresenter;
import com.zccl.ruiqianqi.domain.model.Robot;
import com.zccl.ruiqianqi.domain.model.robotdown.FlushDown;
import com.zccl.ruiqianqi.tools.JsonUtils;
import com.zccl.ruiqianqi.tools.StringUtils;

import static com.zccl.ruiqianqi.brain.service.observer.NameObserver.NAME_URI;
import static com.zccl.ruiqianqi.config.RemoteProtocol.RET_SUCCESS;

/**
 * Created by ruiqianqi on 2017/3/15 0015.
 */

public class FlushTask extends BaseTask {
    @Override
    public void run() {
        FlushDown flushDown = JsonUtils.parseJson(result, FlushDown.class);
        if(null != flushDown && RET_SUCCESS.equals(flushDown.getRet())) {
            if(StringUtils.isEmpty(flushDown.getRobot()))
                return;

            StatePresenter sp = StatePresenter.getInstance();
            Robot robot = JsonUtils.parseJson(flushDown.getRobot(), Robot.class);
            if(null != robot && !StringUtils.isEmpty(robot.getRname())){
                Robot robotBak = sp.getRobot();
                // 更新名字数据库
                if(null != robotBak && !robot.getRname().equals(robotBak.getRname())){
                    Uri uriName = Uri.parse(NAME_URI);
                    ContentResolver resolver = mContext.getContentResolver();
                    ContentValues values = new ContentValues();
                    values.put("name", robot.getRname());
                    resolver.update(uriName, values, null, null);
                }
                // 更新临时持久信息
                sp.setRobot(robot);
            }
        }
    }
}
