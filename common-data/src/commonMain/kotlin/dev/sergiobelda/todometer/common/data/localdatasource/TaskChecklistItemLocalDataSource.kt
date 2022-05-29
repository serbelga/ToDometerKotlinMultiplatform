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

package dev.sergiobelda.todometer.common.data.localdatasource

import dev.sergiobelda.todometer.common.data.database.dao.ITaskChecklistItemDao
import dev.sergiobelda.todometer.common.data.database.mapper.toTaskChecklist
import dev.sergiobelda.todometer.common.data.database.mapper.toTaskChecklistItemEntity
import dev.sergiobelda.todometer.common.domain.Result
import dev.sergiobelda.todometer.common.domain.model.TaskChecklistItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskChecklistItemLocalDataSource(
    private val taskChecklistItemDao: ITaskChecklistItemDao
) : ITaskChecklistItemLocalDataSource {

    override fun getTaskChecklistItems(taskId: String): Flow<Result<List<TaskChecklistItem>>> =
        taskChecklistItemDao.getTaskChecklistItems(taskId).map { list ->
            Result.Success(list.toTaskChecklist())
        }

    override suspend fun insertTaskChecklistItem(taskChecklistItem: TaskChecklistItem) =
        taskChecklistItemDao.insertTaskChecklistItem(taskChecklistItem.toTaskChecklistItemEntity())
}
