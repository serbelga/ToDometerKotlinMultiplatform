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

import dev.sergiobelda.todometer.common.domain.model.NewTask
import dev.sergiobelda.todometer.common.domain.model.Tag
import dev.sergiobelda.todometer.common.ui.base.BaseEvent

sealed class AddTaskEvent : BaseEvent {
    data class OnBack(
        val navigateBack: () -> Unit,
    ) : AddTaskEvent()

    data object OnConfirmDatePickerDialog : AddTaskEvent()
    data object OnDismissDatePickerDialog : AddTaskEvent()
    data object OnShowDatePickerDialog : AddTaskEvent()
    data object OnConfirmTimePickerDialog : AddTaskEvent()
    data object OnDismissTimePickerDialog : AddTaskEvent()
    data object OnShowTimePickerDialog : AddTaskEvent()
    data object OnDismissDiscardTaskDialog : AddTaskEvent()
    data object ClearDateTime : AddTaskEvent()

    data class TaskTitleValueChange(val value: String) : AddTaskEvent()
    data class OnTagSelected(val tag: Tag) : AddTaskEvent()
    data class OnAddTaskCheckListItem(val item: String) : AddTaskEvent()
    data class OnDeleteTaskCheckListItem(val index: Int) : AddTaskEvent()
    data class TaskDescriptionValueChange(val value: String) : AddTaskEvent()

    data class OnSaveButtonClick(
        val onInsertNewTask: (NewTask) -> Unit
    ) : AddTaskEvent()
    data class OnInsertNewTask(val newTask: NewTask) : AddTaskEvent()
}
