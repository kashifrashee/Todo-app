package com.example.todo.data

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.todo.model.Task

class PreviewTaskViewModel : ViewModel() {
    private  val _tasks = mutableStateListOf(
        Task(1, "Task 1", "Description 1"),
        Task(2, "Task 2", "Description 2"),
        Task(3, "Task 3", "Description 3")
    )
    val tasks: List<Task> get() = _tasks

    fun toggleTaskCompletion(task: Task) {
        val index = _tasks.indexOf(task)
        if (index != -1) {
            _tasks[index] = task.copy(isCompleted = !task.isCompleted)
        }
    }

    fun deleteTask(task: Task) {
        _tasks.remove(task)
    }
}