package rabunabi.freechat.com.view.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import rabunabi.freechat.com.R
import kotlinx.android.synthetic.main.fragment_base.view.*

abstract class BaseFragment : Fragment() {
    protected var mView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_base, container, false)
            var view = LayoutInflater.from(context).inflate(getIdContainer(), null)
            mView?.flBaseContainer?.addView(
                view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }


    abstract fun getIdContainer(): Int
    abstract fun initView()
    fun setTitleToolbar(title: String? = "") {
//        tv_title_toolbar.text = title
    }

    fun showLoading() {
        (activity as? BaseActivity)?.showLoading()
    }

    fun hideLoading() {
        (activity as? BaseActivity)?.hideLoading()
    }

    fun goToActivity(c: Class<*>, bundle: Bundle? = null) {
        (activity as? BaseActivity)?.goToActivity(c, bundle)
    }
}