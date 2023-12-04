/*
 * Copyright 2020-2023 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package com.goldenraven.devkitwallet.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.*
import androidx.compose.material3.NavigationDrawerItemDefaults.colors
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.goldenraven.devkitwallet.R
import com.goldenraven.devkitwallet.ui.Screen
import com.goldenraven.devkitwallet.ui.theme.DevkitWalletColors
import com.goldenraven.devkitwallet.ui.theme.jetBrainsMonoSemiBold
import com.goldenraven.devkitwallet.ui.wallet.WalletNavigation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.animation.ExperimentalAnimationApi::class)
@Composable
internal fun WalletRoot(navController: NavController) {

    val scope = rememberCoroutineScope()

    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val items = listOf(Icons.Default.Favorite, Icons.Default.Face, Icons.Default.Email, Icons.Default.Face)
    val selectedItem = remember { mutableStateOf(items[0]) }

    val navigationItemColors = colors(
        selectedContainerColor = DevkitWalletColors.primary,
        unselectedContainerColor = DevkitWalletColors.primary,
        selectedTextColor = DevkitWalletColors.white,
        unselectedTextColor = DevkitWalletColors.white
    )

    ModalNavigationDrawer (
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    Modifier
                        .background(color = DevkitWalletColors.secondary)
                        .height(300.dp)
                        .fillMaxHeight()
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_testnet_logo),
                        contentDescription = "Bitcoin testnet logo",
                        Modifier
                            .size(90.dp)
                            .padding(bottom = 16.dp)
                    )
                    Text(
                        text = "BDK Android Sample Wallet",
                        color = DevkitWalletColors.white
                    )
                    Spacer(modifier = Modifier.padding(16.dp))
                    Text(
                        "Version: 0.1.0",
                        color = DevkitWalletColors.white,
                    )
                }
                Column(
                    Modifier.fillMaxHeight().background(color = DevkitWalletColors.primary)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    NavigationDrawerItem(
                        label = { Text("About") },
                        selected = items[0] == selectedItem.value,
                        onClick = { navController.navigate(Screen.AboutScreen.route) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        colors = navigationItemColors
                    )
                    NavigationDrawerItem(
                        label = { Text("Recovery Phrase") },
                        selected = items[1] == selectedItem.value,
                        onClick = { navController.navigate(Screen.RecoveryPhraseScreen.route) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        colors = navigationItemColors
                    )
                    NavigationDrawerItem(
                        label = { Text("Electrum Server") },
                        selected = items[2] == selectedItem.value,
                        onClick = { navController.navigate(Screen.ElectrumScreen.route) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        colors = navigationItemColors
                    )
                }
            }
        },
        content = {
            Scaffold(
                topBar = { WalletAppBar(scope, drawerState) },
            ) { padding ->
                WalletNavigation(padding)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WalletAppBar(scope: CoroutineScope, drawerState: DrawerState) {
    CenterAlignedTopAppBar(
        title = { AppTitle() },
        navigationIcon = {
            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                Icon(
                    imageVector = Icons.Rounded.Menu,
                    contentDescription = "Open drawer",
                    tint = DevkitWalletColors.white
                )
            }
        },
        // actions = fun RowScope.() {},
        colors = topAppBarColors(
        // containerColor = Color.Red,
        containerColor = DevkitWalletColors.primaryDark,
        // scrolledContainerColor = MaterialTheme.colorScheme.applyTonalElevation(
        //     // backgroundColor = containerColor,
        //     // elevation = TopAppBarSmallTokens.OnScrollContainerElevation
        // )
        )
    )
}

@Composable
internal fun AppTitle() {
    Text(
        text = "BDK Sample Wallet",
        color = DevkitWalletColors.white,
        fontFamily = jetBrainsMonoSemiBold,
        fontSize = 20.sp,
        // modifier = Modifier.background(Color.Magenta)
    )
}
