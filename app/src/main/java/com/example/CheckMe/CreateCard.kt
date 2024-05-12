package com.example.CheckMe

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_create_card.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class CreateCard : AppCompatActivity() {
    private lateinit var database: myDatabase
    private var selectedDueDate: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_card)

        database = Room.databaseBuilder(
            applicationContext, myDatabase::class.java, "To_Do"
        ).build()

        val spinner: Spinner = findViewById(R.id.create_priority_spinner)
        val priorityArray = arrayOf("Select the priority", *resources.getStringArray(R.array.priority_array))

        val spinnerAdapter = ArrayAdapter(
            this,
            R.layout.spinner_dropdown_item, // Use custom layout for spinner items
            priorityArray
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        // Set default selection to "Select the priority"
        spinner.setSelection(0)

        // Set onClickListener for "Pick Due Date" button
        val pick_due_date_button = findViewById<Button>(R.id.pick_date_button)
        pick_due_date_button.setOnClickListener {
            showDatePickerDialog()
        }

        save_button.setOnClickListener {
            val title = create_title.text.toString().trim()
            val priority = spinner.selectedItem.toString()

            if (priority != "Select the priority" && title.isNotEmpty() && selectedDueDate > 0) {
                GlobalScope.launch {
                    database.dao().insertTask(Entity(0, title, priority, selectedDueDate))
                    DataObject.setData(title, priority, selectedDueDate)
                    runOnUiThread {
                        val intent = Intent(this@CreateCard, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            } else {
                Toast.makeText(this@CreateCard, "Please enter a task title, select priority, and pick a due date.", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(selectedYear, selectedMonth, dayOfMonth)
                selectedDueDate = selectedCalendar.timeInMillis
            },
            year,
            month,
            dayOfMonth
        )
        datePickerDialog.show()
    }
}
