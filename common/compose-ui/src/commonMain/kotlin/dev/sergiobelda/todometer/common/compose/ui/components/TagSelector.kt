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

package dev.sergiobelda.todometer.common.compose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FormatColorReset
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.sergiobelda.todometer.common.compose.ui.designsystem.theme.ToDometerColors
import dev.sergiobelda.todometer.common.compose.ui.designsystem.theme.ToDometerTheme
import dev.sergiobelda.todometer.common.compose.ui.mapper.composeColorOf
import dev.sergiobelda.todometer.common.domain.model.Tag
import dev.sergiobelda.todometer.common.resources.MR
import dev.sergiobelda.todometer.common.resources.ToDometerIcons
import dev.sergiobelda.todometer.common.resources.painterResource
import dev.sergiobelda.todometer.common.resources.stringResource

@Composable
internal fun TagSelector(selectedTag: Tag, onSelected: (Tag) -> Unit) {
    val tags = enumValues<Tag>()
    val state = rememberLazyListState()
    Text(
        text = stringResource(MR.strings.choose_tag),
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier.padding(horizontal = 24.dp)
    )
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
        state = state
    ) {
        items(tags) { tag ->
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .clickable {
                        onSelected(tag)
                    }
            ) {
                val color = ToDometerTheme.toDometerColors.composeColorOf(tag)
                val borderColor = if (color == Color.Unspecified) {
                    MaterialTheme.colorScheme.outline
                } else {
                    Color.Unspecified
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(32.dp)
                        .border(width = 1.dp, color = borderColor, shape = CircleShape)
                        .clip(CircleShape)
                        .background(color)
                ) {
                    if (tag == selectedTag) {
                        Icon(
                            painterResource(ToDometerIcons.Check),
                            "Selected",
                            tint = if (color != Color.Unspecified) {
                                MaterialTheme.colorScheme.background
                            } else {
                                MaterialTheme.colorScheme.primary
                            }
                        )
                    }
                    if (tag == Tag.UNSPECIFIED && tag != selectedTag) {
                        Icon(
                            Icons.Rounded.FormatColorReset,
                            null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
    LaunchedEffect(selectedTag) {
        state.animateScrollToItem(tags.indexOf(selectedTag))
    }
}
