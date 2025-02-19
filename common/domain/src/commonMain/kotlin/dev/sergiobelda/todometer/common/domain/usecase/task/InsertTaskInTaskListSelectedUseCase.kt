/*
 * Copyright 2021 Sergio Belda
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

package dev.sergiobelda.todometer.common.domain.usecase.task

import dev.sergiobelda.todometer.common.domain.Result
import dev.sergiobelda.todometer.common.domain.doIfSuccess
import dev.sergiobelda.todometer.common.domain.model.NewTask
import dev.sergiobelda.todometer.common.domain.model.Task
import dev.sergiobelda.todometer.common.domain.repository.ITaskChecklistItemsRepository
import dev.sergiobelda.todometer.common.domain.repository.ITaskRepository
import dev.sergiobelda.todometer.common.domain.repository.IUserPreferencesRepository
import kotlinx.coroutines.flow.firstOrNull

class InsertTaskInTaskListSelectedUseCase(
    private val taskRepository: ITaskRepository,
    private val userPreferencesRepository: IUserPreferencesRepository,
    private val taskChecklistItemsRepository: ITaskChecklistItemsRepository,
) {

    /**
     * Creates a new [Task] given a [newTask].
     */
    suspend operator fun invoke(
        newTask: NewTask
    ): Result<String> {
        val taskListId = userPreferencesRepository.taskListSelected().firstOrNull() ?: ""
        val result = taskRepository.insertTask(
            newTask.title,
            newTask.tag,
            newTask.description,
            newTask.dueDate,
            taskListId,
        )
        result.doIfSuccess { taskId ->
            taskChecklistItemsRepository.insertTaskChecklistItems(
                taskId,
                items = newTask.taskChecklistItems.toTypedArray(),
            )
        }
        return result
    }
}
