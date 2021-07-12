package rabunabi.freechat.com.view.home.ui.profile.mypoint


import android.graphics.Color
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_point_table.*
import kotlinx.android.synthetic.main.toolbar.*
import rabunabi.freechat.com.R
import rabunabi.freechat.com.common.Const
import rabunabi.freechat.com.utils.SharePreferenceUtils
import rabunabi.freechat.com.view.base.BaseActivity
import rabunabi.freechat.com.view.home.ui.profile.ProfileContainerFragment
import rabunabi.freechat.com.view.home.ui.profile.register.RegisterFragment
class PointTableActivity :BaseActivity() {



    override fun getLayoutId(): Int {
        return R.layout.activity_point_table
    }

    override fun initView() {
        var point = SharePreferenceUtils.getInstances().getPointInfo();
        if (point!!.readMessage!=0) {
            readMessage.text = point!!.readMessage.toString() + " pt"
            readMessage.setTextColor(Color.BLACK)
        }
        if (point!!.sendMessage!=0) {
            sendMessage.text = point!!.sendMessage.toString() + " pt"
            sendMessage.setTextColor(Color.BLACK)
        }
        if (point!!.sendImage!=0) {
            sendImage.text = point!!.sendImage.toString() + " pt"
            sendImage.setTextColor(Color.BLACK)
        }
        tvTitle.visibility = View.VISIBLE
        tvTitle.text = "ポイント表"


    }


}
