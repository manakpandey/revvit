package com.mdev.revit

import android.app.Application
import androidx.lifecycle.*
import com.mdev.revit.roomDatabase.FacDetails
import com.mdev.revit.roomDatabase.FacultyRepo
import com.mdev.revit.roomDatabase.TheDatabase

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