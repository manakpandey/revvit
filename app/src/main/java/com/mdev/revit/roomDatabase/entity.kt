package com.mdev.revit.roomDatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "faculty_details")
data class FacDetails(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val school: String,
    val designation: String
)
