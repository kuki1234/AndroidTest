package com.example.androidtest

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "FitRunner.db"
        const val DATABASE_VERSION = 1

        const val TABLE_NAME = "steps_table"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_STEPS = "steps"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_STEPS INTEGER NOT NULL
            )
        """.trimIndent()
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertData(name: String, steps: Int): Boolean {
        val db = writableDatabase
        val query = "INSERT INTO $TABLE_NAME ($COLUMN_NAME, $COLUMN_STEPS) VALUES ('$name', $steps)"
        return try {
            db.execSQL(query)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getSuccessfulUsers(): List<String> {
        val db = readableDatabase
        val query = "SELECT $COLUMN_NAME FROM $TABLE_NAME WHERE $COLUMN_STEPS >= 10"
        val cursor = db.rawQuery(query, null)
        val names = mutableListOf<String>()
        if (cursor.moveToFirst()) {
            do {
                names.add(cursor.getString(0))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return names
    }

    fun isDatabaseEmpty(): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_NAME", null)
        var isEmpty = true
        if(cursor.moveToFirst()){
            isEmpty = cursor.getInt(0) == 0
        }
        cursor.close()
        return isEmpty
    }

    fun deleteAllData() {
        val db = writableDatabase
        db.execSQL("DELETE FROM $TABLE_NAME")
    }
}