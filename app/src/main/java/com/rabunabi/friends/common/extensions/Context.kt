package com.rabunabi.friends.common.extensions

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.rabunabi.friends.R

fun Context.call(phoneNumber: String?) {
    val intent: Intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber ?: ""))
    this.startActivity(intent)
}

fun Context.showError(message: String?) {
    this.showAlertDialog(message = message, positiveText = getString(R.string.close))
}

fun Context.showAlertDialog(
    title: String? = null,
    message: String?,
    positiveText: String,
    negativeText: String? = null,
    clickPositive: (() -> Unit)? = null,
    clickNegative: (() -> Unit)? = null
) {
    val alertDialog = AlertDialog.Builder(this)
    alertDialog.setTitle(title)
    alertDialog.setMessage(message)
    alertDialog.setPositiveButton(positiveText) { dialog, which ->
        dialog.dismiss()
        clickPositive?.invoke()
    }
    alertDialog.setNegativeButton(negativeText) { dialog, which ->
        dialog.dismiss()
        clickNegative?.invoke()
    }
    val alert = alertDialog.create()
    alert.show()
}
