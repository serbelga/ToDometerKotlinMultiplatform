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

package dev.sergiobelda.todometer.ui.about

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sergiobelda.todometer.common.android.extensions.getVersionName
import dev.sergiobelda.todometer.common.compose.ui.R
import dev.sergiobelda.todometer.common.compose.ui.about.AboutItemCard
import dev.sergiobelda.todometer.common.compose.ui.about.AboutTopBar
import dev.sergiobelda.todometer.common.compose.ui.components.title.ToDometerTitle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    openGithub: () -> Unit,
    openLicenses: () -> Unit,
    navigateBack: () -> Unit
) {
    var privacyPolicyDialogState by remember { mutableStateOf(false) }
    Scaffold(
        topBar = { AboutTopBar(navigateBack = navigateBack) }
    ) { paddingValues ->
        if (privacyPolicyDialogState) {
            PrivacyPolicyDialog { privacyPolicyDialogState = false }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(paddingValues)
        ) {
            ToDometerTitle()
            Spacer(modifier = Modifier.height(72.dp))
            AboutItemCard(
                onCardClick = openGithub,
                icon = {
                    Icon(
                        painterResource(R.drawable.ic_github_24),
                        contentDescription = stringResource(id = R.string.github)
                    )
                },
                text = { Text(stringResource(id = R.string.github)) }
            )
            AboutItemCard(
                onCardClick = { privacyPolicyDialogState = true },
                icon = {
                    Icon(
                        Icons.Rounded.Description,
                        contentDescription = stringResource(id = R.string.privacy_policy)
                    )
                },
                text = { Text(stringResource(id = R.string.privacy_policy)) }
            )
            AboutItemCard(
                onCardClick = openLicenses,
                icon = {
                    Icon(
                        Icons.Rounded.Code,
                        contentDescription = stringResource(id = R.string.open_source_licenses)
                    )
                },
                text = { Text(stringResource(id = R.string.open_source_licenses)) }
            )
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            Text(
                text = LocalContext.current.getVersionName() ?: "",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}
