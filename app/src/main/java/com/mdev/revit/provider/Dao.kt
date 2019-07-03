package com.mdev.revit.provider

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FacultyDao{
    @Insert
    suspend fun insert(facDetails: FacDetails)

    @Query("SELECT * FROM faculty_details WHERE name LIKE :queryName ORDER BY name")
    fun search(queryName: String): LiveData<List<FacDetails>>

    @Query("SELECT * FROM faculty_details")
    fun getAll(): LiveData<List<FacDetails>>

    @Query("DELETE from faculty_details")
    fun deleteAll()

    @Query("Select COUNT(id) FROM faculty_details")
    fun count(): List<Int>
}
