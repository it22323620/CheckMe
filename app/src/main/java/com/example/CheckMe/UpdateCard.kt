// UpdateCard.kt
package com.example.CheckMe

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_update_card.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class UpdateCard : AppCompatActivity() {
    private lateinit var database: myDatabase
    private lateinit var adapter: Adapter
    private var dueDate: Long = 0 // Variable to store due date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_card)

        // Initialize the adapter
        adapter = Adapter(emptyList())

        // Initialize the database
        database = Room.databaseBuilder(
            applicationContext, myDatabase::class.java, "To_Do"
        ).build()

        // Get the position from the intent
        val pos = intent.getIntExtra("id", -1)

        if (pos != -1) {
            // Fetch the current task details
            val currentTask = DataObject.getData(pos)
            create_title.setText(currentTask.title)

            // Set the selected due date
            dueDate = currentTask.dueDate

            // Update the due date TextView
            updateDueDateTextView()

            // Set up the priority spinner
            val spinner: Spinner = findViewById(R.id.update_priority_spinner)
            val priorityArray = arrayOf("Select the priority", *resources.getStringArray(R.array.priority_array))

            val spinnerAdapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                priorityArray
            )
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = spinnerAdapter

            // Find the index of the current priority in the priority array
            val currentPriorityIndex = priorityArray.indexOf(currentTask.priority)

            // Set the selection of the spinner to the current priority
            if (currentPriorityIndex != -1) {
                spinner.setSelection(currentPriorityIndex)
            }

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position == 0) {
                        (view as? TextView)?.setTextColor(resources.getColor(android.R.color.darker_gray))
                    } else {
                        (view as? TextView)?.setTextColor(resources.getColor(android.R.color.black))
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Do nothing
                }
            }

            // Set onClickListener for "Pick Due Date" button
            val pick_due_date_button = findViewById<Button>(R.id.pick_date_button)
            pick_due_date_button.setOnClickListener {
                showDatePickerDialog()
            }

            delete_button.setOnClickListener {
                // Delete the task from DataObject and database
                DataObject.deleteData(pos)
                GlobalScope.launch {
                    database.dao().deleteTask(currentTask)
                    updateAdapter()
                    myIntent()
                }
            }

            update_button.setOnClickListener {
                // Update the task in DataObject and database
                val updatedTitle = create_title.text.toString()
                val updatedPriority = spinner.selectedItem.toString()

                if (updatedPriority != "Select the priority") {
                    DataObject.updateData(pos, updatedTitle, updatedPriority, dueDate)
                    GlobalScope.launch {
                        database.dao().updateTask(
                            Entity(
                                currentTask.id, updatedTitle, updatedPriority, dueDate
                            )
                        )
                        updateAdapter()
                        myIntent()
                    }
                } else {
                    Toast.makeText(this@UpdateCard, "Please select priority.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun updateAdapter() {
        // Fetch the latest data and update the adapter
        DataObject.listdata = database.dao().getTasks() as MutableList<Entity>
        runOnUiThread {
            adapter.data = DataObject.getAllData()
            adapter.notifyDataSetChanged()
        }
    }

    private fun myIntent() {
        // Navigate back to MainActivity
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
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
                dueDate = selectedCalendar.timeInMillis
                updateDueDateTextView() // Update due date TextView
            },
            year,
            month,
            dayOfMonth
        )
        datePickerDialog.show()
    }

    private fun updateDueDateTextView() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDueDate = dateFormat.format(Date(dueDate))
        due_date_text.text = "Due Date: $formattedDueDate"
    }
}

