package com.example.todo.data

import android.content.Context
import com.example.todo.model.Task
import com.google.gson.Gson

class TaskRepository(
    context: Context
) {
    private val sharedPreferences = context.getSharedPreferences("TaskPrefs", Context.MODE_PRIVATE)
    private val gson = Gson() // for converting objects to JSON and back

    // Function to save tasks to SharedPreferences
    fun saveTasks(tasks: List<Task>) {
        val tasksJson = gson.toJson(tasks)
        sharedPreferences.edit().putString("tasks_key", tasksJson).apply()
    }

    fun loadTasks(): List<Task> {
        val tasksJson = sharedPreferences.getString("tasks_key", null)
        return if (tasksJson != null) {
            gson.fromJson(tasksJson, Array<Task>::class.java).toList()
        } else {
            emptyList()
        }
    }
}