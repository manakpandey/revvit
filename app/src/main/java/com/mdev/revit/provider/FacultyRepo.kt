package com.mdev.revit.provider

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

class FacultyRepo(private val facultyDao: FacultyDao){


    fun search(query: String): LiveData<List<FacDetails>>{
        return facultyDao.search("%$query%")
    }

    @WorkerThread
    suspend fun insert(facDetails: FacDetails){
        facultyDao.insert(facDetails)
    }
}