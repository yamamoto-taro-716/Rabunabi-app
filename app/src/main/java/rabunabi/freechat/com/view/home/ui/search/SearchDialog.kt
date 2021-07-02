package rabunabi.freechat.com.view.home.ui.search

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.TextView
import rabunabi.freechat.com.R
import rabunabi.freechat.com.customview.CustomListPopupWindow
import rabunabi.freechat.com.utils.FileUtils
import rabunabi.freechat.com.utils.GsonUtils
import kotlinx.android.synthetic.main.dialog_search.*

class SearchDialog(context: Context) :
    Dialog(context, R.style.DialogTheme) {

    private lateinit var mNickname: String
    private lateinit var mAgeFrom: String
    private lateinit var mAgeTo: String
    private lateinit var mHasAvatar: String
    private lateinit var mArea: String
    private lateinit var mGender: String
    private lateinit var mPurpose: String
    private lateinit var mMarriage: String

    private var mListener: OnSearchUserListener? = null

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_search)

        btnSearch.setOnClickListener(View.OnClickListener {
            getDataSelected()
            mListener?.onDataSelected(mNickname, mGender, mArea, mAgeFrom, mAgeTo, mHasAvatar, mPurpose, mMarriage)
            dismiss()
        })
    }

    fun setListener(mListener: OnSearchUserListener?) {
        this.mListener = mListener
    }

    fun getDataSelected() {
        mNickname = edtNickname.text.toString()
        mAgeFrom = edtAgeFrom.text.toString()
        mAgeTo = edtAgeTo.text.toString()
    }


    fun showOccupationPicker(
        tvDisplaySource: TextView
    ) {
        val jsonCity: String = FileUtils.loadJSONFromAsset(
            context,
            "jp_city.json"
        )
        val listNation: List<String> = GsonUtils.String2ListObject(
            jsonCity,
            Array<String>::class.java
        )
        val nationSelected: String = tvDisplaySource.getText().toString()
        val spinnerAdapter =
            CustomSpinnerAdapter(
                context, R.layout.spinner_item, listNation,
                tvDisplaySource
            )
        val listPopupWindow =
            CustomListPopupWindow(
                context,
                tvDisplaySource,
                spinnerAdapter,
                nationSelected
            )
        listPopupWindow.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
            tvDisplaySource.text = (parent.adapter.getItem(position)) as String
            listPopupWindow.dismiss()
        })
        listPopupWindow.show()
    }
}