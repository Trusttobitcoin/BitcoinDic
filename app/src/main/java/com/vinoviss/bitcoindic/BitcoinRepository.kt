package com.vinoviss.bitcoindic.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class BitcoinRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    // Get all terms ordered by English name
    fun getAllTermsOrderedByEngName(): Flow<List<BitcoinTerm>> = flow {
        val terms = mutableListOf<BitcoinTerm>()
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            "maintbl",
            arrayOf("_id", "engname", "faname", "description"),
            null,
            null,
            null,
            null,
            "engname ASC"
        )

        cursor.use {
            val idIndex = it.getColumnIndex("_id")
            val engNameIndex = it.getColumnIndex("engname")
            val faNameIndex = it.getColumnIndex("faname")
            val descriptionIndex = it.getColumnIndex("description")

            while (it.moveToNext()) {
                val term = BitcoinTerm(
                    id = it.getInt(idIndex),
                    engname = it.getString(engNameIndex) ?: "",
                    faname = it.getString(faNameIndex) ?: "",
                    description = it.getString(descriptionIndex) ?: ""
                )
                terms.add(term)
            }
        }

        emit(terms)
    }.flowOn(Dispatchers.IO)

    // Get all terms ordered by Farsi name
    fun getAllTermsOrderedByFaName(): Flow<List<BitcoinTerm>> = flow {
        val terms = mutableListOf<BitcoinTerm>()
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            "maintbl",
            arrayOf("_id", "engname", "faname", "description"),
            null,
            null,
            null,
            null,
            "faname ASC"
        )

        cursor.use {
            val idIndex = it.getColumnIndex("_id")
            val engNameIndex = it.getColumnIndex("engname")
            val faNameIndex = it.getColumnIndex("faname")
            val descriptionIndex = it.getColumnIndex("description")

            while (it.moveToNext()) {
                val term = BitcoinTerm(
                    id = it.getInt(idIndex),
                    engname = it.getString(engNameIndex) ?: "",
                    faname = it.getString(faNameIndex) ?: "",
                    description = it.getString(descriptionIndex) ?: ""
                )
                terms.add(term)
            }
        }

        emit(terms)
    }.flowOn(Dispatchers.IO)

    // Search in English terms
    fun searchByEngName(query: String): Flow<List<BitcoinTerm>> = flow {
        val terms = mutableListOf<BitcoinTerm>()
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            "maintbl",
            arrayOf("_id", "engname", "faname", "description"),
            "engname LIKE ? OR description LIKE ?",  // Search in both name and description
            arrayOf("%$query%", "%$query%"),
            null,
            null,
            "engname ASC"
        )

        cursor.use {
            val idIndex = it.getColumnIndex("_id")
            val engNameIndex = it.getColumnIndex("engname")
            val faNameIndex = it.getColumnIndex("faname")
            val descriptionIndex = it.getColumnIndex("description")

            while (it.moveToNext()) {
                val term = BitcoinTerm(
                    id = it.getInt(idIndex),
                    engname = it.getString(engNameIndex) ?: "",
                    faname = it.getString(faNameIndex) ?: "",
                    description = it.getString(descriptionIndex) ?: ""
                )
                terms.add(term)
            }
        }

        emit(terms)
    }.flowOn(Dispatchers.IO)

    // Search in Farsi terms
    fun searchByFaName(query: String): Flow<List<BitcoinTerm>> = flow {
        val terms = mutableListOf<BitcoinTerm>()
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            "maintbl",
            arrayOf("_id", "engname", "faname", "description"),
            "faname LIKE ? OR description LIKE ?",  // Search in both name and description
            arrayOf("%$query%", "%$query%"),
            null,
            null,
            "faname ASC"
        )

        cursor.use {
            val idIndex = it.getColumnIndex("_id")
            val engNameIndex = it.getColumnIndex("engname")
            val faNameIndex = it.getColumnIndex("faname")
            val descriptionIndex = it.getColumnIndex("description")

            while (it.moveToNext()) {
                val term = BitcoinTerm(
                    id = it.getInt(idIndex),
                    engname = it.getString(engNameIndex) ?: "",
                    faname = it.getString(faNameIndex) ?: "",
                    description = it.getString(descriptionIndex) ?: ""
                )
                terms.add(term)
            }
        }

        emit(terms)
    }.flowOn(Dispatchers.IO)

    // Get a single term by ID
    suspend fun getTermById(id: Int): BitcoinTerm? = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            "maintbl",
            arrayOf("_id", "engname", "faname", "description"),
            "_id = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        cursor.use {
            if (it.moveToFirst()) {
                val idIndex = it.getColumnIndex("_id")
                val engNameIndex = it.getColumnIndex("engname")
                val faNameIndex = it.getColumnIndex("faname")
                val descriptionIndex = it.getColumnIndex("description")

                return@withContext BitcoinTerm(
                    id = it.getInt(idIndex),
                    engname = it.getString(engNameIndex) ?: "",
                    faname = it.getString(faNameIndex) ?: "",
                    description = it.getString(descriptionIndex) ?: ""
                )
            }
        }

        return@withContext null
    }
}