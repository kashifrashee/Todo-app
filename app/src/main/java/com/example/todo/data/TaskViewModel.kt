package com.example.todo.data

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.model.Task
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    // Initialize the shared preferences repository
    private val taskRepository = TaskRepository(application)

    // LiveData to observe tasks
    private val _tasks = mutableStateListOf<Task>()
    val tasks: List<Task>  get() = _tasks

    // Load tasks from the shared preferences repository when the ViewModel is created
    init {
        _tasks.addAll(taskRepository.loadTasks())
    }

    // Function to update a task
    fun updateTask(updatedTask: Task) {
        val index = _tasks.indexOfFirst { it.id == updatedTask.id }
        if (index != -1) {
            _tasks[index] = updatedTask
            taskRepository.saveTasks(_tasks) // Save tasks to shared preferences
        }
    }

    fun addTask(
        title: String,
        description: String
    ){
        val newTask = Task(
            id = _tasks.size + 1,
            title = title,
            description = description
        )
        _tasks.add(newTask) // Add the new task to the list
        taskRepository.saveTasks(_tasks) // Save tasks to shared preferences
    }

    fun deleteTask(task: Task){
        _tasks.remove(task) // Remove the task from the list
        taskRepository.saveTasks(_tasks) // Save tasks to shared preferences
    }

    fun toggleTaskCompletion(task: Task){
        val index = _tasks.indexOf(task)
        if (index != -1){
            _tasks[index] = task.copy(isCompleted = !task.isCompleted) // Toggle the completion status
            taskRepository.saveTasks(_tasks) // Save tasks to shared preferences
        }
    }

    fun toggleFavouriteTask(task: Task){
        val index = _tasks.indexOf(task)
        if (index != -1){
            _tasks[index] = task.copy(isFavourite = !task.isFavourite) // Toggle the completion status
            taskRepository.saveTasks(_tasks) // Save tasks to shared preferences
        }
    }

    // return the list of favourite tasks
    fun getFavouriteTasks(): List<Task> {
        return _tasks.filter { it.isFavourite }
    }

}