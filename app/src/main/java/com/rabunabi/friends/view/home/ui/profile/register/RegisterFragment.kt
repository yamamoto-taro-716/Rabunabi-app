package com.rabunabi.friends.view.home.ui.profile.register

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.rabunabi.friends.BalloonchatApplication
import com.rabunabi.friends.R
import com.rabunabi.friends.adapter.CountryAdapter
import com.rabunabi.friends.common.Const
import com.rabunabi.friends.common.DialogUtils
import com.rabunabi.friends.common.extensions.from
import com.rabunabi.friends.customview.CustomListPopupWindow
import com.rabunabi.friends.utils.*
import com.rabunabi.friends.view.base.BaseFragment
import com.rabunabi.friends.view.dialog.DialogMessage
import com.rabunabi.friends.view.home.HomeActivity
import com.rabunabi.friends.view.home.ui.search.CustomSpinnerAdapter
import com.rabunabi.friends.viewmodel.ProfileViewModel
import kotlinx.android.synthetic.main.dialog_chooser_image.*
import kotlinx.android.synthetic.main.dialog_country.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.android.synthetic.main.fragment_register.btnSave
import kotlinx.android.synthetic.main.fragment_register.edtAge
import kotlinx.android.synthetic.main.fragment_register.edtIntro
import kotlinx.android.synthetic.main.fragment_register.edtNickname
import kotlinx.android.synthetic.main.fragment_register.img_avatar
import kotlinx.android.synthetic.main.fragment_register.linear_country
import kotlinx.android.synthetic.main.fragment_register.pradmob_banner
import kotlinx.android.synthetic.main.fragment_register.radioFemale
import kotlinx.android.synthetic.main.fragment_register.radioMale
import kotlinx.android.synthetic.main.fragment_register.textViewArea
import kotlinx.android.synthetic.main.fragment_register.textViewMarriage
import kotlinx.android.synthetic.main.fragment_register.textViewPurpose
import kotlinx.android.synthetic.main.fragment_register.tv_country
import kotlinx.android.synthetic.main.toolbar.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.json.JSONArray
import com.rabunabi.friends.view.home.ui.search.ReloadListEvent
import java.io.File
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set

class RegisterFragment : BaseFragment() {
    var profileViewModel: ProfileViewModel? = null
    var from: String? = null

    companion object {
        var imageUri: Uri? = null
        var imageFilePath: String? = ""
        var imageFilePathNew = ""
        var gender: Int = 1
        var langCode: String = "JP"
        var authencation: String? = null
    }

    override fun getIdContainer(): Int {
        return R.layout.fragment_register
    }

    protected fun getAge(): List<String> {
        val arr = ArrayList<String>()
        arr.add("指定なし")
        for (i in 18..80) {
            arr.add("" + i + "歳")
        }
        return arr
    }

    override fun initView() {
        if (TextUtils.isEmpty(
                SharePreferenceUtils.getInstances().getString(
                    Const.COUNTRY_CODE
                )
            )
        ) {
            tv_country.setText(LocaleUtils.jpTex)
//            img_country.setImageResource(LocaleUtils.jpIcon)
        }
        authencation = SharePreferenceUtils.getInstances().getString(Const.AUTH)
        profileViewModel = ProfileViewModel()
        profileViewModel?.initCountryData()
        initToolbar()
        setOnclick()
        if (!authencation.equals("") && authencation != null) {
            loadData()
        }
        if (TextUtils.isEmpty(from)) {
            pradmob_banner.visibility = View.INVISIBLE
        } else {
            pradmob_banner.visibility = View.VISIBLE
        }
    }


    fun loadData() {
        showLoading()
        profileViewModel?.getMyProfile(authencation.toString()) {
            if (it == null) {
                edtNickname.setText(profileViewModel?.profileModel?.nickname)
                img_avatar.from(
                    profileViewModel?.profileModel?.avatar,
                    R.drawable.ic_avatar_default2
                )
                setGenderData(profileViewModel?.profileModel?.gender)
                BalloonchatApplication.userGender = profileViewModel?.profileModel?.gender!!
                setCountryData(profileViewModel?.profileModel?.nationality)
                //setLanguageData(profileViewModel?.profileModel?.nationality)
                profileViewModel?.initCountryData()
                edtIntro.setText(profileViewModel?.profileModel?.intro)

                edtAge.text = profileViewModel?.profileModel?.age + "歳"
                textViewArea.text = profileViewModel?.profileModel?.prefecture

                //muc dich
                textViewPurpose.text = "" + profileViewModel?.profileModel?.purpose
                //hon nhan
                textViewMarriage.text = "" + profileViewModel?.profileModel?.marriage
                hideLoading()
            }
        }
    }

    fun setCountryData(countryCode: String?) {
        val data: HashMap<String?, String?> = HashMap()
        var jsonArray = JSONArray(Utils.readJSONFromAsset("countries.json"))
        for (i in 0 until jsonArray.length()) {
            data[jsonArray.getJSONObject(i)?.optString("code")] =
                jsonArray.getJSONObject(i)?.optString("name")
        }
        val resources = BalloonchatApplication.context?.resources
        val resourceId = resources?.getIdentifier(
            "flag_${countryCode?.toLowerCase()}",
            "drawable",
            BalloonchatApplication.context?.packageName
        )
        resourceId?.let {
            //            img_country.setImageDrawable(resources?.getDrawable(resourceId))
        }
        tv_country.text = data.get(countryCode)
        if (countryCode == "00") {
            tv_country.text = "Orther Country"
        }
    }

    fun setGenderData(gender: Int?) {
        if (gender == 1) {
            radioMale.isChecked = true
            radioFemale.isChecked = false
        } else {
            radioFemale.isChecked = true
            radioMale.isChecked = false
        }
    }

    private fun register() {
        var body: MultipartBody.Part? = null
        if (!imageFilePathNew.equals("")) {
            var file = File(imageFilePathNew)
            val replaceFile = file.absolutePath.substring(file.absolutePath.lastIndexOf("/") + 1)
            val split =
                replaceFile.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val linkImage = split[1]
            var requestFile = when (linkImage) {
                "png" -> RequestBody.create(MediaType.parse("image/png"), file)
                "jpg" -> RequestBody.create(MediaType.parse("image/jpg"), file)
                "jpeg" -> RequestBody.create(MediaType.parse("image/jpeg"), file)
                else -> {
                    RequestBody.create(MediaType.parse("image/png"), file)
                }
            }
            body = MultipartBody.Part.createFormData("avatar", file.name, requestFile)
        } else {
            val file = RequestBody.create(MultipartBody.FORM, "")
            body = MultipartBody.Part.createFormData("avatar", "", file)
        }

        //save country code to SharePreference
        SharePreferenceUtils.getInstances().saveString(
            Const.COUNTRY_CODE,
            langCode
        )

        //start register
        var age = edtAge.text.toString()
        if (age == "指定なし") {
            age = ""
        }
        if (!TextUtils.isEmpty(age)) {
            age = age.replace("歳", "")
        }

        var prefecture = textViewArea.text.toString()
        if (prefecture == "指定なし") {
            prefecture = ""
        }

        profileViewModel?.register(
            edtNickname.text.toString(),
            gender,
            edtIntro.text.toString(),
            age,
            prefecture,
            langCode,
            body
        ) {
            hideLoading()
            if (it == null) {
                showDialogMessage()
            } else {
                DialogUtils.showDialogMessage(context, it)
            }
        }
    }

    private fun updateProfile() {
        var body: MultipartBody.Part? = null
        if (!imageFilePathNew.equals("")) {
            var file = File(imageFilePathNew)
            val replaceFile = file.absolutePath.substring(file.absolutePath.lastIndexOf("/") + 1)
            val split =
                replaceFile.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val linkImage = split[1]
            var requestFile = when (linkImage) {
                "png" -> RequestBody.create(MediaType.parse("image/png"), file)
                "jpg" -> RequestBody.create(MediaType.parse("image/jpg"), file)
                "jpeg" -> RequestBody.create(MediaType.parse("image/jpeg"), file)
                else -> {
                    RequestBody.create(MediaType.parse("image/png"), file)
                }
            }
            body = MultipartBody.Part.createFormData("avatar", file.name, requestFile)
        } else {
            val file = RequestBody.create(MultipartBody.FORM, "")
            body = MultipartBody.Part.createFormData("avatar", "", file)
        }
        //save country code to SharePreference
        SharePreferenceUtils.getInstances().saveString(
            Const.COUNTRY_CODE,
            langCode
        )

        //start update profile
        var age = edtAge.text.toString()
        if (age == "指定なし") {
            age = ""
        }
        if (!TextUtils.isEmpty(age)) {
            age = age.replace("歳", "")
        }


        var prefecture = textViewArea.text.toString()
        if (prefecture == "指定なし") {
            prefecture = ""
        }

        profileViewModel?.updateProfile(
            edtNickname.text.toString(),
            gender,
            edtIntro.text.toString(),
            age,
            prefecture,
            langCode,
            textViewMarriage.text.toString(),
            textViewPurpose.text.toString(),
            body
        ) {
            hideLoading()
            if (it == null) {
                showDialogMessage()
            } else {
                DialogUtils.showDialogMessage(context, it)
            }
            EventBus.getDefault().post(ReloadListEvent())
            imageFilePathNew = ""
        }
    }

    private fun validateFieldData(): Boolean {
        if (edtNickname.text.toString().equals("") || edtNickname.text.toString() == null) {
            DialogUtils.showDialogMessage(
                activity,
                getString(R.string.err_name_required),
                getString(R.string.ok)
            )
            return false
        } else if (textViewArea.text.toString() == "") {
            DialogUtils.showDialogMessage(
                activity,
                getString(R.string.err_area_required),
                getString(R.string.ok)
            )
            return false
        } else return true
    }

    private fun showDialogMessage() {
        DialogUtils.showDialogMessage(
            activity,
            getString(R.string.profile_saved),
            "",
            DialogMessage.OnDialogMessageListener {
                if (!TextUtils.isEmpty(from)) { // TH di tu splash -> profile
                    goToActivity(HomeActivity::class.java)
                    if (activity !is HomeActivity) {
                        activity?.finish()
                    }
                }
            })
    }

    private fun setOnclick() {
        btnSave.setOnClickListener {
            if (validateFieldData()) {
                showLoading()
                if (!authencation.equals("")) {
                    updateProfile()
                } else {
                    register()
                }
            }
        }
        linear_country.setOnClickListener {

            var dialog = Dialog(context!!)
            var layoutManager =
                LinearLayoutManager(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_country)
            val view = (dialog).window
            view!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.imvClose.setOnClickListener { dialog.dismiss() }
            dialog.show()
            layoutManager.orientation = LinearLayoutManager.VERTICAL
            dialog.rcvCountry.layoutManager = layoutManager
            dialog.rcvCountry.adapter =
                CountryAdapter(profileViewModel?.countryList) { countryModel ->
                    langCode =
                        countryModel?.code.toString()
                    val resources = BalloonchatApplication.context?.resources
                    val resourceId = resources?.getIdentifier(
                        "flag_${countryModel?.code?.toLowerCase()}",
                        "drawable",
                        BalloonchatApplication.context?.packageName
                    )

//                    resourceId?.let {
////                        img_country.setImageDrawable(resources?.getDrawable(resourceId))
//                    }
                    tv_country.text = countryModel?.name
                    if (countryModel?.name == "ALL") {
                        tv_country.text = "Orther Co..."
                    }
                    dialog.dismiss()
                }

        }
        edtAge.setOnClickListener(View.OnClickListener {
            showAge(edtAge)
        })
        img_avatar.setOnClickListener { showDialogChooser() }
        radioMale.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) gender = 1
        })
        radioFemale.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) gender = 2
        })

        textViewArea.setOnClickListener { showCityPicker(textViewArea) }

        textViewPurpose.setOnClickListener { showPurposePicker(textViewPurpose) }

        textViewMarriage.setOnClickListener { showMarriagePicker(textViewMarriage) }
    }

    private fun showDialogChooser() {
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_chooser_image)
        dialog.tvCamera.setOnClickListener {
            dialog.dismiss()
            openCamera()
        }
        dialog.tvGallery.setOnClickListener {
            dialog.dismiss()
            openGallery()
        }
        dialog.tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun openGallery() {
        if (ContextCompat.checkSelfPermission(
                BalloonchatApplication.context!!,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                BalloonchatApplication.context!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    Const.READ_STORAGE_PERMISSION_CODE_PROFILE
                )
            }
        } else {
            val intent = Intent()
            intent.type = "image/*"
            if (Build.VERSION.SDK_INT <= 23) {
                intent.action = Intent.ACTION_GET_CONTENT
            } else {
                intent.action = Intent.ACTION_OPEN_DOCUMENT
                intent.addCategory(Intent.CATEGORY_OPENABLE)
            }
            this.startActivityForResult(
                Intent.createChooser(intent, getString(R.string.select_image_gallery)),
                Const.REQUEST_CODE_GALLERY
            )
        }
    }

    @SuppressLint("NewApi")
    fun openCamera() {
        if (ContextCompat.checkSelfPermission(
                BalloonchatApplication.context!!,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                BalloonchatApplication.context!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            this.activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    Const.CAMERA_PERMISSION_CODE_PROFILE
                )
            }
        } else {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(this.context!!.packageManager) != null) {
                val photoFile = Utils.getTemporaryCameraFile()
                imageUri = context?.let {
                    FileProvider.getUriForFile(
                        it,
                        context!!.packageName + ".provider",
                        photoFile
                    )
                }
                takePictureIntent.putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    imageUri
                )
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                    takePictureIntent.clipData = ClipData.newRawUri(
                        "",
                        imageUri
                    )
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                }
                startActivityForResult(takePictureIntent, Const.REQUEST_CODE_CAMERA)
            }
        }
    }

    private fun initToolbar() {
//        rl_action_left.visibility = View.VISIBLE
//        rl_action_left.setOnClickListener {
//            (parentFragment as ProfileContainerFragment?)!!.popFragment()
//        }
        imv_action_left.setImageResource(R.drawable.ic_icon_back_p)
        if (TextUtils.isEmpty(from)) {
//            tv_title_toolbar.text = getString(R.string.text_mypage)

        } else {
//            tv_title_toolbar.text = getString(R.string.text_profile)

            imv_action_left.visibility = View.INVISIBLE
        }
        rl_action_right.visibility = View.VISIBLE


        img_title.visibility = View.INVISIBLE
        tvTitle.setText("プロフィール作成")
        tvTitle.visibility = View.VISIBLE
        /*imv_action_right.visibility = View.VISIBLE
        imv_action_right.setOnClickListener {
            goToActivity(SettingActivity::class.java)
        }*/
        /*System.out.println("DIEP ")
        if (!SharePreferenceUtils.getInstances().getString(Const.AUTH).equals("") && SharePreferenceUtils.getInstances().getInt(
                Const.USER_START_ID
            ) > 0
        ) {
            System.out.println("DIEP if")
            tvRightToolbar?.visibility = View.VISIBLE;
            tvRightToolbar?.setOnClickListener(View.OnClickListener {
                goToActivity(HomeActivity::class.java)
            });
        } else {
            System.out.println("DIEP else")
            tvRightToolbar?.visibility = View.INVISIBLE;
        }*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            when (requestCode) {
                Const.REQUEST_CODE_CAMERA -> if (resultCode == Activity.RESULT_OK) {
                    val aIntentData = Intent()
                    val file = Utils.getLastUsedCameraFile()
                    aIntentData.data = Uri.fromFile(file)
                    imageUri = aIntentData.data
                    val fileNew = File(imageFilePathNew)
                    if (fileNew.exists()) {
                        fileNew.delete()
                    }
                    imageFilePath = imageUri?.path
                    imageFilePathNew = Utils.decodeFileStep1(
                        imageFilePath!!, 800, 800
                    )!!
                    Utils.rotate(
                        imageFilePathNew,
                        imageUri!!,
                        context!!
                    )
                    Glide.with(this).load(imageFilePathNew).into(img_avatar)
                    val file1 = File(imageFilePath)
                    if (file1.exists()) {
                        file1.delete()
                    }
                }
                Const.REQUEST_CODE_GALLERY -> if (resultCode == Activity.RESULT_OK) {
                    val imageUri = data!!.data
                    val fileNew = File(imageFilePathNew)
                    if (fileNew.exists()) {
                        fileNew.delete()
                    }
                    imageFilePath = Utils.getPath(context!!, data.data!!)
                    imageFilePathNew = Utils.decodeFileStep1(
                        imageFilePath!!, 800, 800
                    )!!
                    Glide.with(this).load(imageFilePathNew).into(img_avatar)
                }
            }
        } catch (e: Exception) {
            Toast.makeText(BalloonchatApplication.context, "Please try again", Toast.LENGTH_LONG)
                .show()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Const.CAMERA_PERMISSION_CODE_PROFILE -> if (grantResults.size > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                openCamera()
            } else {
                val showRationale =
                    activity?.let {
                        ActivityCompat.shouldShowRequestPermissionRationale(
                            it,
                            Manifest.permission.CAMERA
                        )
                    }
                if (!showRationale!!) {
                    context?.let {
                        Utils.showDialogPermission(
                            it,
                            resources.getString(R.string.permission_camera)
                        )
                    }
                }
            }
            Const.READ_STORAGE_PERMISSION_CODE_PROFILE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                val showRationale =
                    activity?.let {
                        ActivityCompat.shouldShowRequestPermissionRationale(
                            it,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    }
                if (!showRationale!!) {
                    context?.let {
                        Utils.showDialogPermission(
                            it,
                            resources.getString(R.string.permission_storage)
                        )
                    }
                }
            }
            else -> {
            }
        }
    }

    fun showAge(tvDisplaySource: TextView) {
        val listNation = getAge()
        val nationSelected = tvDisplaySource.text.toString()
        val spinnerAdapter = CustomSpinnerAdapter(
            activity!!, R.layout.spinner_item, listNation,
            tvDisplaySource
        )
        val listPopupWindow =
            CustomListPopupWindow(activity!!, tvDisplaySource, spinnerAdapter, nationSelected)
        listPopupWindow.setOnItemClickListener { parent, view, position, id ->
            tvDisplaySource.text = parent.adapter.getItem(position) as String
            listPopupWindow.dismiss()
        }
        listPopupWindow.show()
    }

    private fun showCityPicker(tvDisplaySource: TextView) {
        val jsonCity = FileUtils.loadJSONFromAsset(
            activity!!,
            activity!!.getString(R.string.list_city_jp)
        )
        val listNation = GsonUtils.String2ListObject(jsonCity, Array<String>::class.java)
        val nationSelected = tvDisplaySource.text.toString()
        val spinnerAdapter = CustomSpinnerAdapter(
            activity!!, R.layout.spinner_item, listNation,
            tvDisplaySource
        )
        val listPopupWindow =
            CustomListPopupWindow(activity!!, tvDisplaySource, spinnerAdapter, nationSelected)
        listPopupWindow.setOnItemClickListener { parent, view, position, id ->
            tvDisplaySource.text = parent.adapter.getItem(position) as String
            listPopupWindow.dismiss()
        }
        listPopupWindow.show()
    }

    private fun showPurposePicker(tvDisplaySource: TextView) {
        val jsonCity = FileUtils.loadJSONFromAsset(
            activity!!,
            activity!!.getString(R.string.list_purpose)
        )
        val listNation = GsonUtils.String2ListObject(jsonCity, Array<String>::class.java)
        val nationSelected = tvDisplaySource.text.toString()
        val spinnerAdapter = CustomSpinnerAdapter(
            activity!!, R.layout.spinner_item, listNation,
            tvDisplaySource
        )
        val listPopupWindow =
            CustomListPopupWindow(activity!!, tvDisplaySource, spinnerAdapter, nationSelected)
        listPopupWindow.setOnItemClickListener { parent, view, position, id ->
            tvDisplaySource.text = parent.adapter.getItem(position) as String
            listPopupWindow.dismiss()
        }
        listPopupWindow.show()
    }

    private fun showMarriagePicker(tvDisplaySource: TextView) {
        val jsonCity = FileUtils.loadJSONFromAsset(
            activity!!,
            activity!!.getString(R.string.list_marriage)
        )
        val listNation = GsonUtils.String2ListObject(jsonCity, Array<String>::class.java)
        val nationSelected = tvDisplaySource.text.toString()
        val spinnerAdapter = CustomSpinnerAdapter(
            activity!!, R.layout.spinner_item, listNation,
            tvDisplaySource
        )
        val listPopupWindow =
            CustomListPopupWindow(activity!!, tvDisplaySource, spinnerAdapter, nationSelected)
        listPopupWindow.setOnItemClickListener { parent, view, position, id ->
            tvDisplaySource.text = parent.adapter.getItem(position) as String
            listPopupWindow.dismiss()
        }
        listPopupWindow.show()
    }
}