package com.vinoviss.bitcoindic.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DatabaseHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "bitcoin.db"
        private const val DATABASE_VERSION = 1
        private const val TAG = "DatabaseHelper"
    }

    init {
        // Check if the database exists, if not copy it from assets
        if (!checkDatabaseExists()) {
            try {
                copyDatabaseFromAssets()
            } catch (e: IOException) {
                Log.e(TAG, "Error copying database from assets", e)
                throw RuntimeException("Error copying database from assets", e)
            }
        }
    }

    private fun checkDatabaseExists(): Boolean {
        val dbFile = context.getDatabasePath(DATABASE_NAME)
        return dbFile.exists()
    }

    private fun copyDatabaseFromAssets() {
        // Create the database file
        val dbFile = context.getDatabasePath(DATABASE_NAME)

        // Make sure the database directory exists
        dbFile.parentFile?.mkdirs()

        // Copy the database from assets
        context.assets.open("bitcoind").use { input ->
            FileOutputStream(dbFile).use { output ->
                val buffer = ByteArray(1024)
                var length: Int
                while (input.read(buffer).also { length = it } > 0) {
                    output.write(buffer, 0, length)
                }
                output.flush()
            }
        }

        Log.i(TAG, "Database copied successfully from assets")
    }

    override fun onCreate(db: SQLiteDatabase) {
        // We don't need to create tables as we're copying the database from assets
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // We would implement database migration logic here in a real app
    }

    suspend fun getDatabasePath(): File = withContext(Dispatchers.IO) {
        context.getDatabasePath(DATABASE_NAME)
    }
}