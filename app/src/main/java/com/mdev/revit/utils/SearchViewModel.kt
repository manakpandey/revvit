package com.mdev.revit.utils

import android.app.Application
import androidx.lifecycle.*
import com.mdev.revit.provider.FacDetails
import com.mdev.revit.provider.FacultyRepo
import com.mdev.revit.provider.TheDatabase

class SearchViewModel(application: Application): AndroidViewModel(application){

    private val repository: FacultyRepo
    private val query = MutableLiveData<String>()
    var searchResults: LiveData<List<FacDetails>>

    init {
        val facultyDao = TheDatabase.getDatabase(application,viewModelScope).facultyDao()
        repository = FacultyRepo(facultyDao)
        searchResults = Transformations.switchMap(query) { repository.search(query.value!!) }
    }

   fun searchByName(name: String){
       query.value = name
   }
}