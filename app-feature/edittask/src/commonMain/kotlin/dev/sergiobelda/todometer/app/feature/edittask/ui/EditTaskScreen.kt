/*
 * Copyright 2023 Sergio Belda
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

package dev.sergiobelda.todometer.app.feature.edittask.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import dev.sergiobelda.navigation.compose.extended.annotation.NavArgument
import dev.sergiobelda.navigation.compose.extended.annotation.NavArgumentType
import dev.sergiobelda.navigation.compose.extended.annotation.NavDestination
import dev.sergiobelda.todometer.app.common.designsystem.components.TodometerDivider
import dev.sergiobelda.todometer.app.common.designsystem.components.TodometerTitledTextField
import dev.sergiobelda.todometer.app.common.ui.components.DatePickerDialog
import dev.sergiobelda.todometer.app.common.ui.components.DateTimeSelector
import dev.sergiobelda.todometer.app.common.ui.components.SaveActionTopAppBar
import dev.sergiobelda.todometer.app.common.ui.components.TagSelector
import dev.sergiobelda.todometer.app.common.ui.components.TimePickerDialog
import dev.sergiobelda.todometer.app.common.ui.extensions.addStyledOptionalSuffix
import dev.sergiobelda.todometer.app.common.ui.extensions.selectedTimeMillis
import dev.sergiobelda.todometer.app.common.ui.loading.LoadingScreenDialog
import dev.sergiobelda.todometer.app.common.ui.values.SectionPadding
import dev.sergiobelda.todometer.app.common.ui.values.TextFieldPadding
import dev.sergiobelda.todometer.common.domain.model.Tag
import dev.sergiobelda.todometer.common.domain.model.Task
import dev.sergiobelda.todometer.common.resources.TodometerResources
import dev.sergiobelda.todometer.common.ui.extensions.localTime

@NavDestination(
    destinationId = "edittask",
    name = "EditTask",
    arguments = [
        NavArgument("taskId", NavArgumentType.String),
    ],
)
@Composable
fun EditTaskScreen(
    navigateBack: () -> Unit,
    viewModel: EditTaskViewModel,
) {
    when {
        viewModel.state.isLoading -> {
            LoadingScreenDialog(navigateBack)
        }

        !viewModel.state.isLoading -> {
            viewModel.state.task?.let { task ->
                EditTaskSuccessContent(
                    task = task,
                    navigateBack = navigateBack,
                    updateTask = { title, tag, description, dueDate ->
                        viewModel.updateTask(title, tag, description, dueDate)
                    },
                )
            }
        }
    }
}

// TODO: Resolve LongMethod issue.
@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditTaskSuccessContent(
    task: Task,
    navigateBack: () -> Unit,
    updateTask: (taskTitle: String, selectedTag: Tag, taskDescription: String, taskDueDate: Long?) -> Unit,
) {
    var taskTitle by rememberSaveable { mutableStateOf(task.title) }
    var taskTitleInputError: Boolean by remember { mutableStateOf(false) }
    var taskDescription by rememberSaveable {
        mutableStateOf(
            task.description ?: "",
        )
    }
    var selectedTag by rememberSaveable { mutableStateOf(task.tag) }
    var taskDueDate: Long? by rememberSaveable { mutableStateOf(task.dueDate) }

    var datePickerDialogState by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = task.dueDate)

    var timePickerDialogState by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = task.dueDate?.localTime()?.hour ?: 0,
        initialMinute = task.dueDate?.localTime()?.minute ?: 0,
    )

    Scaffold(
        topBar = {
            SaveActionTopAppBar(
                navigateBack = navigateBack,
                title = TodometerResources.strings.editTask,
                onSaveButtonClick = {
                    if (taskTitle.isBlank()) {
                        taskTitleInputError = true
                    } else {
                        updateTask(taskTitle, selectedTag, taskDescription, taskDueDate)
                        navigateBack()
                    }
                },
            )
        },
        content = { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                TodometerTitledTextField(
                    title = TodometerResources.strings.name,
                    value = taskTitle,
                    onValueChange = {
                        taskTitle = it
                        taskTitleInputError = false
                    },
                    placeholder = { Text(TodometerResources.strings.enterTaskName) },
                    isError = taskTitleInputError,
                    errorMessage = TodometerResources.strings.fieldNotEmpty,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next,
                    ),
                    modifier = Modifier.padding(TextFieldPadding),
                )
                Text(
                    text = TodometerResources.strings.chooseTag.addStyledOptionalSuffix(),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = SectionPadding),
                )
                TagSelector(
                    onTagSelected = { selectedTag = it },
                    selectedTag = selectedTag
                )
                Text(
                    text = TodometerResources.strings.dateTime.addStyledOptionalSuffix(),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = SectionPadding),
                )
                DateTimeSelector(
                    taskDueDate,
                    onEnterDateTimeClick = { datePickerDialogState = true },
                    onDateClick = { datePickerDialogState = true },
                    onTimeClick = { timePickerDialogState = true },
                    onClearDateTimeClick = { taskDueDate = null },
                )
                TodometerTitledTextField(
                    title = TodometerResources.strings.description.addStyledOptionalSuffix(),
                    value = taskDescription,
                    onValueChange = { taskDescription = it },
                    placeholder = { Text(TodometerResources.strings.enterDescription) },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done,
                    ),
                    modifier = Modifier.padding(TextFieldPadding),
                    maxLines = 4,
                )
                TodometerDivider()
            }
        },
    )
    if (datePickerDialogState) {
        DatePickerDialog(
            onDismissRequest = { datePickerDialogState = false },
            onConfirm = {
                datePickerDialogState = false
                taskDueDate =
                    datePickerState.selectedDateMillis?.plus(timePickerState.selectedTimeMillis)
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
    if (timePickerDialogState) {
        TimePickerDialog(
            onDismissRequest = { timePickerDialogState = false },
            onConfirm = {
                timePickerDialogState = false
                taskDueDate =
                    datePickerState.selectedDateMillis?.plus(timePickerState.selectedTimeMillis)
            },
        ) {
            TimePicker(state = timePickerState)
        }
    }
}
