package rabunabi.freechat.com.view

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
import android.os.Handler
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_chat.*
import rabunabi.freechat.com.BalloonchatApplication
import rabunabi.freechat.com.R
import rabunabi.freechat.com.adapter.ContactusAdapter
import rabunabi.freechat.com.common.Const
import rabunabi.freechat.com.common.extensions.showError
import rabunabi.freechat.com.common.extensions.toTimeFomatHHmm
import rabunabi.freechat.com.model.ContactusModel
import rabunabi.freechat.com.utils.SharePreferenceUtils
import rabunabi.freechat.com.utils.SocketConnectCallback
import rabunabi.freechat.com.utils.Utils
import rabunabi.freechat.com.view.base.BaseActivity
import rabunabi.freechat.com.view.home.ui.chat.ChatActivity
import rabunabi.freechat.com.viewmodel.ContactusViewModel
import kotlinx.android.synthetic.main.activity_contacus.*
import kotlinx.android.synthetic.main.dialog_chooser_image.*
import kotlinx.android.synthetic.main.toolbar.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import rabunabi.freechat.com.utils.SocketSingleton
import java.io.File
import java.util.*

class ContacusActivity : BaseActivity(), SocketConnectCallback {


    var contactusViewModel: ContactusViewModel? = null
    var adapter: ContactusAdapter? = null
    var date: Date? = null
    override fun getLayoutId(): Int {
        return R.layout.activity_contacus
    }
    var time:String? = "";
    override fun socketConnected() {

        SocketSingleton.getInstance().joinContact()

        SocketSingleton.getInstance().socket?.on("receive:contact") { args ->
            val obj = args[0] as JSONObject
            var message = obj.optString("message")
            var sendId = obj.optInt("send_id")
            var created = obj.optString("created")
            var sendId2 = obj.optInt("send_id")
            if (time != null && time.equals(created)) {
                System.out.println("diep return message " + message + " time " + time)
                return@on
            }
            time = created;
            System.out.println("diep message " + message + " time " + time)
            runOnUiThread {
                contactusViewModel?.list?.add(
                    ContactusModel(
                        message,
                        created.toTimeFomatHHmm(),
                        when (SharePreferenceUtils.getInstances().getUserInfo()!!.id) {
                            sendId -> true
                            else -> {
                                false
                            }
                        }, sendId2
                    )
                )
                adapter?.notifyDataSetChanged()
                rcvChat.scrollToPosition(contactusViewModel?.list?.size!! - 1)
            }

        }
    }

    override fun initView() {
        //connect socket
        SocketSingleton.getInstance().connect(this)
        contactusViewModel = ContactusViewModel()
//        tv_title_toolbar.text = getString(R.string.contact)
        img_title.setImageResource(R.drawable.title_contact_p)
        rl_action_left.visibility = View.VISIBLE

        img_title.visibility = View.GONE
        tvTitle.setText("お問い合わせ")
        tvTitle.visibility = View.VISIBLE

        imv_action_left.setImageResource(R.drawable.ic_icon_back_p)
        rl_action_left.setOnClickListener {
            finish()
        }

        var layoutManager =
            LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        rcvChat.layoutManager = layoutManager
        adapter = ContactusAdapter(contactusViewModel?.list)
        rcvChat.adapter = adapter
        btnSend.setOnClickListener {
            SocketSingleton.getInstance().sendContact(edtInput.text.toString())
            edtInput.text.clear()
        }
        edtInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                /*if (edt_send.text.toString().trim().length == 0) {
                    //     btn_send.setBackgroundResource(R.drawable.btn_message_send_active)
                    btnSend.alpha =0.5f;
                } else {
                    btnSend.alpha = 1f;
                    btnSend.setBackgroundResource(R.drawable.btn_message_send_nomal)
                }*/
            }

            override fun afterTextChanged(s: Editable) {
                /*if (!edtInput.text.toString().isNullOrEmpty()) {
                    btnSend.setBackgroundResource(R.drawable.btn_message_send_active)
                } else {
                    btnSend.setBackgroundResource(R.drawable.btn_message_send_nomal)
                }*/
            }
        })

        btnCamera.setOnClickListener{
            showDialogChooser()
        }
        showLoading()
        contactusViewModel?.getContactMessage {
            if (it == null) {
                adapter?.notifyDataSetChanged()
                rcvChat.scrollToPosition(adapter!!.itemCount - 1)
                Handler().postDelayed({
                    try {
                        rcvChat.scrollToPosition(adapter!!.itemCount - 1)
                    } catch (e: Exception) {
                    }
                }, 3000)
            } else {
                showError(it)
            }
            hideLoading()
        }
    }

    private fun showDialogChooser() {
        val dialog = Dialog(this)
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
    fun openGallery() {
        if (ContextCompat.checkSelfPermission(
                BalloonchatApplication.context!!,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                BalloonchatApplication.context!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                Const.READ_STORAGE_PERMISSION_CODE_PROFILE
            )
        } else {
            val intent = Intent()
            intent.type = "image/*"
            if (Build.VERSION.SDK_INT <= 23) {
                intent.action = Intent.ACTION_GET_CONTENT
            } else {
                intent.action = Intent.ACTION_OPEN_DOCUMENT
                intent.addCategory(Intent.CATEGORY_OPENABLE)
            }
            startActivityForResult(
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
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                Const.CAMERA_PERMISSION_CODE_PROFILE
            )
        } else {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(this.packageManager) != null) {
                val photoFile = Utils.getTemporaryCameraFile()
                ChatActivity.imageUri = FileProvider.getUriForFile(
                    applicationContext,
                    packageName + ".provider",
                    photoFile
                )
                takePictureIntent.putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    ChatActivity.imageUri
                )
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                    takePictureIntent.clipData = ClipData.newRawUri(
                        "",
                        ChatActivity.imageUri
                    )
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                }
                startActivityForResult(takePictureIntent, Const.REQUEST_CODE_CAMERA)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            when (requestCode) {
                Const.REQUEST_CODE_CAMERA -> if (resultCode == Activity.RESULT_OK) {
                    val aIntentData = Intent()
                    val file = Utils.getLastUsedCameraFile()
                    aIntentData.data = Uri.fromFile(file)
                    ChatActivity.imageUri = aIntentData.data
                    val fileNew = File(ChatActivity.imageFilePathNew)
                    if (fileNew.exists()) {
                        fileNew.delete()
                    }
                    ChatActivity.imageFilePath = ChatActivity.imageUri?.path
                    ChatActivity.imageFilePathNew = Utils.decodeFileStep1(
                        ChatActivity.imageFilePath!!, 800, 800
                    )!!
                    Utils.rotate(ChatActivity.imageFilePathNew, ChatActivity.imageUri!!, this)
                    val file1 = File(ChatActivity.imageFilePath)
                    if (file1.exists()) {
                        file1.delete()
                    }
                    doSendImageMessage()
                }
                Const.REQUEST_CODE_GALLERY -> if (resultCode == Activity.RESULT_OK) {
                    val imageUri = data!!.data
                    val fileNew = File(ChatActivity.imageFilePathNew)
                    if (fileNew.exists()) {
                        fileNew.delete()
                    }
                    ChatActivity.imageFilePath = Utils.getPath(this, data.data!!)
                    ChatActivity.imageFilePathNew = Utils.decodeFileStep1(
                        ChatActivity.imageFilePath!!, 800, 800
                    )!!

//                    Utils.rotate(imageFilePathNew, imageUri, this)

                    doSendImageMessage()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(BalloonchatApplication.context, "Please try again", Toast.LENGTH_LONG)
                .show()
        }
    }

    fun doSendImageMessage() {
        var file = File(ChatActivity.imageFilePathNew)
        val replaceFile = file.absolutePath.substring(file.absolutePath.lastIndexOf("/") + 1)
        val split = replaceFile.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val linkImage = split[1]
        var requestFile = when (linkImage) {
            "png" -> RequestBody.create(MediaType.parse("image/png"), file)
            "jpg" -> RequestBody.create(MediaType.parse("image/jpg"), file)
            "jpeg" -> RequestBody.create(MediaType.parse("image/jpeg"), file)
            else -> {
                RequestBody.create(MediaType.parse("image/png"), file)
            }
        }
        var image: MultipartBody.Part =
            MultipartBody.Part.createFormData("image", file.name, requestFile)

        contactusViewModel?.sendMessageImage(SharePreferenceUtils.getInstances().getUserInfo()!!.id!!, image) {
            if (it == null) {
                contactusViewModel?.chatModel?.let { it1 -> adapter?.listContent?.add(it1) }
                adapter?.notifyDataSetChanged()
                hideLoading()
            } else {
                showError(it)
            }
        }
    }
}
