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

package dev.sergiobelda.todometer.desktop.ui.addtasklist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import dev.sergiobelda.todometer.common.compose.ui.addtasklist.AddTaskListScreen
import dev.sergiobelda.todometer.desktop.koin
import org.koin.core.parameter.parametersOf

@Composable
fun AddTaskListRoute(
    navigateBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val addTaskListViewModel: AddTaskListViewModel = remember {
        koin.get { parametersOf(coroutineScope) }
    }

    AddTaskListScreen(
        navigateBack = navigateBack,
        onAddTaskList = { taskListName ->
            addTaskListViewModel.insertTaskList(taskListName)
        },
        addTaskListUiState = addTaskListViewModel.addTaskListUiState
    )
}
