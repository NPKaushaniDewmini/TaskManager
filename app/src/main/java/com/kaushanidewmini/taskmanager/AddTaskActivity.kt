package com.kaushanidewmini.taskmanager

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kaushanidewmini.taskmanager.databinding.ActivityAddTaskBinding
import kotlinx.coroutines.launch

class AddTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTaskBinding
    private val viewModel: TaskViewModel by viewModels { TaskViewModelFactory(TaskDatabaseHelper(this)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        binding.saveButton.setOnClickListener {

            val title = binding.titleEditText.text.toString()
            val content = binding.contentEditText.text.toString()
            val priority = binding.prioritySpinner.selectedItem.toString()
          //validation
            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter a title!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (content.isEmpty()) {
                Toast.makeText(this, "Please enter content!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val task = TaskDAO(0, title, content, priority)
            //coroutine asynchronously create a new task
            lifecycleScope.launch {
                viewModel.createTask(task)
                runOnUiThread {
                    finish()
                    Toast.makeText(this@AddTaskActivity, "Task Created!", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}