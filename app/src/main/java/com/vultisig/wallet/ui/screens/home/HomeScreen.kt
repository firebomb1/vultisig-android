package com.vultisig.wallet.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.vultisig.wallet.R
import com.vultisig.wallet.ui.components.UiIcon
import com.vultisig.wallet.ui.models.HomeUiModel
import com.vultisig.wallet.ui.models.HomeViewModel
import com.vultisig.wallet.ui.theme.Theme

@Composable
internal fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    HomeScreen(
        navController = navController,
        state = state,
        onOpenSettings = viewModel::openSettings,
        onEdit = viewModel::edit,
        onToggleVaults = viewModel::toggleVaults,
        onSelectVault = viewModel::selectVault,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    navController: NavHostController,
    state: HomeUiModel,
    onOpenSettings: () -> Unit = {},
    onEdit: () -> Unit = {},
    onToggleVaults: () -> Unit = {},
    onSelectVault: (vaultId: String) -> Unit = {}
) {
    val caretRotation by animateFloatAsState(
        targetValue = if (state.showVaultList) -90f else 90f,
        animationSpec = tween(300),
        label = "HomeScreen caretRotation",
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .clickable(onClick = onToggleVaults),
                    ) {
                        Text(
                            text = state.vaultName,
                            style = Theme.montserrat.subtitle1,
                            fontWeight = FontWeight.Bold,
                            color = Theme.colors.neutral0,
                        )

                        UiIcon(
                            drawableResId = R.drawable.caret_right,
                            size = 12.dp,
                            modifier = Modifier.rotate(caretRotation)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Theme.colors.oxfordBlue800,
                    titleContentColor = Theme.colors.neutral0,
                ),
                navigationIcon = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            imageVector = Icons.Outlined.Menu,
                            contentDescription = "settings",
                            tint = Theme.colors.neutral0,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_edit_square_24),
                            contentDescription = "edit",
                            tint = Theme.colors.neutral0,
                        )
                    }
                }
            )
        },
    ) {
        Box(
            modifier = Modifier.padding(it),
        ) {
            if (state.selectedVaultId != null) {
                VaultAccountsScreen(
                    navHostController = navController,
                    vaultId = state.selectedVaultId,
                )
            }

            AnimatedVisibility(
                visible = state.showVaultList,
                enter = slideInVertically(initialOffsetY = { height -> -height }),
                exit = slideOutVertically(targetOffsetY = { height -> -height })
            ) {
                VaultListScreen(
                    navController = navController,
                    onSelectVault = onSelectVault,
                )
            }
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreen(
        navController = rememberNavController(),
        state = HomeUiModel(
            showVaultList = false,
            vaultName = "Vault Name",
            selectedVaultId = "1",
        )
    )
}
