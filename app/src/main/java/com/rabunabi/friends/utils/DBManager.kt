package com.rabunabi.friends.utils

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.rabunabi.friends.model.ReloadMessageEvent
import com.rabunabi.friends.model.friends.FriendListModel
import org.greenrobot.eventbus.EventBus
import java.util.*


class DBManager(val context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME, null, 2
) {
    companion object {
        val DATABASE_NAME = "SAVE"
        val TABLE_NAME = "SAVE"
        val ID = "id"
        val ID_NAME = "ID_NAME"
        val AVARTAR = "AVARTAR"
        val GENDER = "GENDER"
        val NATIONAL = "NATIONAL"
        val NAME = "NAME"
        val MESSAGE = "MESSAGE"
        val TIME = "TIME"
        val NOTIFY = "NOTIFY"
        val REVISION = "REVISION"
        val AGE = "AGE"
        val INTRO = "INTRO"
        val UNREAD = "UNREAD"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val sqlQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                ID + " integer primary key, " +
                ID_NAME + " TEXT, " +
                AVARTAR + " TEXT, " +
                NAME + " TEXT, " +
                GENDER + " TEXT, " +
                NATIONAL + " TEXT, " +
                MESSAGE + " TEXT, " +
                REVISION + " TEXT, " +
                NOTIFY + " TEXT, " +
                TIME + " TEXT, " +
                AGE + " AGE, " +
                INTRO + " INTRO, " +
                UNREAD + " INTEGER)"
        db?.execSQL(sqlQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun getAllMessage(): MutableList<FriendListModel> {
        var listMessageModel = ArrayList<FriendListModel>()
        val selectQuery = "SELECT  * FROM $TABLE_NAME"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        listMessageModel.addAll(printCusor(cursor));
        cursor.close()
        db.close()
        return listMessageModel
    }

    fun printCusor(cursor: Cursor): MutableList<FriendListModel> {
        val listMessageModel = ArrayList<FriendListModel>()
        var totalUnread = 0
        if (cursor.moveToFirst()) {
            do {
                val sb = StringBuilder()
                val columnsQty = cursor.getColumnCount()

                val messageModel = FriendListModel()
                for (idx in 0 until columnsQty) {
                    if (idx == 1)
                        messageModel.id = cursor.getInt(1)
                    if (idx == 2)
                        messageModel.avatar = cursor.getString(2)
                    if (idx == 3)
                        messageModel.nickname = cursor.getString(3)
                    if (idx == 4)
                        messageModel.gender = cursor.getInt(4)
                    if (idx == 5)
                        messageModel.nationality = cursor.getString(5)
                    if (idx == 6)
                        messageModel.message = cursor.getString(6)
                    if (idx == 7)
                        messageModel.revision = cursor.getString(7)
                    if (idx == 8)
                        messageModel.notify = cursor.getString(8)
                    if (idx == 9)
                        messageModel.created = cursor.getString(9)
                    if (idx == 10)
                        messageModel.age = cursor.getInt(10)
                    if (idx == 11)
                        messageModel.intro = cursor.getString(11)
                    if (idx == 12)
                        messageModel.totalUnread = cursor.getInt(12)

                    sb.append(cursor.getString(idx))
                    if (idx < columnsQty - 1)
                        sb.append("; ")
                }
                listMessageModel.add(messageModel)
                /*System.out.println(
                    " xDIEP " + String.format(
                        "Row: %d, Values: %s",
                        cursor.getPosition(),
                        sb.toString()
                    )
                )*/
            } while (cursor.moveToNext())
        }
        return listMessageModel;
    }

    fun deleteSave(id: Int): Boolean {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, "$ID_NAME=$id", null) > 0
    }

    fun addMessage(messageModel: FriendListModel) {
        if (rowIdExists(messageModel.id.toString())) {

        } else {
            val db = this.writableDatabase
            val values = ContentValues()
            values.put(ID_NAME, messageModel.id)
            values.put(AVARTAR, messageModel.avatar)
            values.put(NAME, messageModel.nickname)
            values.put(GENDER, messageModel.gender)
            values.put(NATIONAL, messageModel.nationality)
            values.put(MESSAGE, messageModel.message)
            values.put(REVISION, messageModel.revision)
            values.put(NOTIFY, messageModel.notify)
            values.put(TIME, messageModel.created)
            values.put(AGE, messageModel.age)
            values.put(INTRO, messageModel.intro)
            values.put(UNREAD, 0)
            db.insert(TABLE_NAME, null, values)
            db.close()
        }
    }

    fun updateMessage2(messageModel: FriendListModel) {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(ID_NAME, messageModel.id)
        values.put(AVARTAR, messageModel.avatar)
        values.put(NAME, messageModel.nickname)
        values.put(GENDER, messageModel.gender)
        values.put(NATIONAL, messageModel.nationality)
        values.put(MESSAGE, messageModel.message)
        values.put(REVISION, messageModel.revision)
        values.put(NOTIFY, messageModel.notify)
        values.put(TIME, messageModel.created)
        values.put(AGE, messageModel.age)
        values.put(INTRO, messageModel.intro)

        var friendListModel = getMessageById(messageModel.id!!)
        if (friendListModel != null) {
            var totalUnread = friendListModel?.totalUnread
            if (totalUnread != null) {
                totalUnread ++
                values.put(UNREAD, totalUnread)
            }
            val whereClause = ID_NAME + " =?"
            val whereArgs = arrayOf("" + messageModel.id)
            var updateResule = db.update(TABLE_NAME, values, whereClause, whereArgs)

            System.out.println("diep update updateMessage2 : " + updateResule + "  totalUnread " + totalUnread + " time "+messageModel.created)
        } else {
            values.put(UNREAD, 1)
            System.out.println("diep add")
            addMessage(messageModel)
        }
        try {
            // bắn eventbus xử lý để ko hiển thị người vừa block hiển thị lên list
            var b = ReloadMessageEvent();
            EventBus.getDefault().post(b)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getMessageById(id: Int): FriendListModel? {
        val db = this.readableDatabase

        var cursor: Cursor
        cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE ID_NAME = " + id, null);
        if (cursor != null && cursor.moveToFirst()) {
            //num = cursor.getString(cursor.getColumnIndex("ContactNumber"));
            val friendListModel = FriendListModel(
                cursor!!.getInt(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getInt(4),
                cursor.getString(5),
                cursor.getString(6),
                cursor.getString(7),
                cursor.getString(8)
            )
            friendListModel.totalUnread = cursor.getInt(12)

            cursor.close()

            return friendListModel
        }
        db.close()
        return null
    }

    fun deleteAll() {
        val db = this.writableDatabase
        db.execSQL("delete from $TABLE_NAME")
        db.close()
    }

    fun deleteMessage(id: String): Int {
        val db = this.writableDatabase
        val whereArgs = arrayOf("" + id)
        return db.delete(
            TABLE_NAME,
            "$ID=?",
            whereArgs
        )
    }

    fun updateMessage(friendListModel: FriendListModel): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(AVARTAR, friendListModel.avatar)

        //contentValues.put(ID_NAME, friendListModel.id)
        contentValues.put(AVARTAR, friendListModel.avatar)
        contentValues.put(NAME, friendListModel.nickname)
        contentValues.put(GENDER, friendListModel.gender)
        contentValues.put(NATIONAL, friendListModel.nationality)
        //contentValues.put(MESSAGE, messageModel.message)
        //contentValues.put(REVISION, messageModel.revision)
        //contentValues.put(NOTIFY, messageModel.notify)
        //contentValues.put(TIME, messageModel.created)

        contentValues.put(AGE, friendListModel.age)
        contentValues.put(INTRO, friendListModel.intro)

        val whereArgs = arrayOf("" + friendListModel.id)
        return db.update(
            TABLE_NAME,
            contentValues,
            "$ID=?",
            whereArgs
        )
    }
    fun updateUnreadToReaded(idFriend: String) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(UNREAD, 0)
        val whereClause = ID_NAME + " =?"
        val whereArgs = arrayOf(idFriend)

        db.update(TABLE_NAME, contentValues, whereClause, whereArgs)
        db.close()

        try {
            // bắn eventbus xử lý để ko hiển thị người vừa block hiển thị lên list
            var b = ReloadMessageEvent();
            EventBus.getDefault().post(b)
        } catch (e: Exception) {
        }
    }
    fun modifyCredentials(idFriend: String, newMessage: String, time: String) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(MESSAGE, newMessage)
        contentValues.put(TIME, time)
        contentValues.put(UNREAD, 0)
        val whereClause = ID_NAME + " =?"
        val whereArgs = arrayOf(idFriend)

        db.update(TABLE_NAME, contentValues, whereClause, whereArgs)
        db.close()
    }

    fun rowIdExists(id: String): Boolean {
        val db = this.writableDatabase
        val cursor = db.rawQuery(
            "select 1 from " + TABLE_NAME
                    + " where ID_NAME=?", arrayOf("" + id)
        )
        val exists = (cursor.count > 0)
        cursor.close()
        return exists
    }
}