/*
 * Copyright 2022 Sergio Belda
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.sergiobelda.todometer.ui.edittasklist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sergiobelda.todometer.common.domain.doIfError
import dev.sergiobelda.todometer.common.domain.doIfSuccess
import dev.sergiobelda.todometer.common.domain.usecase.GetTaskListSelectedUseCase
import dev.sergiobelda.todometer.common.domain.usecase.UpdateTaskListUseCase
import dev.sergiobelda.todometer.common.ui.error.mapToErrorUi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class EditTaskListViewModel(
    private val getTaskListSelectedUseCase: GetTaskListSelectedUseCase,
    private val updateTaskListUseCase: UpdateTaskListUseCase
) : ViewModel() {

    var editTaskListUiState by mutableStateOf(EditTaskListUiState(isLoading = true))
        private set

    init {
        getTaskListSelected()
    }

    private fun getTaskListSelected() = viewModelScope.launch {
        delay(3000)
        getTaskListSelectedUseCase().collect { result ->
            result.doIfSuccess { taskList ->
                editTaskListUiState = editTaskListUiState.copy(
                    isLoading = false,
                    taskList = taskList,
                    errorUi = null
                )
            }.doIfError { error ->
                editTaskListUiState = editTaskListUiState.copy(
                    isLoading = false,
                    taskList = null,
                    errorUi = error.mapToErrorUi()
                )
            }
        }
    }

    fun updateTaskList(name: String) = viewModelScope.launch {
        editTaskListUiState.taskList?.let {
            updateTaskListUseCase(it.copy(name = name))
        }
    }
}
