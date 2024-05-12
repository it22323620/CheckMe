package com.example.CheckMe


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.CheckMe.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


// entity - table
// dao - queries
// database

class MainActivity : AppCompatActivity() {
    private lateinit var database: myDatabase
    private lateinit var adapter: Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        database = Room.databaseBuilder(
            applicationContext, myDatabase::class.java, "To_Do"
        ).build()

        add.setOnClickListener {
            val intent = Intent(this, CreateCard::class.java)
            startActivity(intent)
        }

        deleteAll.setOnClickListener {
            DataObject.deleteAll()
            GlobalScope.launch {
                database.dao().deleteAll()
                runOnUiThread {
                    adapter.notifyDataSetChanged()
                }
            }
        }

        setRecycler()
    }

    override fun onResume() {
        super.onResume()
        setRecycler()
    }

    private fun setRecycler() {
        GlobalScope.launch {
            DataObject.listdata = database.dao().getTasks() as MutableList<Entity>
            runOnUiThread {
                adapter = Adapter(DataObject.getAllData())
                recycler_view.adapter = adapter
                recycler_view.layoutManager = LinearLayoutManager(this@MainActivity)
            }
        }
    }
}