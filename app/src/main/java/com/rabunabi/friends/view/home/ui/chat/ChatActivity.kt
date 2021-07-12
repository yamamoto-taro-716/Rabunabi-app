package com.rabunabi.friends.view.home.ui.chat

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
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.dialog_chooser_image.*
import kotlinx.android.synthetic.main.dialog_image.*
import kotlinx.android.synthetic.main.toolbar.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import com.rabunabi.friends.BalloonchatApplication.Companion.context
import com.rabunabi.friends.R
import com.rabunabi.friends.adapter.ChatAdapter
import com.rabunabi.friends.common.Const
import com.rabunabi.friends.common.Const.Companion.REQUEST_CODE_CAMERA
import com.rabunabi.friends.common.Const.Companion.REQUEST_CODE_GALLERY
import com.rabunabi.friends.common.DialogUtils.showDialogMessage
import com.rabunabi.friends.common.extensions.showAlertDialog
import com.rabunabi.friends.common.extensions.showError
import com.rabunabi.friends.common.extensions.toTimeFomatHHmm
import com.rabunabi.friends.firebase.NotifyHelper
import com.rabunabi.friends.model.ChatModel
import com.rabunabi.friends.model.friends.FriendListModel
import com.rabunabi.friends.utils.*
import com.rabunabi.friends.utils.Utils.Companion.getTemporaryCameraFile
import com.rabunabi.friends.view.base.BaseActivity
import com.rabunabi.friends.view.home.HomeActivity
import com.rabunabi.friends.viewmodel.ChatViewModel
import java.io.File
import java.util.*

class ChatActivity : BaseActivity(), SocketConnectCallback {


    var chatViewModel: ChatViewModel? = null
    var adapter: ChatAdapter? = null
    var mUnregistrar: Unregistrar? = null
    var dbManager: DBManager? = null
    lateinit var friendListModel: FriendListModel
    var typeActivity: Int? = null
    var uerId: Int? = 0;
    var point: Int = 0;
    var sendMessage: Int = 0;
    var readMessage: Int = 0;
    var sendImage: Int = 0;
    var deductPoint: Boolean = false;
    companion object {
        var friendId: Int = 0
        var imageUri: Uri? = null
        var imageFilePath: String? = ""
        var imageFilePathNew = ""
    }

    override fun getLayoutId(): Int {
        uerId = SharePreferenceUtils.getInstances().getUserInfo()!!.id;
        System.out.println("diep ==============================uerId " + uerId)
        return R.layout.activity_chat
    }

    override fun socketConnected() {
        System.out.println("diep ==============================Socket CONNECTED -> do join")
        SocketSingleton.getInstance().joinRoom(friendId, if(deductPoint) readMessage else 0)
        SocketSingleton.getInstance().receiveMessage(onNewMessage())

    }

    override fun initView() {

        var pointInfo = SharePreferenceUtils.getInstances().getPointInfo();
        point = pointInfo?.points!!;
        sendMessage = pointInfo?.sendMessage;
        readMessage = pointInfo?.readMessage
        sendImage = pointInfo?.sendImage;
        print("sendMessage" + sendMessage.toString());

        extraData {
            friendId = it.getInt(Const.FRIEND_ID, 0)
            friendListModel = it.getParcelable(Const.KEY_FRIEND)!!
            typeActivity = it.getInt(Const.KEY_ACTYVITY, 0)
            deductPoint = it.getBoolean("point")
        }

        dbManager = DBManager(this)
        dbManager?.updateUnreadToReaded(""+friendId);

        SocketSingleton.getInstance().connect(this)
        if (SocketSingleton.getInstance().socketStatus()) {
            System.out.println("diep ==============================Socket socketStatus true > join " + friendId);
            SocketSingleton.getInstance().joinRoom(friendId, if(deductPoint) readMessage else 0)
        }

        if (typeActivity == 0) {// from list friend
            //var count_ads = SharePreferenceUtils.getInstances().getStartInfo().count_ads
            //   if (count_ads!! == BalloonchatApplication.ADS_CONFIG) {

            //   }else{
            initInterstitialAd(BaseActivity.FORM_CHAT)
            //}
        }

        chatViewModel = ChatViewModel()
        initToolbar()
        initData()
        initonclick()
        checkKeyboard()
        checkFriendStatus()
    }

    var createdTime: String? = "";
    private fun onNewMessage() = Emitter.Listener {
        val obj = it[0] as JSONObject
        var message = obj.optString("message")
        var sendId = obj.optInt("send_id")
        var receiveiId = obj.optInt("receive_id")
        var created = obj.optString("created")
        var type = obj.optString("type")

        var avatar = obj.optString("avatar")
        var gender = obj.optInt("gender")
        var account_id = obj.optInt("account_id")
        var revision = obj.optString("revision")
        var nationality = obj.optString("nationality")

        if (createdTime!!.equals(created)) {
            return@Listener
        }
        createdTime = created;
        friendListModel?.created = createdTime;
        runOnUiThread {
            if (friendId == sendId) {
                var lisFriendListModel = dbManager?.getAllMessage()
                if (lisFriendListModel?.size == 0) {
                    if (type.toInt() == 1) {
                        friendListModel?.message = getString(R.string.message_new)
                    } else {
                        friendListModel?.message = getString(R.string.message_image)
                    }
                    friendListModel?.message = message
                    dbManager?.addMessage(this!!.friendListModel!!)
                } else {
                    for (i in 0 until lisFriendListModel!!.size) {
                        if (lisFriendListModel.get(i).id != friendId) {
                            if (type.toInt() == 1) {
                                friendListModel?.message = getString(R.string.message_new)
                            } else {
                                friendListModel?.message = getString(R.string.message_image)
                            }
                            dbManager?.addMessage(this!!.friendListModel!!)
                        } else
                            if (lisFriendListModel.get(i).id == friendId) {
                                if (type.toInt() == 1) {
                                    friendListModel?.message = getString(R.string.message_new)
                                } else {
                                    friendListModel?.message = getString(R.string.message_image)
                                }
                                dbManager!!.modifyCredentials(
                                    friendId.toString(),
                                    friendListModel?.message.toString(), createdTime!!
                                )
                            }
                    }
                }
                chatViewModel!!.list?.add(
                    ChatModel(
                        content = message, sendId = sendId, receiveId = receiveiId
                        , time = created.toTimeFomatHHmm(), type = type, isMe = false
                    )
                )

            } else {
                var lisFriendListModel = dbManager?.getAllMessage()
                if (lisFriendListModel?.size == 0) {
                    if (type.toInt() == 1) {
                        friendListModel?.message = message
                    } else {
                        friendListModel?.message = getString(R.string.message_image_you_send)
                    }
                    dbManager?.addMessage(this!!.friendListModel!!)
                } else {
                    for (i in 0 until lisFriendListModel!!.size) {
                        if (lisFriendListModel.get(i).id != friendId) {
                            if (type.toInt() == 1) {
                                friendListModel?.message = message
                            } else {
                                friendListModel?.message =
                                    getString(R.string.message_image_you_send)
                            }
                            dbManager?.addMessage(this.friendListModel)
                        } else
                            if (lisFriendListModel.get(i).id == friendId) {
                                if (type.toInt() == 1) {
                                    dbManager!!.modifyCredentials(
                                        friendId.toString(),
                                        message,
                                        createdTime!!
                                    )
                                } else {
                                    dbManager!!.modifyCredentials(
                                        friendId.toString(),
                                        getString(R.string.message_image_you_send), createdTime!!
                                    )
                                }

                            }
                    }
                }
                chatViewModel!!.list?.add(
                    ChatModel(
                        content = message, sendId = sendId, receiveId = receiveiId
                        , time = created.toTimeFomatHHmm(), type = type, isMe = true
                    )
                )

            }
            adapter = ChatAdapter(uerId, chatViewModel?.list!!, friendListModel) {
                if (chatViewModel?.list!!.get(it!!).type!!.toInt() == 2) {
                    showDialogImage(chatViewModel?.list!!.get(it!!).content.toString())
                }
            }
            adapter?.notifyDataSetChanged()
            System.out.println("diep adapter.itemCount " + adapter!!.itemCount);
            rcv_chat.scrollToPosition(adapter!!.itemCount - 1)
            Handler().postDelayed({
                try {
                    rcv_chat.scrollToPosition(adapter!!.itemCount - 1)
                    System.out.println("diep adapter.itemCount " + adapter!!.itemCount);
                } catch (e: Exception) {
                }
            }, 3000)
            rcv_chat.adapter = adapter
        }

        if (!NotifyHelper.appInForeground(this)) run {
            println(" ==== DIEP isRunning BACKGROUND ")
            NotifyHelper.sendNotification(this,
                "chat", created, message, friendListModel?.nickname,
                avatar,
                gender,
                account_id,
                revision,
                nationality
            )
            return@Listener
        }
    }

    private fun showDialogImage(messageImage: String) {
        var dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_image)
        val view = (dialog).window
        view!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        Glide.with(this).load(messageImage).into(dialog.img_send)
        dialog.imvClose1.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun checkKeyboard() {
        mUnregistrar = KeyboardVisibilityEvent.registerEventListener(
            this!!
        ) { isOpen ->
            //Toast.makeText(getActivity(), "Keyboard Show: "+ isOpen, Toast.LENGTH_SHORT).show();
            showLayout(isOpen)
        }
    }

    private fun showLayout(open: Boolean) {
        if (open) {
            rcv_chat.scrollToPosition(chatViewModel?.list!!.size - 1)
        }
    }

    fun checkFriendStatus() {
        chatViewModel?.joinChat(friendId) {
            if (it == null) {
                if (chatViewModel?.is_blocked == 1 || chatViewModel?.is_friend_blocked == 1 || chatViewModel?.is_reported == 1 || chatViewModel?.is_friend_reported == 1) {
                    showAlertDialog(
                        message = getString(R.string.message_block),
                        positiveText = getString(R.string.ok),
                        clickPositive = {
                            goToActivity(HomeActivity::class.java)
                        })
                }
            }
            hideLoading()
        }
    }

    private fun initonclick() {
        btn_send.setOnClickListener {

            if (!TextUtils.isEmpty(edt_send.text.toString().trim())) {
                if(sendMessage > point) {
                    showDialogMessage(
                        this,
                        getString(R.string.no_point_send),
                        getString(R.string.ok)
                    )
                } else {
                    //doSendImageMessage();
                    SocketSingleton.getInstance().sendMessage(friendId, 1,sendMessage, edt_send.text.toString())
                    hideLoading()
                    edt_send.text.clear()
                    point -= sendMessage
                    SharePreferenceUtils.getInstances().updatePointInfo(point)
                }
            }
        }
        edt_send.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (edt_send.text.toString().trim().length == 0) {
                    //     btn_send.setBackgroundResource(R.drawable.btn_message_send_active)
                    btn_send.alpha =0.5f;
                } else {
                    btn_send.alpha = 1f;
                    btn_send.setBackgroundResource(R.drawable.btn_message_send_nomal)
                }
            }
        })

        img_camera.setOnClickListener {
            if(sendMessage > point) {
                showDialogMessage(
                    this,
                    getString(R.string.no_point_image),
                    getString(R.string.ok)
                )
            }else {
                showDialogChooser()
            }
        }

    }

    private fun initData() {
        var layoutManager =
            LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        rcv_chat.layoutManager = layoutManager
        showLoading()
        chatViewModel?.getHistoryChat(friendId, {
            if (it == null) {
                Collections.reverse(chatViewModel?.list!!)
                adapter = ChatAdapter(uerId, chatViewModel?.list!!, friendListModel)
                rcv_chat.adapter = adapter
                adapter?.notifyDataSetChanged()

                rcv_chat.scrollToPosition(adapter!!.itemCount - 1)
                Handler().postDelayed({
                    try {
                        rcv_chat.scrollToPosition(adapter!!.itemCount - 1)
                        System.out.println("diep adapter.itemCount " + adapter!!.itemCount);
                    } catch (e: Exception) {
                    }
                }, 3000)
            }
            hideLoading()
        })
        rcv_chat.adapter = adapter
    }

    private fun initToolbar() {
        rl_action_left.visibility = View.VISIBLE
        imv_action_left.setImageResource(R.drawable.ic_icon_back_p)
        //tv_title_toolbar.text = friendListModel?.nickname
        tvTitle.visibility = View.VISIBLE
        tvTitle.text = friendListModel?.nickname
        rl_action_left.setOnClickListener {
            finish()
        }

    }

    fun doSendImageMessage() {
        var file = File(imageFilePathNew)
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
        showLoading()
        chatViewModel?.sendMessageImage(friendId, image, sendImage) {
            if (it == null) {
                chatViewModel?.chatModel?.let { it1 -> adapter?.listChat?.add(it1) }
                adapter?.notifyDataSetChanged()
                hideLoading()
                point -= sendImage
                SharePreferenceUtils.getInstances().updatePointInfo(point)
            } else {
                showError(it)
            }
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
                context!!,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                context!!,
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
                REQUEST_CODE_GALLERY
            )
        }
    }

    @SuppressLint("NewApi")
    fun openCamera() {
        if (ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context!!,
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
                val photoFile = getTemporaryCameraFile()
                imageUri = FileProvider.getUriForFile(
                    applicationContext,
                    packageName + ".provider",
                    photoFile
                )
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
                startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            when (requestCode) {
                REQUEST_CODE_CAMERA -> if (resultCode == Activity.RESULT_OK) {
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
                    Utils.rotate(imageFilePathNew, imageUri!!, this)
                    val file1 = File(imageFilePath)
                    if (file1.exists()) {
                        file1.delete()
                    }
                    doSendImageMessage()
                }
                REQUEST_CODE_GALLERY -> if (resultCode == Activity.RESULT_OK) {
                    val imageUri:Uri = data!!.data!!
                    val fileNew = File(imageFilePathNew)
                    if (fileNew.exists()) {
                        fileNew.delete()
                    }
//                    imageFilePath = getRealPathFromURI(imageUri, this)
                    imageFilePath = Utils.getPath(this, imageUri)
                    imageFilePathNew = Utils.decodeFileStep1(
                        imageFilePath!!, 800, 800
                    )!!

//                    Utils.rotate(imageFilePathNew, imageUri, this)

                    doSendImageMessage()
                }
            }
        } catch (e: Exception) {
            print("ERRRRROR++++++++++++++++++++++++++++++++++++++++++++++++++"+e)
            Toast.makeText(context, "Please try again", Toast.LENGTH_LONG)
                .show()
        }

    }
    fun getRealPathFromURI(contentURI: Uri?, context: Activity): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.managedQuery(
            contentURI, projection, null,
            null, null
        ) ?: return null
        val column_index = cursor
            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        return if (cursor.moveToFirst()) {
            // cursor.close();
            cursor.getString(column_index)
        } else null
        // cursor.close();
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
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.CAMERA
                    )
                if (!showRationale) {
                    Utils.showDialogPermission(
                        this,
                        resources.getString(R.string.permission_camera)
                    )
                }
            }
            Const.READ_STORAGE_PERMISSION_CODE_PROFILE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                val showRationale =
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                if (!showRationale) {
                    Utils.showDialogPermission(
                        this,
                        resources.getString(R.string.permission_storage)
                    )
                }
            }
            else -> {
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SocketSingleton.getInstance().offSocket()
        mUnregistrar?.unregister()
    }
}