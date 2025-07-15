package com.example.clearcanvas.data

import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.clearcanvas.viewmodel.JournalEntry


class JournalDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "journal.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE journal (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                productName TEXT,
                duration TEXT,
                helpful INTEGER
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS journal")
        onCreate(db)
    }

    fun insert(entry: JournalEntry): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("productName", entry.productName)
            put("duration", entry.duration)
            put("helpful", if (entry.helpful) 1 else 0)
        }
        return db.insert("journal", null, values)
    }

    fun getAll(): List<JournalEntry> {
        val list = mutableListOf<JournalEntry>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM journal", null)
        while (cursor.moveToNext()) {
            val id = cursor.getInt(0)
            val productName = cursor.getString(1)
            val duration = cursor.getString(2)
            val helpful = cursor.getInt(3) == 1
            list.add(JournalEntry(id, productName, duration, helpful))
        }
        cursor.close()
        return list
    }

    fun delete(id: Int) {
        writableDatabase.delete("journal", "id = ?", arrayOf(id.toString()))
    }

    fun update(entry: JournalEntry) {
        val values = ContentValues().apply {
            put("productName", entry.productName)
            put("duration", entry.duration)
            put("helpful", if (entry.helpful) 1 else 0)
        }
        writableDatabase.update("journal", values, "id = ?", arrayOf(entry.id.toString()))
    }
}
