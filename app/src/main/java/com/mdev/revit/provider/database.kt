package com.mdev.revit.provider

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [FacDetails::class], version = 1, exportSchema = false)
abstract class TheDatabase: RoomDatabase(){
    abstract fun facultyDao(): FacultyDao

    companion object {
        @Volatile
        private var INSTANCE: TheDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): TheDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TheDatabase::class.java,
                    "The_database"
                ).addCallback(DatabaseCallback(scope)).build()
                INSTANCE = instance
                return instance
            }
        }


        private class DatabaseCallback(private val scope: CoroutineScope): RoomDatabase.Callback(){
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let {database ->
                    scope.launch(Dispatchers.IO){
                        val dao = database.facultyDao()
                        val fireStore = FirebaseFirestore.getInstance()
                        val count = dao.count()[0]
                        val fireCount = fireStore.collection("counters")
                            .document("search").get()
                        try {
                            if (Tasks.await(fireCount)["num_of_fac"].toString() != count.toString()) {
                                val docs = fireStore.collection("fac_details").get()
                                dao.deleteAll()
                                for (i in Tasks.await(docs)) {
                                    dao.insert(
                                        FacDetails(
                                            0
                                            , i["name"].toString()
                                            , i["school"].toString()
                                            , i["designation"].toString()
                                        )
                                    )
                                }
                            }
                        } catch (e: Throwable){
                            Log.d("TheDatabaseLog","Error Occurred: $e")
                        }
                    }
                }
            }
        }
    }
}