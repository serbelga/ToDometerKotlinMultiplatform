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

package dev.sergiobelda.todometer.app.common.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sergiobelda.pigment.colorpicker.ColorPicker
import dev.sergiobelda.pigment.colorpicker.ColorPickerItem
import dev.sergiobelda.todometer.app.common.designsystem.theme.TodometerTheme
import dev.sergiobelda.todometer.app.common.ui.mapper.composeColorOf
import dev.sergiobelda.todometer.common.domain.model.Tag

@Composable
fun TagSelector(
    onTagSelected: (Tag) -> Unit,
    selectedTag: Tag
) {
    val tags = enumValues<Tag>()
    val state = rememberLazyListState()

    val colorsMap = tags.associateWith {
        ColorPickerItem(
            color = TodometerTheme.todometerColors.composeColorOf(it),
        )
    }

    ColorPicker.LazyRow(
        colorsMap = colorsMap,
        selectedItem = selectedTag,
        onItemSelected = onTagSelected,
        state = state,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(vertical = 16.dp),
    )

    LaunchedEffect(selectedTag) {
        state.animateScrollToItem(tags.indexOf(selectedTag))
    }
}
