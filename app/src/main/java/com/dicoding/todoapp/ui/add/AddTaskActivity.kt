package com.dicoding.todoapp.ui.add

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.todoapp.R
import com.dicoding.todoapp.data.Task
import com.dicoding.todoapp.ui.ViewModelFactory
import com.dicoding.todoapp.utils.DatePickerFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddTaskActivity : AppCompatActivity(), DatePickerFragment.DialogDateListener {
    private var dueDateMillis: Long = System.currentTimeMillis()
    private lateinit var addTaskViewModel: AddTaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        addTaskViewModel = ViewModelProvider(
            this, ViewModelFactory.getInstance(applicationContext)
        )[AddTaskViewModel::class.java]


        supportActionBar?.title = getString(R.string.add_task)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {

                //TODO 12 : Create AddTaskViewModel and insert new task to database

                val titleEditText: EditText = findViewById(R.id.add_ed_title)
                val descriptionEditText: EditText = findViewById(R.id.add_ed_description)
                val title = titleEditText.text.toString()
                val description = descriptionEditText.text.toString()
                if (title.isBlank()) {
                    Toast.makeText(this, getString(R.string.empty_task_message), Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val newTask = Task(0, title, description, dueDateMillis)
                    addTaskViewModel.insertTask(newTask)
                    finish()
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    fun showDatePicker(view: View) {
        val dialogFragment = DatePickerFragment()
        dialogFragment.show(supportFragmentManager, "datePicker")
    }

    override fun onDialogDateSet(tag: String?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        findViewById<TextView>(R.id.add_tv_due_date).text = dateFormat.format(calendar.time)

        dueDateMillis = calendar.timeInMillis
    }
}