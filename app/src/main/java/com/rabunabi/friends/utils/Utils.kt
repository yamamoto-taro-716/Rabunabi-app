package com.rabunabi.friends.utils

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.rabunabi.friends.BalloonchatApplication
import com.rabunabi.friends.R
import java.io.*
import java.net.URISyntaxException
import java.text.SimpleDateFormat
import java.util.*

class Utils {
    companion object {
        fun currentDate(dateFormat: String): String {
            val c = Calendar.getInstance()
            val dateformat = SimpleDateFormat(dateFormat)
            return dateformat.format(c.time)
        }


        fun readJSONFromAsset(fileName: String?): String? {
            var json: String? = null
            try {
                val inputStream: InputStream = BalloonchatApplication?.context?.assets!!.open(fileName!!)
                json = inputStream.bufferedReader().use { it.readText() }
            } catch (ex: Exception) {
                ex.printStackTrace()
                return null
            }
            return json
        }

        fun getTemporaryCameraFile(): File {
            val storageDir = File(getAppExternalDataDirectoryFile(), "Camera")
            storageDir.mkdirs()
            val file = File(storageDir, "CAMERA_" + System.currentTimeMillis() + ".jpg")
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return file
        }

        fun getLastUsedCameraFile(): File? {
            val dataDir = File(getAppExternalDataDirectoryFile(), "Camera")
            val files = dataDir.listFiles()
            val filteredFiles = ArrayList<File>()
            for (file in files) {
                if (file.name.startsWith("CAMERA_")) {
                    filteredFiles.add(file)
                }
            }

            Collections.sort(filteredFiles)
            return if (!filteredFiles.isEmpty()) {
                filteredFiles[filteredFiles.size - 1]
            } else {
                null
            }
        }

        fun getAppExternalDataDirectoryFile(): File {
            val dataDirectoryFile = File(getAppExternalDataDirectoryPath())
            dataDirectoryFile.mkdirs()

            return dataDirectoryFile
        }

        fun getAppExternalDataDirectoryPath(): String {
            val sb = StringBuilder()
            sb.append(Environment.getExternalStorageDirectory())
                .append(File.separator)
                .append("Android")
                .append(File.separator)
                .append("data")
                .append(File.separator)
                .append(BalloonchatApplication.context?.getPackageName())
                .append(File.separator)

            return sb.toString()
        }

        fun showDialogPermission(context: Context, message: String) {
            val alertBuilder = AlertDialog.Builder(context)
            alertBuilder.setCancelable(true)
            alertBuilder.setTitle(context.resources.getString(R.string.title_permission))
            alertBuilder.setMessage(message)
            alertBuilder.setPositiveButton(android.R.string.yes) { dialog, which ->
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null)
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
            alertBuilder.setNegativeButton(android.R.string.cancel) { dialog, which -> dialog.dismiss() }
            val alert = alertBuilder.create()
            if (!alert.isShowing) {
                alert.show()
            }
        }

        fun decodeFileStep1(path: String, DESIREDWIDTH: Int, DESIREDHEIGHT: Int): String? {
            var strMyImagePath: String? = null
            var scaledBitmap: Bitmap? = null

            try {
                // Part 1: Decode image
                val unscaledBitmap =
                    ScalingUtilities.decodeFile(path, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT)

                if (!(unscaledBitmap.getWidth() <= DESIREDWIDTH && unscaledBitmap.getHeight() <= DESIREDHEIGHT)) {
                    // Part 2: Scale image
                    scaledBitmap = ScalingUtilities.createScaledBitmap(
                        unscaledBitmap,
                        DESIREDWIDTH,
                        DESIREDHEIGHT,
                        ScalingUtilities.ScalingLogic.FIT
                    )
                } else {
                    unscaledBitmap.recycle()
                    return path
                }

                // Store to tmp file
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val imageFileName = "IMG_" + timeStamp + "_"
                val storageDir = BalloonchatApplication.context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                val file = File.createTempFile(imageFileName, ".png", storageDir)

                strMyImagePath = file.absolutePath
                var fos: FileOutputStream? = null
                try {
                    fos = FileOutputStream(file)
                    scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 75, fos)
                    fos.flush()
                    fos.close()
                } catch (e: FileNotFoundException) {

                    e.printStackTrace()
                } catch (e: Exception) {

                    e.printStackTrace()
                }

                scaledBitmap!!.recycle()
            } catch (e: Throwable) {
            }

            return strMyImagePath ?: path

        }

        fun rotate(filePath: String, uri: Uri, context: Context) {
            var cameraBitmap: Bitmap? = null
            var inputStream: InputStream? = null
            try {
                inputStream = context.contentResolver.openInputStream(uri)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

            val bmOptions = BitmapFactory.Options()
            bmOptions.inJustDecodeBounds = false
            bmOptions.inPurgeable = true
            bmOptions.inBitmap = cameraBitmap
            bmOptions.inMutable = true

            cameraBitmap = BitmapFactory.decodeFile(filePath, bmOptions)
            // Your image file path
            val bos = ByteArrayOutputStream()
            cameraBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, bos)


            var exif: ExifInterface? = null
            try {
                if (Build.VERSION.SDK_INT > 23)
                    exif = ExifInterface(inputStream!!)
                else
                    exif = ExifInterface(filePath)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            val rotation =
                exif!!.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL).toFloat()
            println(rotation)

            val rotationInDegrees =
                exifToDegrees(rotation)
            println(rotationInDegrees)

            val matrix = Matrix()
            matrix.postRotate(rotationInDegrees)

            val rotatedBitmap =
                Bitmap.createBitmap(cameraBitmap, 0, 0, cameraBitmap.width, cameraBitmap.height, matrix, true)
            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(filePath)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            try {
                fos!!.write(bos.toByteArray())
            } catch (e: IOException) {
                e.printStackTrace()
            }

            cameraBitmap.recycle()
            System.gc()
            try {
                fos!!.flush()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            try {
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        fun exifToDegrees(exifOrientation: Float): Float {
            val deviceModel = Build.MODEL
            val reqString = (Build.MANUFACTURER
                    + " " + Build.MODEL + " " + Build.VERSION.RELEASE
                    + " " + Build.VERSION_CODES::class.java.fields[android.os.Build.VERSION.SDK_INT].name)
            return if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90.toFloat()) {
                90f
            } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180.toFloat()) {
                180f
            } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270.toFloat()) {
                270f
            } else {
                if (reqString.contains("Sony")) {
                    90f
                } else
                    0f
            }

        }

        private fun isExternalStorageDocument(uri: Uri): Boolean {
            return "com.android.externalstorage.documents" == uri.authority
        }

        private fun isDownloadsDocument(uri: Uri): Boolean {
            return "com.android.providers.downloads.documents" == uri.authority
        }

        private fun isMediaDocument(uri: Uri): Boolean {
            return "com.android.providers.media.documents" == uri.authority
        }

        @SuppressLint("NewApi")
        @Throws(URISyntaxException::class)
        fun getPath(context: Context, uri: Uri): String? {
            var uri = uri
            val needToCheckUri = Build.VERSION.SDK_INT >= 19
            var selection: String? = null
            var selectionArgs: Array<String>? = null
            // Uri is different in versions after KITKAT (Android 4.4), we need to
            // deal with different Uris.
            if (needToCheckUri && DocumentsContract.isDocumentUri(context.applicationContext, uri)) {
                if (isExternalStorageDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]
                    if ("primary".equals(type, ignoreCase = true)) {
                        return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                    } else {
                        val files = ContextCompat.getExternalFilesDirs(context, null)
                        for (file in files) {
                            if (file.absolutePath.contains(type)) {
                                return file.parentFile.parentFile.parentFile.parentFile.toString() + "/" + split[1]
                            }
                        }
                    }
                } else if (isDownloadsDocument(uri)) {
                    val id = DocumentsContract.getDocumentId(uri)
                    uri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))
                } else if (isMediaDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]
                    if ("image" == type) {
                        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    } else if ("video" == type) {
                        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else if ("audio" == type) {
                        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    selection = "_id=?"
                    selectionArgs = arrayOf(split[1])
                }
            }
            if ("content".equals(uri.scheme!!, ignoreCase = true)) {
                val projection = arrayOf(MediaStore.Images.Media.DATA)
                var cursor: Cursor? = null
                try {
                    cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
                    assert(cursor != null)
                    val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    if (cursor.moveToFirst()) {
                        return cursor.getString(column_index)
                    }
                } catch (e: Exception) {
                }

            } else if ("file".equals(uri.scheme!!, ignoreCase = true)) {
                return uri.path
            }
            return null
        }
    }
}