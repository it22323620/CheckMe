package com.example.CheckMe

import androidx.room.*

@Dao
interface DAO {
    @Insert
    suspend fun insertTask(entity: Entity)

    @Update
    suspend fun updateTask(entity: Entity)

    @Delete
    suspend fun deleteTask(entity: Entity)

    @Query("DELETE FROM to_do")
    suspend fun deleteAll()

    @Query("SELECT * FROM to_do")
    suspend fun getTasks(): List<Entity>

    @Query("SELECT * FROM to_do WHERE id = :id")
    suspend fun getTaskById(id: Int): Entity

    @Query("DELETE FROM to_do WHERE id = :id")
    suspend fun deleteTaskById(id: Int)
}
