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

package dev.sergiobelda.todometer.app.feature.taskdetails.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.sergiobelda.navigation.compose.extended.annotation.NavArgument
import dev.sergiobelda.navigation.compose.extended.annotation.NavArgumentType
import dev.sergiobelda.navigation.compose.extended.annotation.NavDestination
import dev.sergiobelda.todometer.app.common.designsystem.components.TodometerCheckbox
import dev.sergiobelda.todometer.app.common.designsystem.components.TodometerDivider
import dev.sergiobelda.todometer.app.common.designsystem.theme.Alpha.applyMediumEmphasisAlpha
import dev.sergiobelda.todometer.app.common.ui.components.AddChecklistItemField
import dev.sergiobelda.todometer.app.common.ui.components.TaskDueDateChip
import dev.sergiobelda.todometer.app.common.ui.components.TaskTagIndicator
import dev.sergiobelda.todometer.app.common.ui.loading.LoadingScreenDialog
import dev.sergiobelda.todometer.app.common.ui.values.SectionPadding
import dev.sergiobelda.todometer.common.designsystem.resources.images.Images
import dev.sergiobelda.todometer.common.designsystem.resources.images.icons.Close
import dev.sergiobelda.todometer.common.designsystem.resources.images.icons.Edit
import dev.sergiobelda.todometer.common.designsystem.resources.images.icons.NavigateBefore
import dev.sergiobelda.todometer.common.designsystem.resources.images.icons.PushPin
import dev.sergiobelda.todometer.common.designsystem.resources.images.icons.PushPinFilled
import dev.sergiobelda.todometer.common.domain.model.Tag
import dev.sergiobelda.todometer.common.domain.model.Task
import dev.sergiobelda.todometer.common.domain.model.TaskChecklistItem
import dev.sergiobelda.todometer.common.domain.model.TaskChecklistItemState
import dev.sergiobelda.todometer.common.resources.TodometerResources
import kotlinx.collections.immutable.ImmutableList

@NavDestination(
    destinationId = "taskdetails",
    name = "TaskDetails",
    arguments = [
        NavArgument("taskId", NavArgumentType.String),
    ],
    deepLinkUris = ["app://open.task"],
)
@Composable
fun TaskDetailsScreen(
    navigateBack: () -> Unit,
    navigateToEditTask: () -> Unit,
    viewModel: TaskDetailsViewModel,
) {
    when {
        viewModel.state.isLoadingTask -> {
            LoadingScreenDialog(navigateBack)
        }

        !viewModel.state.isLoadingTask ->
            viewModel.state.task?.let { task ->
                TaskDetailsSuccessContent(
                    task = task,
                    taskChecklistItems = viewModel.state.taskChecklistItems,
                    navigateBack = navigateBack,
                    navigateToEditTask = navigateToEditTask,
                    onTaskChecklistItemClick = { id, checked ->
                        if (checked) {
                            viewModel.setTaskChecklistItemChecked(id)
                        } else {
                            viewModel.setTaskChecklistItemUnchecked(id)
                        }
                    },
                    onDeleteTaskCheckListItem = viewModel::deleteTaskChecklistItem,
                    onAddTaskCheckListItem = viewModel::insertTaskChecklistItem,
                    toggleTaskPinnedValueClick = viewModel::toggleTaskPinnedValueUseCase,
                )
            }
    }
}

// TODO: Resolve LongMethod issue.
@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskDetailsSuccessContent(
    task: Task,
    taskChecklistItems: ImmutableList<TaskChecklistItem>,
    navigateBack: () -> Unit,
    navigateToEditTask: () -> Unit,
    onTaskChecklistItemClick: (String, Boolean) -> Unit,
    onDeleteTaskCheckListItem: (String) -> Unit,
    onAddTaskCheckListItem: (String) -> Unit,
    toggleTaskPinnedValueClick: () -> Unit,
) {
    val lazyListState = rememberLazyListState()
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)

    val showTopAppBarTitle by remember {
        derivedStateOf { lazyListState.firstVisibleItemIndex > 0 }
    }
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    AnimatedVisibility(
                        visible = showTopAppBarTitle,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        Text(
                            task.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            Images.Icons.NavigateBefore,
                            contentDescription = TodometerResources.strings.back,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = toggleTaskPinnedValueClick) {
                        Icon(
                            imageVector = if (task.isPinned) {
                                Images.Icons.PushPinFilled
                            } else {
                                Images.Icons.PushPin
                            },
                            contentDescription = if (task.isPinned) {
                                TodometerResources.strings.pinnedTask
                            } else {
                                TodometerResources.strings.notPinnedTask
                            },
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                    IconButton(onClick = navigateToEditTask) {
                        Icon(
                            Images.Icons.Edit,
                            contentDescription = TodometerResources.strings.editTask,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        content = { paddingValues ->
            LazyColumn(state = lazyListState, modifier = Modifier.padding(paddingValues)) {
                taskTitle(task)
                taskChips(task)
                taskChecklist(
                    taskChecklistItems,
                    onTaskChecklistItemClick = onTaskChecklistItemClick,
                    onDeleteTaskCheckListItem = onDeleteTaskCheckListItem,
                    onAddTaskCheckListItem = onAddTaskCheckListItem,
                )
                taskDescription(task.description)
            }
        },
    )
}

private fun LazyListScope.taskTitle(task: Task) {
    item {
        Surface(modifier = Modifier.heightIn(max = 80.dp, min = 64.dp)) {
            Row(
                modifier = Modifier.padding(start = 24.dp, bottom = 8.dp, end = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (task.tag != Tag.UNSPECIFIED) {
                    TaskTagIndicator(task.tag)
                }
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 4.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        TodometerDivider()
    }
}

private fun LazyListScope.taskChips(task: Task) {
    item {
        task.dueDate?.let {
            TaskDueDateChip(it, modifier = Modifier.padding(start = 24.dp, top = 24.dp))
        }
    }
}

private fun LazyListScope.taskChecklist(
    taskChecklistItems: ImmutableList<TaskChecklistItem>,
    onTaskChecklistItemClick: (String, Boolean) -> Unit,
    onDeleteTaskCheckListItem: (String) -> Unit,
    onAddTaskCheckListItem: (String) -> Unit,
) {
    item {
        Spacer(modifier = Modifier.height(24.dp))
    }
    item {
        TaskDetailSectionTitle(TodometerResources.strings.checklist)
    }
    items(taskChecklistItems, key = { it.id }) { taskChecklistItem ->
        TaskChecklistItem(taskChecklistItem, onTaskChecklistItemClick, onDeleteTaskCheckListItem)
    }
    item {
        AddChecklistItemField(
            onAddTaskCheckListItem = onAddTaskCheckListItem,
        ) {
            Text(TodometerResources.strings.addElement)
        }
    }
    item {
        TodometerDivider()
    }
}

@Composable
private fun LazyItemScope.TaskChecklistItem(
    taskChecklistItem: TaskChecklistItem,
    onTaskChecklistItemClick: (String, Boolean) -> Unit,
    onDeleteTaskCheckListItem: (String) -> Unit,
) {
    val textColor = if (taskChecklistItem.state == TaskChecklistItemState.CHECKED) {
        MaterialTheme.colorScheme.onSurface.applyMediumEmphasisAlpha()
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    val textDecoration = if (taskChecklistItem.state == TaskChecklistItemState.CHECKED) {
        TextDecoration.LineThrough
    } else {
        null
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onTaskChecklistItemClick(
                    taskChecklistItem.id,
                    taskChecklistItem.state == TaskChecklistItemState.UNCHECKED,
                )
            }
            .animateItem()
            .padding(horizontal = 8.dp),
    ) {
        TodometerCheckbox(
            checked = taskChecklistItem.state == TaskChecklistItemState.CHECKED,
            onCheckedChange = { checked ->
                onTaskChecklistItemClick(
                    taskChecklistItem.id,
                    checked,
                )
            },
            modifier = Modifier.scale(TodometerCheckboxScaleFactor),
        )
        Text(
            text = taskChecklistItem.text,
            modifier = Modifier.weight(1f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = textColor,
            textDecoration = textDecoration,
        )
        IconButton(onClick = { onDeleteTaskCheckListItem(taskChecklistItem.id) }) {
            Icon(
                Images.Icons.Close,
                contentDescription = TodometerResources.strings.clear,
                tint = MaterialTheme.colorScheme.onSurface.applyMediumEmphasisAlpha(),
            )
        }
    }
}

private fun LazyListScope.taskDescription(description: String?) {
    item {
        Spacer(modifier = Modifier.height(24.dp))
    }
    item {
        TaskDetailSectionTitle(TodometerResources.strings.description)
    }
    item {
        if (!description.isNullOrBlank()) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(
                    start = SectionPadding,
                    end = SectionPadding,
                    bottom = SectionPadding,
                ),
            )
        } else {
            Text(
                text = TodometerResources.strings.noDescription,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(
                    top = 16.dp,
                    start = SectionPadding,
                    end = SectionPadding,
                    bottom = SectionPadding,
                ),
                color = MaterialTheme.colorScheme.onSurface.applyMediumEmphasisAlpha(),
            )
        }
    }
}

@Composable
private fun TaskDetailSectionTitle(text: String) {
    Text(
        text,
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier.padding(start = SectionPadding, bottom = 8.dp),
    )
}

private const val TodometerCheckboxScaleFactor: Float = 0.85f
