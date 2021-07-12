package rabunabi.freechat.com.view.home.ui.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import rabunabi.freechat.com.BalloonchatApplication
import rabunabi.freechat.com.R
import rabunabi.freechat.com.common.Const
import rabunabi.freechat.com.common.DialogUtils
import rabunabi.freechat.com.customview.CustomListPopupWindow
import rabunabi.freechat.com.utils.*
import rabunabi.freechat.com.view.base.BaseFragment
import rabunabi.freechat.com.view.dialog.DialogMessage
import rabunabi.freechat.com.view.home.HomeActivity
import rabunabi.freechat.com.view.home.ui.search.CustomSpinnerAdapter
import rabunabi.freechat.com.viewmodel.ProfileViewModel
import kotlinx.android.synthetic.main.dialog_chooser_image.*
import kotlinx.android.synthetic.main.dialog_country.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.img_avatar
import kotlinx.android.synthetic.main.fragment_profile.pradmob_banner
import kotlinx.android.synthetic.main.fragment_profile_container.*
import kotlinx.android.synthetic.main.fragment_setting.*
import kotlinx.android.synthetic.main.toolbar.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.json.JSONArray
import rabunabi.freechat.com.common.extensions.from
import rabunabi.freechat.com.model.ProfileModel
import rabunabi.freechat.com.view.ContacusActivity
import rabunabi.freechat.com.view.PrivacyActivity
import rabunabi.freechat.com.view.TermsActivity
import rabunabi.freechat.com.view.home.ui.profile.mypoint.PointFragment
import rabunabi.freechat.com.view.home.ui.search.ReloadListEvent
import rabunabi.freechat.com.view.home.ui.withdraw.DisableUserFragment
import java.io.File
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set

class FirstProfileFragment : BaseFragment() {
    var profileViewModel: ProfileViewModel? = null
    var from: String? = null;

    companion object {
        var imageUri: Uri? = null
        var imageFilePath: String? = ""
        var imageFilePathNew = ""
        var gender: Int = 1
        var langCode: String = "JP"
        var authencation: String? = null
    }

    override fun getIdContainer(): Int {
        return R.layout.fragment_profile_container
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
        var pr = SharePreferenceUtils.getInstances().getUserInfo();
        var point = SharePreferenceUtils.getInstances().getPointInfo();
        pr.let {
            tvNickName.setText("" + pr!!.nickname)
            tvCurrentPoint.setText("所持ポイント：" + point!!.points + "pt")
        }
        authencation = SharePreferenceUtils.getInstances().getString(Const.AUTH)
        profileViewModel = ProfileViewModel()
        profileViewModel?.initCountryData()
        initToolbar()
//        setOnclick()
        if (!authencation.equals("") && authencation != null) {
            try {
                loadData()
            }catch (e:java.lang.Exception ){}
        }
        if (TextUtils.isEmpty(from)) {
            pradmob_banner.visibility = View.INVISIBLE
        } else {
            pradmob_banner.visibility = View.VISIBLE
        }
    }

    var dialog: AlertDialog? = null

    fun showLoadingDialog(context: Context?) {
        var dialogBuilder = AlertDialog.Builder(context, R.style.CustomProgress)
        val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView = inflater.inflate(R.layout.progress_dialog, null)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setCancelable(false)
        dialog = dialogBuilder.create()
        dialog?.show()
    }

    fun dismiss() {
        try {
            dialog?.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    override fun onResume() {
        super.onResume()
        initData()

    }
    private fun initData() {
        var pr = SharePreferenceUtils.getInstances().getUserInfo();
        var point = SharePreferenceUtils.getInstances().getPointInfo();
        pr.let {
            tvNickName.setText("" + pr!!.nickname)
            tvCurrentPoint.setText("所持ポイント：" + point!!.points + "pt")
        }
    }
    private fun loadData() {
        showLoadingDialog(context)
        // goi api lay avatar
        profileViewModel?.getMyProfile(authencation.toString()) {
            dismiss()
            if (it == null && img_avatar != null) {
                img_avatar.from(
                    profileViewModel?.profileModel?.avatar,
                    R.drawable.ic_avatar_default2
                )
            }
        }
        img_avatar.setOnClickListener { showDialogChooser() }

        imgProfileDetail.setOnClickListener {
            (parentFragment as ProfileContainerFragment?)!!.addChild(ProfileFragment())
        }
        imgLogout.setOnClickListener {
            (parentFragment as ProfileContainerFragment?)!!.addChild(DisableUserFragment())
        }
        imgContactus.setOnClickListener {
            goToActivity(ContacusActivity::class.java)
        }
        imgTearm.setOnClickListener {
            goToActivity(TermsActivity::class.java)
        }
        imgPrivacy.setOnClickListener {
            goToActivity(PrivacyActivity::class.java)
        }
        imgMyPoint.setOnClickListener {
            (parentFragment as ProfileContainerFragment?)?.addChild(PointFragment())
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
            body = MultipartBody.Part.createFormData("avatar", file.getName(), requestFile)
        } else {
            val file = RequestBody.create(MultipartBody.FORM, "")
            body = MultipartBody.Part.createFormData("avatar", "", file)
        }
        //save country code to SharePreference
        SharePreferenceUtils.getInstances().saveString(
            Const.COUNTRY_CODE,
            langCode
        )
        var pr = SharePreferenceUtils.getInstances().getUserInfo();

        pr.let {
            showLoadingDialog(context)
            profileViewModel?.updateProfile(
                "" + pr!!.nickname,
                pr!!.gender!!.toInt(),
                "" + pr!!.intro,
                "" + pr!!.age,
                "" + pr!!.prefecture,
                langCode,
                "" + pr!!.marriage,
                "" + pr!!.purpose,
                body
            ) {
                dismiss()
                DialogUtils.showDialogMessage(context, "画像が保存されました。")
                if (it == null) {
                    //  showDialogMessage()
                } else {
                    //  DialogUtils.showDialogMessage(context, it)
                }
                imageFilePathNew = ""
            }
        }
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
            this!!.activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    Const.CAMERA_PERMISSION_CODE_PROFILE
                )
            }
        } else {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(this?.context!!.packageManager) != null) {
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
        rl_action_left.visibility = View.INVISIBLE
        imv_action_left.setImageResource(R.drawable.ic_icon_back_p)
        if (TextUtils.isEmpty(from)) {
//            tv_title_toolbar.text = getString(R.string.text_mypage)

        } else {
//            tv_title_toolbar.text = getString(R.string.text_profile)

        }
        rl_action_right.visibility = View.VISIBLE

        tvTitle.setText("マイページ")
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
                    imageFilePath = imageUri?.getPath()
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
                    updateProfile();
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
                    updateProfile();
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
}