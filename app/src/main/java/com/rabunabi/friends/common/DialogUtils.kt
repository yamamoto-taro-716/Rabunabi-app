package com.rabunabi.friends.common

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.rabunabi.friends.R
import com.rabunabi.friends.view.dialog.DialogMessage
import com.rabunabi.friends.view.dialog.DialogMessage.OnDialogMessageListener


object DialogUtils {
    var dialog: AlertDialog? = null

    fun showLoadingDialog(context: Context?) {
        var dialogBuilder = AlertDialog.Builder(context, R.style.CustomProgress)
        val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView = inflater.inflate(R.layout.progress_dialog, null)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setCancelable(true)
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

    fun showDialogMessage(
        context: Context?,
        message: String?,
        textButton: String?,
        listener: OnDialogMessageListener?
    ) {
        val dialogMessage = DialogMessage(
            context!!,
            message,
            textButton,
            listener
        )
        dialogMessage.show()
    }

    fun showDialogMessage(
        context: Context?,
        message: String?,
        textButton: String?
    ) {
        val dialogMessage =
            DialogMessage(
                context!!, message, textButton,
                OnDialogMessageListener { })
        dialogMessage.show()
    }

    fun showDialogMessage(
        context: Context?,
        message: String?
    ) {
        val dialogMessage =
            DialogMessage(
                context!!, message, context.resources.getString(R.string.close),
                OnDialogMessageListener { })
        dialogMessage.show()
    }

}