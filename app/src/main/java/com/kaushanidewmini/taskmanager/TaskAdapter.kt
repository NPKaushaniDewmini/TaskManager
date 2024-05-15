package com.kaushanidewmini.taskmanager

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class TaskAdapter(private var tasks: List<TaskDAO>, private val context: Context) :// Initialize TaskDatabaseHelper for database operations
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val db = TaskDatabaseHelper(context)
    // ViewHolder class to hold references to views in the task item layout
    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        val priorityTextView: TextView = itemView.findViewById(R.id.priorityTextView)
        val updateButton: ImageView = itemView.findViewById(R.id.updateButton)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
    }
    // Create ViewHolder instances
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }
    // Return the number of items in the dataset
    override fun getItemCount(): Int = tasks.size
    // Bind data to views within a ViewHolder
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.titleTextView.text = task.title
        holder.contentTextView.text = task.content
        holder.priorityTextView.text = "${task.priority} priority"
// Set click listener for the update button
        holder.updateButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, UpdateTaskActivity::class.java).apply {
                putExtra("task_id", task.id)
            }
            holder.itemView.context.startActivity(intent)
        }
// Set click listener for the delete button
        holder.deleteButton.setOnClickListener {
            val activity = context as? AppCompatActivity
            activity?.lifecycleScope?.launch {
                db.deleteTask(task.id) //delete task
                refreshData(db.getAllTasks())//refresh database

                activity.runOnUiThread {
                    Toast.makeText(context, "Task Deleted!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
//refresh data
    fun refreshData(newTasks: List<TaskDAO>) {
        tasks = newTasks
        notifyDataSetChanged()
    }


}