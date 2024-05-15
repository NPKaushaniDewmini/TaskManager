package com.kaushanidewmini.taskmanager

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kaushanidewmini.taskmanager.databinding.ActivityUpdateTaskBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UpdateTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateTaskBinding
    private val viewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(
            TaskDatabaseHelper(
                this
            )
        )
    }
    private var taskId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityUpdateTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        taskId = intent.getIntExtra("task_id", -1)

        if (taskId == -1) {
            finish()
            return
        }
        //fetch task details
        lifecycleScope.launch {
            viewModel.getTaskByID(taskId).collectLatest { task ->
                if (task != null) {
                    binding.updateTitleEditText.setText(task.title)
                    binding.updateContentEditText.setText(task.content)

                    val priorityIndex = when (task.priority) {
                        "Low" -> 0
                        "Medium" -> 1
                        else -> 2
                    }

                    binding.updatePrioritySpinner.setSelection(priorityIndex)
                }
            }
        }
// Retrieve updated task details from UI
        // Validate input
        // Create updated task object
        // Update task in the database
        // Show toast message indicating successful update
        // Finish the activity
        binding.updateTaskButton.setOnClickListener {
            val newTitle = binding.updateTitleEditText.text.toString()
            val newContent = binding.updateContentEditText.text.toString()
            val newPriority = binding.updatePrioritySpinner.selectedItem.toString()

            if (newTitle.isEmpty()) {
                Toast.makeText(this, "Please enter a title!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newContent.isEmpty()) {
                Toast.makeText(this, "Please enter content!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedTask = TaskDAO(taskId, newTitle, newContent, newPriority)
         //update task coroutine
            lifecycleScope.launch {
                viewModel.updateTask(updatedTask)

                runOnUiThread {
                    Toast.makeText(
                        this@UpdateTaskActivity,
                        "Task successfully updated!",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
        }

    }
}