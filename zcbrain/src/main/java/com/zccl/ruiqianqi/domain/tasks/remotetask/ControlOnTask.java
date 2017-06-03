package com.zccl.ruiqianqi.domain.tasks.remotetask;

import com.zccl.ruiqianqi.brain.R;
import com.zccl.ruiqianqi.presentation.presenter.ReportPresenter;
import com.zccl.ruiqianqi.presentation.presenter.StatePresenter;
import com.zccl.ruiqianqi.domain.model.Robot;
import com.zccl.ruiqianqi.domain.model.datadown.ControlOn;
import com.zccl.ruiqianqi.tools.JsonUtils;
import com.zccl.ruiqianqi.tools.StringUtils;
import com.zccl.ruiqianqi.tools.SystemUtils;

import static com.zccl.ruiqianqi.config.RemoteProtocol.RET_SUCCESS;

/**
 * Created by ruiqianqi on 2017/3/15 0015.
 */

public class ControlOnTask extends BaseTask {
    @Override
    public void run() {
        ControlOn controlOn = JsonUtils.parseJson(result, ControlOn.class);
        if(null != controlOn && RET_SUCCESS.equals(controlOn.getRet())) {
            StatePresenter sp = StatePresenter.getInstance();
            if(!StringUtils.isEmpty(controlOn.getRobot())) {
                Robot robot = JsonUtils.parseJson(controlOn.getRobot(), Robot.class);
                if(null != robot) {
                    sp.setRobot(robot);
                }
            }
            sp.setInControl(true);
            sp.setControlId(controlOn.getController());

            ReportPresenter.report(mContext.getString(R.string.control_is_on));

            // 点亮屏幕
            SystemUtils.wakeUp(mContext);
        }
    }
}
