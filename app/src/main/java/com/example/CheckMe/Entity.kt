// Entity.kt
package com.example.CheckMe

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "To_Do")
data class Entity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var title: String,
    var priority: String,
    var dueDate: Long // Add due date field
)
