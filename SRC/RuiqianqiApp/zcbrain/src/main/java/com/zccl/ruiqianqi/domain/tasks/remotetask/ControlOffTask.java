package com.zccl.ruiqianqi.domain.tasks.remotetask;

import com.zccl.ruiqianqi.brain.R;
import com.zccl.ruiqianqi.presentation.presenter.ReportPresenter;
import com.zccl.ruiqianqi.presentation.presenter.StatePresenter;

/**
 * Created by ruiqianqi on 2017/3/15 0015.
 */

public class ControlOffTask extends BaseTask {
    @Override
    public void run() {
        StatePresenter.getInstance().setInControl(false);
        StatePresenter.getInstance().setControlId(null);

        ReportPresenter.report(mContext.getString(R.string.control_is_off));
    }
}
