package com.rabunabi.friends.firebase

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.rabunabi.friends.R

class PushResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val actionBar = supportActionBar
        actionBar!!.hide()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.push_dialog)
        initView()
    }

    fun initView() {
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            val title = bundle.getString("title") // 1
            val message = bundle.getString("message") // 1
            showDialog(title!!, message!!)
        } else {
            finish()
        }
    }

  /*  private fun showDialog2(title: String, message: String) {
        var dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_push)
        val view = (dialog).window
        view!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.imvClose.setOnClickListener {
            dialog.dismiss()
            finish()
        }
        dialog.setOnDismissListener {
            finish()
        }
        dialog.setOnCancelListener {
            finish()
        }
        dialog.show()

        dialog.tv_title_push.setText("" + title)
        dialog.tv_message_push.setText("" + message)
    }*/
     fun showDialog(title: String, message: String) {
         // Initialize a new instance of
         val builder = AlertDialog.Builder(this,R.style.DialogTheme)

         // Set the alert dialog title
         title?.let {
             builder.setTitle("" + title)
         }
         // Display a message on alert dialog
         builder.setMessage("" + message)

         // Set a positive button and its click listener on alert dialog
         builder.setPositiveButton(getString(R.string.ok)) { dialog, which ->
             finish()
         }

         // Finally, make the alert dialog using builder
         val dialog: AlertDialog = builder.create()
         dialog.setOnDismissListener {
             finish()
         }
         dialog.setOnCancelListener {
             finish()
         }
         // Display the alert dialog on app interface
         dialog.show()
     }
}