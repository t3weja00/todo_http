package com.example.todo.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.model.Todo
import com.example.todo.model.TodosApi
import com.example.todo.utils.NetworkUtils
import kotlinx.coroutines.launch

sealed interface TodoUiState {
    data class Success(val todos: List<Todo>): TodoUiState
    data class Error(val message: String): TodoUiState
    object Loading: TodoUiState
}

class TodoViewModel(private val application: Application) : AndroidViewModel(application) {
    var todoUiState: TodoUiState by mutableStateOf<TodoUiState>(TodoUiState.Loading)
        private set

    init {
        getTodosList()
    }

    private fun getTodosList() {
        viewModelScope.launch {
            if (!NetworkUtils.isNetworkAvailable(application)) {
                todoUiState = TodoUiState.Error("No internet connection available")
                return@launch
            }

            try {
                Log.d("TODO_APP", "Starting API call")
                val todosApi = TodosApi.getInstance()
                Log.d("TODO_APP", "API instance created")
                val fetchedTodos = todosApi.getTodos()
                Log.d("TODO_APP", "Fetched ${fetchedTodos.size} todos")
                todoUiState = TodoUiState.Success(fetchedTodos)
                Log.d("TODO_APP", "Updated todos list")
            } catch (e: Exception) {
                Log.d("TODO_APP", "Error: ${e.message}")
                todoUiState = TodoUiState.Error("Failed to fetch data from server")
                e.printStackTrace()
            }
        }
    }
}