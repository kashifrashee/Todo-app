@file:Suppress("NAME_SHADOWING")

package com.example.todo.ui.theme.Screens

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOut
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todo.R
import com.example.todo.data.TaskViewModel
import com.example.todo.model.Task
import com.example.todo.ui.theme.LightOrange


// Todo App
@Composable
fun TodoApp() {
    val navController = rememberNavController()
    val viewModel: TaskViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "task_list",
    ) {
        composable("task_list") {
            TaskListScreen(taskViewModel = viewModel,
                navigateToAddTask = { navController.navigate("add_task") },
                navigateToFavoriteTasks = { navController.navigate("favorite_tasks") })
        }
        composable(
            "add_task",
            enterTransition = {
                slideInVerticallySpring()
            },
            exitTransition = { slideOutOfContainerSpring(AnimatedContentTransitionScope.SlideDirection.Left) }
        ) {
            AddTaskScreen(
                taskViewModel = viewModel,
                navigationToTaskList = { navController.popBackStack() }
            )
        }
        composable(
            "favorite_tasks",
            enterTransition = {
                slideInHorizontallySpring()
            },
            exitTransition = { slideOutOfContainerSpring(AnimatedContentTransitionScope.SlideDirection.Right) }
        ) {
            FavoriteTasksScreen(taskViewModel = viewModel,
                navigateBack = { navController.popBackStack() })
        }
    }
}

// Todo App Bar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoAppBar(
    currentScreen: String,
    showBackButton: Boolean,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val isDarkTheme = isSystemInDarkTheme()
    val textColor = if (isDarkTheme) Color.Black else Color.White

    TopAppBar(
        title = {
            Text(
                text = currentScreen,
                color = textColor,
                fontSize = 25.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = (if (showBackButton) {
            {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back_button),
                        tint = textColor
                    )
                }
            }
        } else {
            { Box {} }
        }),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier,
    )
}

// Add Task Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    modifier: Modifier = Modifier, taskViewModel: TaskViewModel, navigationToTaskList: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf(false) }
    var descriptionError by remember { mutableStateOf(false) }

    // Function to validate fields
    fun validateFields(): Boolean {
        val isTitleValid = title.isNotEmpty()
        val isDescriptionValid = description.isNotEmpty()
        titleError = !isTitleValid
        descriptionError = !isDescriptionValid
        return isTitleValid && isDescriptionValid
    }

    Scaffold(topBar = {
        TodoAppBar(
            currentScreen = "Add Task",
            showBackButton = true,
            onBackClick = navigationToTaskList
        )
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    titleError = false // Reset error state when text changes
                },
                label = {
                    Text(text = "Title")
                },
                singleLine = true,
                isError = titleError,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = if (titleError) Color.Red else MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = if (titleError) Color.Red else Color.Gray,
                    errorBorderColor = Color.Red
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
            val scrollState = rememberScrollState()
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(200.dp) // Height for the text field
                    .verticalScroll(scrollState) // Enable vertical scrolling
            ) {
                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                        descriptionError = false // Reset error state when text changes
                    },
                    label = {
                        Text(text = "Description")
                    },
                    singleLine = false,
                    colors = outlinedTextFieldColors(
                        focusedBorderColor = if (descriptionError) Color.Red else MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = if (descriptionError) Color.Red else Color.Gray,
                        errorBorderColor = Color.Red
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(width = 120.dp, height = 200.dp)
                        .padding(start = 12.dp, end = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            IconButton(
                onClick = {
                    if (validateFields()) {
                        taskViewModel.addTask(title, description)
                        navigationToTaskList()
                    }
                },
                modifier = Modifier
                    .padding(top = 50.dp, start = 12.dp, end = 12.dp)
                    .background(LightOrange, RoundedCornerShape(10.dp))
                    .size(60.dp) // Size of the icon
                //.clip(RoundedCornerShape(10.dp)) // Clip the background to a circle shape
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Task",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}


// Task List Screen
@Composable
fun TaskListScreen(
    modifier: Modifier = Modifier,
    taskViewModel: TaskViewModel,
    navigateToAddTask: () -> Unit,
    navigateToFavoriteTasks: () -> Unit
) {
    // State to manage tasks
    val tasks by remember { derivedStateOf { taskViewModel.tasks } }
    val isDarkTheme = isSystemInDarkTheme()
    val buttonBackgroundColor = if (isDarkTheme) Color.DarkGray else LightOrange
    val favoriteButtonColor = if (isDarkTheme) Color.Red else Color.Red

    // State for showing edit dialog
    val (selectedTask, setSelectedTask) = remember { mutableStateOf<Task?>(null) }
    val (showEditDialog, setShowEditDialog) = remember { mutableStateOf(false) }

    // Edit dialog
    if (showEditDialog && selectedTask != null) {
        EditTaskDialog(task = selectedTask!!,
            onDismiss = { setShowEditDialog(false) },
            onSave = { updatedTask ->
                taskViewModel.updateTask(updatedTask)
                setShowEditDialog(false)
            })
    }

    // State for showing delete confirmation dialog
    val (showDeleteDialog, setShowDeleteDialog) = remember { mutableStateOf(false) }
    val (taskToDelete, setTaskToDelete) = remember { mutableStateOf<Task?>(null) }

    // Delete confirmation dialog
    if (showDeleteDialog && taskToDelete != null) {
        AlertDialog(onDismissRequest = { setShowDeleteDialog(false) },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete this task?") },
            confirmButton = {
                TextButton(onClick = {
                    taskToDelete?.let { taskViewModel.deleteTask(it) }
                    setShowDeleteDialog(false)
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { setShowDeleteDialog(false) }) {
                    Text("Cancel")
                }
            })
    }

    Scaffold(topBar = {
        TodoAppBar(currentScreen = "Task List", showBackButton = false, onBackClick = {})
    }) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                if (tasks.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No favorite tasks yet!",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                } else {
                    items(tasks) { task ->
                        AnimatedVisibility(
                            visible = true, enter = fadeIn(), exit = fadeOut()
                        ) {
                            AnimatedTaskItem(task = task,
                                onToggleCompletion = { taskViewModel.toggleTaskCompletion(task) },
                                onDelete = {
                                    setTaskToDelete(task)
                                    setShowDeleteDialog(true)
                                },
                                onToggleFavourite = { taskViewModel.toggleFavouriteTask(task) },
                                onEdit = { task ->
                                    setSelectedTask(task)
                                    setShowEditDialog(true)
                                })
                        }
                    }
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = navigateToAddTask,
                modifier = Modifier
                    .padding(bottom = 14.dp)
                    .background(buttonBackgroundColor, CircleShape)
                    .size(58.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Task",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
            IconButton(
                onClick = navigateToFavoriteTasks,
                modifier = Modifier
                    .padding(bottom = 14.dp)
                    .background(buttonBackgroundColor, CircleShape)
                    .size(58.dp)
            ) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = "Favorite Task",
                    tint = favoriteButtonColor,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}


// Favorite Tasks Screen
@Composable
fun FavoriteTasksScreen(
    modifier: Modifier = Modifier, taskViewModel: TaskViewModel, navigateBack: () -> Unit
) {
    val favouriteTasks = taskViewModel.getFavouriteTasks()

    val (selectedTask, setSelectedTask) = remember { mutableStateOf<Task?>(null) }
    val (showEditDialog, setShowEditDialog) = remember { mutableStateOf(false) }

    if (showEditDialog && selectedTask != null) {
        EditTaskDialog(task = selectedTask!!,
            onDismiss = { setShowEditDialog(false) },
            onSave = { updatedTask ->
                taskViewModel.updateTask(updatedTask)
                setShowEditDialog(false)
            })
    }

    // State for showing delete confirmation dialog
    val (showDeleteDialog, setShowDeleteDialog) = remember { mutableStateOf(false) }
    val (taskToDelete, setTaskToDelete) = remember { mutableStateOf<Task?>(null) }

    // Delete confirmation dialog
    if (showDeleteDialog && taskToDelete != null) {
        AlertDialog(onDismissRequest = { setShowDeleteDialog(false) },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete this task?") },
            confirmButton = {
                TextButton(onClick = {
                    taskToDelete?.let { taskViewModel.deleteTask(it) }
                    setShowDeleteDialog(false)
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { setShowDeleteDialog(false) }) {
                    Text("Cancel")
                }
            })
    }


    Scaffold(topBar = {
        TodoAppBar(
            currentScreen = "Favorite Tasks", showBackButton = true, onBackClick = navigateBack
        )
    }) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
        ) {
            if (favouriteTasks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No favorite tasks yet!",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn {
                    items(favouriteTasks) { task ->
                        AnimatedTaskItem(task = task,
                            onToggleCompletion = { taskViewModel.toggleTaskCompletion(task) },
                            onDelete = {
                                setTaskToDelete(task)
                                setShowDeleteDialog(true)
                                       },
                            onToggleFavourite = { taskViewModel.toggleFavouriteTask(task) },
                            onEdit = { task ->
                                setSelectedTask(task)
                                setShowEditDialog(true)
                            })
                    }
                }
            }
        }
    }
}


// Animated Task Item
@Composable
fun AnimatedTaskItem(
    task: Task,
    onToggleCompletion: () -> Unit,
    onDelete: () -> Unit,
    onToggleFavourite: () -> Unit,
    onEdit: (Task) -> Unit,
) {
    val isDarkTheme = isSystemInDarkTheme()
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val editButtonColor = if (isDarkTheme) Color.White else Color.Gray
    val showEditDialog = remember { mutableStateOf(false) }
    val expanded = remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp)),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 60.dp
        )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = task.isCompleted, onCheckedChange = { onToggleCompletion() })
                Column(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = task.title,
                            fontSize = 19.sp,
                            color = textColor,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = onToggleFavourite,
                            modifier = Modifier.size(24.dp),
                        ) {
                            Icon(
                                Icons.Default.Favorite,
                                contentDescription = "Favorite Task",
                                tint = if (task.isFavourite) Color.Red else Color.Gray
                            )
                        }
                    }
                    AnimatedVisibility(
                        visible = expanded.value, enter = expandIn(), exit = shrinkOut()
                    ) {
                        Column(
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text(
                                text = task.description, modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(onClick = { showEditDialog.value = true }) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Edit Task",
                                        tint = editButtonColor
                                    )
                                }
                                IconButton(onClick = onDelete) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete Task",
                                        tint = Color.Red
                                    )
                                }
                            }
                        }
                    }
                }
            }
            IconButton(onClick = { expanded.value = !expanded.value }) {
                Icon(
                    imageVector = if (expanded.value) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand/Collapse Details",
                    tint = Color.Gray
                )
            }
        }
    }
    if (showEditDialog.value) {
        EditTaskDialog(task = task,
            onDismiss = { showEditDialog.value = false },
            onSave = { updatedTask ->
                onEdit(updatedTask)
                showEditDialog.value = false
            })
    }
}


// Edit Task Dialog
@Composable
fun EditTaskDialog(
    task: Task, onDismiss: () -> Unit, onSave: (Task) -> Unit
) {
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }

    AlertDialog(onDismissRequest = onDismiss, title = { Text("Edit Task") }, text = {
        Column {
            OutlinedTextField(value = title,
                onValueChange = { title = it },
                label = { Text("Title") })
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = description,
                onValueChange = { description = it },
                label = { Text("Description") })
        }
    }, confirmButton = {
        TextButton(onClick = {
            onSave(task.copy(title = title, description = description))
        }) {
            Text("Save")
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text("Cancel")
        }
    })
}


// Slide in from the bottom animation method
@OptIn(ExperimentalAnimationApi::class)
fun slideInVerticallySpring(
    animationSpec: FiniteAnimationSpec<IntOffset> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )
): EnterTransition {
    return slideInVertically(
        animationSpec = animationSpec,
        initialOffsetY = { it }
    )
}


// Slide out from the left animation method
@OptIn(ExperimentalAnimationApi::class)
fun slideOutOfContainerSpring(
    towards: AnimatedContentTransitionScope.SlideDirection,
    animationSpec: FiniteAnimationSpec<IntOffset> = spring(
        stiffness = Spring.StiffnessMediumLow,
        dampingRatio = Spring.DampingRatioMediumBouncy
    ),
    targetOffset: (fullSize: IntSize) -> IntOffset = { size ->
        when (towards) {
            AnimatedContentTransitionScope.SlideDirection.Left -> IntOffset(-size.width, 0)
            AnimatedContentTransitionScope.SlideDirection.Right -> IntOffset(size.width, 0)
            AnimatedContentTransitionScope.SlideDirection.Up -> IntOffset(0, -size.height)
            AnimatedContentTransitionScope.SlideDirection.Down -> IntOffset(0, size.height)
            else -> IntOffset.Zero
        }
    }
): ExitTransition {
    return slideOut(
        animationSpec = animationSpec,
        targetOffset = targetOffset
    )
}


// Slide in from the right animation method
@OptIn(ExperimentalAnimationApi::class)
fun slideInHorizontallySpring(
    animationSpec: FiniteAnimationSpec<IntOffset> = spring(
        stiffness = Spring.StiffnessMediumLow,
        dampingRatio = Spring.DampingRatioMediumBouncy
    ),
    initialOffset: (fullSize: IntSize) -> IntOffset = { size ->
        IntOffset(size.width, 0) // Slide in from the right
    }
): EnterTransition {
    return slideIn(
        animationSpec = animationSpec,
        initialOffset = initialOffset
    )
}



