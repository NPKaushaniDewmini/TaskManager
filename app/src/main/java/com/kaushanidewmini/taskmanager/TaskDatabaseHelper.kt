package com.kaushanidewmini.taskmanager

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TaskDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "taskmanager.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "alltasks"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_CONTENT = "content"
        private const val COLUMN_PRIORITY = "priority"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery =
            "CREATE TABLE $TABLE_NAME($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_TITLE TEXT, $COLUMN_CONTENT TEXT, $COLUMN_PRIORITY TEXT)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    suspend fun createTask(task: TaskDAO) {
        withContext(Dispatchers.IO) {
            val db = writableDatabase
            val values = ContentValues().apply {
                put(COLUMN_TITLE, task.title)
                put(COLUMN_CONTENT, task.content)
                put(COLUMN_PRIORITY, task.priority)
            }
            db.insert(TABLE_NAME, null, values)
            db.close()
        }
    }

    suspend fun getAllTasks(): List<TaskDAO> {
        return withContext(Dispatchers.IO) {
            val tasks = mutableListOf<TaskDAO>()
            val db = readableDatabase
            val query = "SELECT * FROM $TABLE_NAME"
            val cursor = db.rawQuery(query, null)

            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
                val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
                val priority = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRIORITY))

                val task = TaskDAO(id, title, content, priority)
                tasks.add(task)
            }

            cursor.close()
            db.close()

            tasks
        }
    }

    suspend fun updateTask(task: TaskDAO) {
        withContext(Dispatchers.IO) {
            val db = readableDatabase
            val values = ContentValues().apply {
                put(COLUMN_TITLE, task.title)
                put(COLUMN_CONTENT, task.content)
                put(COLUMN_PRIORITY, task.priority)
            }
            val whereClause = "$COLUMN_ID = ?"
            val whereArgs = arrayOf(task.id.toString())

            db.update(TABLE_NAME, values, whereClause, whereArgs)
            db.close()
        }
    }

    suspend fun getTaskByID(taskId: Int): TaskDAO {
        return withContext(Dispatchers.IO) {
            val db = readableDatabase
            val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = $taskId"
            val cursor = db.rawQuery(query, null)
            cursor.moveToFirst()

            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
            val priority = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRIORITY))

            cursor.close()
            db.close()

            TaskDAO(id, title, content, priority)
        }
    }

    suspend fun deleteTask(taskId: Int) {
        withContext(Dispatchers.IO) {
            val db = writableDatabase
            val whereClause = "$COLUMN_ID = ?"
            val whereArgs = arrayOf(taskId.toString())

            db.delete(TABLE_NAME, whereClause, whereArgs)
            db.close()
        }
    }
}