package com.example

import kotlinx.coroutines.flow.Flow

class ThumbnailRepository(private val dao: ThumbnailHistoryDao) {
    val allHistory: Flow<List<ThumbnailHistory>> = dao.getAllHistory()

    suspend fun insert(history: ThumbnailHistory): Long {
        return dao.insert(history)
    }

    suspend fun deleteById(id: Int) {
        dao.deleteById(id)
    }

    suspend fun getById(id: Int): ThumbnailHistory? {
        return dao.getById(id)
    }
}
