package com.dicoding.todoapp.ui.detail

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.todoapp.R
import com.dicoding.todoapp.data.Task
import com.dicoding.todoapp.ui.ViewModelFactory
import com.dicoding.todoapp.utils.DateConverter
import com.google.android.material.textfield.TextInputEditText

class DetailTaskActivity : AppCompatActivity() {

    private lateinit var detailViewModel: DetailTaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        //TODO 11 : Show detail task and implement delete action

        detailViewModel = ViewModelProvider(
            this, ViewModelFactory.getInstance(this)
        )[DetailTaskViewModel::class.java]

        val taskId = intent.getIntExtra("TASK_ID", -1)

        detailViewModel.setTaskId(taskId)

        detailViewModel.task.observe(this) { task ->
            if (task != null) {
                updateUI(task)
            } else {
                Toast.makeText(this, "Task not found", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        val deleteButton: Button = findViewById(R.id.btn_delete_task)
        deleteButton.setOnClickListener {
            detailViewModel.deleteTask()
            finish()
        }
    }

    private fun updateUI(task: Task) {
        val titleEditText: TextInputEditText? = findViewById(R.id.detail_ed_title)
        val descriptionEditText: TextInputEditText? = findViewById(R.id.detail_ed_description)
        val dueDateEditText: TextInputEditText? = findViewById(R.id.detail_ed_due_date)

        titleEditText?.setText(task.title)
        descriptionEditText?.setText(task.description)

        val formattedDueDate = DateConverter.convertMillisToString(task.dueDateMillis)
        dueDateEditText?.setText(formattedDueDate)

        titleEditText?.isFocusable = true
        titleEditText?.isClickable = true
        descriptionEditText?.isFocusable = true
        descriptionEditText?.isClickable = true
        dueDateEditText?.isFocusable = true
        dueDateEditText?.isClickable = true
    }
}
