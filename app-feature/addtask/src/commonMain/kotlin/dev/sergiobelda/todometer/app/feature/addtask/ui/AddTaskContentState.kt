/*
 * Copyright 2025 Sergio Belda
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

package dev.sergiobelda.todometer.app.feature.addtask.ui

import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import dev.sergiobelda.todometer.app.common.ui.extensions.selectedTimeMillis
import dev.sergiobelda.todometer.common.domain.model.NewTask
import dev.sergiobelda.todometer.common.domain.model.Tag
import dev.sergiobelda.todometer.common.ui.base.BaseContentState
import dev.sergiobelda.todometer.common.ui.base.BaseEvent

@OptIn(ExperimentalMaterial3Api::class)
data class AddTaskContentState internal constructor(
    val snackbarHostState: SnackbarHostState,
    val topAppBarState: TopAppBarState,
    val datePickerState: DatePickerState,
    val timePickerState: TimePickerState,
) : BaseContentState {
    private val tags = enumValues<Tag>()

    var taskTitle by mutableStateOf("")
        private set

    var taskTitleInputError by mutableStateOf(false)
        private set

    var taskDescription by mutableStateOf("")
        private set

    var selectedTag by mutableStateOf(tags.firstOrNull() ?: Tag.UNSPECIFIED)

    var taskDueDate: Long? by mutableStateOf(null)

    val taskChecklistItems = mutableStateListOf<String>()

    var discardTaskAlertDialogVisible by mutableStateOf(false)
        private set

    var datePickerDialogVisible by mutableStateOf(false)
        private set

    var timePickerDialogVisible by mutableStateOf(false)
        private set

    suspend fun showSnackbar(message: String) =
        snackbarHostState.showSnackbar(message = message)

    override fun handleEvent(event: BaseEvent) {
        when (event) {
            is AddTaskEvent.OnBack -> checkOnBack(event)
            is AddTaskEvent.OnConfirmDatePickerDialog -> confirmDatePickerDialog()
            is AddTaskEvent.OnDismissDatePickerDialog -> dismissDatePickerDialog()
            is AddTaskEvent.OnShowDatePickerDialog -> showDatePickerDialog()
            is AddTaskEvent.OnConfirmTimePickerDialog -> confirmTimePickerDialog()
            is AddTaskEvent.OnDismissTimePickerDialog -> dismissTimePickerDialog()
            is AddTaskEvent.OnShowTimePickerDialog -> showTimePickerDialog()
            is AddTaskEvent.OnDismissDiscardTaskDialog -> dismissDiscardTaskDialog()
            is AddTaskEvent.ClearDateTime -> clearDateTime()
            is AddTaskEvent.TaskTitleValueChange -> taskTitleValueChange(event)
            is AddTaskEvent.OnTagSelected -> selectTag(event)
            is AddTaskEvent.OnAddTaskCheckListItem -> addTaskCheckListItem(event)
            is AddTaskEvent.OnDeleteTaskCheckListItem -> deleteTaskCheckListItem(event)
            is AddTaskEvent.TaskDescriptionValueChange -> taskDescriptionValueChange(event)
            is AddTaskEvent.OnSaveButtonClick -> onSaveButtonClick(event)
        }
    }

    private fun confirmDatePickerDialog() {
        datePickerDialogVisible = false
        updateTaskDueDate()
    }

    private fun dismissDatePickerDialog() {
        datePickerDialogVisible = false
    }

    private fun showDatePickerDialog() {
        datePickerDialogVisible = true
    }

    private fun confirmTimePickerDialog() {
        timePickerDialogVisible = false
        updateTaskDueDate()
    }

    private fun dismissTimePickerDialog() {
        timePickerDialogVisible = false
    }

    private fun showTimePickerDialog() {
        timePickerDialogVisible = true
    }

    private fun updateTaskDueDate() {
        taskDueDate = datePickerState.selectedDateMillis?.plus(timePickerState.selectedTimeMillis)
    }

    private fun dismissDiscardTaskDialog() {
        discardTaskAlertDialogVisible = false
    }

    private fun clearDateTime() {
        taskDueDate = null
    }

    private fun checkOnBack(event: AddTaskEvent.OnBack) {
        if (initialValuesUpdated()) {
            discardTaskAlertDialogVisible = true
        } else {
            event.navigateBack.invoke()
        }
    }

    private fun initialValuesUpdated(): Boolean =
        taskTitle.isNotBlank() ||
                taskDueDate != null ||
                taskDescription.isNotBlank() ||
                taskChecklistItems.isNotEmpty()

    private fun taskTitleValueChange(event: AddTaskEvent.TaskTitleValueChange) {
        taskTitle = event.value
    }

    private fun selectTag(event: AddTaskEvent.OnTagSelected) {
        selectedTag = event.tag
    }

    private fun addTaskCheckListItem(event: AddTaskEvent.OnAddTaskCheckListItem) {
        taskChecklistItems.add(event.item)
    }

    private fun deleteTaskCheckListItem(event: AddTaskEvent.OnDeleteTaskCheckListItem) {
        taskChecklistItems.removeAt(index = event.index)
    }

    private fun taskDescriptionValueChange(event: AddTaskEvent.TaskDescriptionValueChange) {
        taskDescription = event.value
    }

    private fun onSaveButtonClick(event: AddTaskEvent.OnSaveButtonClick) {
        taskTitleInputError = false
        if (taskTitle.isBlank()) {
            taskTitleInputError = true
        } else {
            event.onInsertNewTask(
                NewTask(
                    title = taskTitle,
                    tag = selectedTag,
                    description = taskDescription,
                    dueDate = taskDueDate,
                    taskChecklistItems = taskChecklistItems,
                )
            )
        }
    }

    companion object {
        internal fun Saver(
            snackbarHostState: SnackbarHostState,
            topAppBarState: TopAppBarState,
            datePickerState: DatePickerState,
            timePickerState: TimePickerState,
        ): Saver<AddTaskContentState, *> = mapSaver(
            save = {
                mapOf(
                    TaskTitleKey to it.taskTitle,
                    TaskDescriptionKey to it.taskDescription,
                    SelectedTagKey to it.selectedTag,
                    TaskDueDateKey to it.taskDueDate,
                )
            },
            restore = { map ->
                AddTaskContentState(
                    snackbarHostState = snackbarHostState,
                    topAppBarState = topAppBarState,
                    datePickerState = datePickerState,
                    timePickerState = timePickerState
                ).apply {
                    taskTitle = map[TaskTitleKey] as String
                    taskDescription = map[TaskDescriptionKey] as String
                    selectedTag = map[SelectedTagKey] as Tag
                    taskDueDate = map[TaskDueDateKey] as? Long
                }
            }
        )

        private const val TaskTitleKey: String = "task_title"
        private const val TaskDescriptionKey: String = "task_description"
        private const val SelectedTagKey: String = "selected_tag"
        private const val TaskDueDateKey: String = "task_due_date"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberAddTaskContentState(
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    topAppBarState: TopAppBarState = rememberTopAppBarState(),
    datePickerState: DatePickerState = rememberDatePickerState(),
    timePickerState: TimePickerState = rememberTimePickerState(),
): AddTaskContentState = rememberSaveable(
    saver = AddTaskContentState.Saver(
        snackbarHostState = snackbarHostState,
        topAppBarState = topAppBarState,
        datePickerState = datePickerState,
        timePickerState = timePickerState,
    )
) {
    AddTaskContentState(
        snackbarHostState = snackbarHostState,
        topAppBarState = topAppBarState,
        datePickerState = datePickerState,
        timePickerState = timePickerState,
    )
}
