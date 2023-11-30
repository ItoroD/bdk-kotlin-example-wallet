/*
 * Copyright 2020-2022 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package com.goldenraven.devkitwallet.ui.wallet

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.goldenraven.devkitwallet.R
import com.goldenraven.devkitwallet.data.Wallet
import com.goldenraven.devkitwallet.ui.Screen
import com.goldenraven.devkitwallet.ui.composables.LoadingAnimation
import com.goldenraven.devkitwallet.ui.theme.DevkitWalletColors
import com.goldenraven.devkitwallet.ui.theme.firaMono
import com.goldenraven.devkitwallet.ui.theme.firaMonoMedium
import com.goldenraven.devkitwallet.utilities.TAG
import com.goldenraven.devkitwallet.utilities.formatInBtc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class WalletViewModel : ViewModel() {

    private var _balance: MutableLiveData<ULong> = MutableLiveData(0u)
    val balance: LiveData<ULong>
        get() = _balance

    private var _syncing: MutableLiveData<Boolean> = MutableLiveData(false)
    val syncing: LiveData<Boolean>
        get() = _syncing

    fun updateBalance() {
        _syncing.value = true
        viewModelScope.launch(Dispatchers.IO) {
            Wallet.sync()
            withContext(Dispatchers.Main) {
                _balance.value = Wallet.getBalance()
                _syncing.value = false
            }
        }
    }
}

@Composable
internal fun HomeScreen(
    navController: NavHostController,
    paddingValues: PaddingValues,
    walletViewModel: WalletViewModel = viewModel()
) {

    val networkAvailable: Boolean = isOnline(LocalContext.current)
    val syncing by walletViewModel.syncing.observeAsState(true)
    val balance by walletViewModel.balance.observeAsState()
    if (networkAvailable && !Wallet.isBlockChainCreated()) {
        Log.i(TAG, "Creating new blockchain")
        Wallet.createBlockchain()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DevkitWalletColors.primary)
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.padding(24.dp))
        Row(
            Modifier
                .fillMaxWidth(0.9f)
                .padding(horizontal = 8.dp)
                .background(
                    color = DevkitWalletColors.primaryLight,
                    shape = RoundedCornerShape(16.dp)
                )
                .height(100.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_bitcoin_logo),
                contentDescription = "Bitcoin testnet logo",
                Modifier
                    .align(Alignment.CenterVertically)
                    .rotate(-13f)
            )
            Text(
                balance.formatInBtc(),
                fontFamily = firaMonoMedium,
                fontSize = 32.sp,
                color = DevkitWalletColors.white
            )
        }
        Spacer(modifier = Modifier.padding(4.dp))
        Row(
            modifier = Modifier.height(40.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (syncing) LoadingAnimation()
        }

        if (!networkAvailable) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(color = DevkitWalletColors.accent2)
                    .height(50.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Network unavailable",
                    fontFamily = firaMonoMedium,
                    fontSize = 18.sp,
                    color = DevkitWalletColors.white
                )
            }
        }
        Button(
            onClick = { walletViewModel.updateBalance() },
            colors = ButtonDefaults.buttonColors(
                containerColor = DevkitWalletColors.secondary,
                disabledContainerColor = DevkitWalletColors.secondary,
            ),
            enabled = networkAvailable,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .height(80.dp)
                .fillMaxWidth(0.9f)
                .padding(vertical = 8.dp, horizontal = 8.dp)
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
        ) {
            Text(
                text = "sync",
                fontSize = 16.sp,
                fontFamily = firaMono,
                textAlign = TextAlign.Center,
                lineHeight = 28.sp,
            )
        }


        Button(
            onClick = { navController.navigate(Screen.TransactionsScreen.route) },
            colors = ButtonDefaults.buttonColors(
                containerColor = DevkitWalletColors.secondary,
                disabledContainerColor = DevkitWalletColors.secondary,
            ),
            shape = RoundedCornerShape(16.dp),
            enabled = networkAvailable,
            modifier = Modifier
                .height(80.dp)
                .fillMaxWidth(0.9f)
                .padding(vertical = 8.dp, horizontal = 8.dp)
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
        ) {
            Text(
                text = "transaction history",
                fontSize = 16.sp,
                fontFamily = firaMono,
                textAlign = TextAlign.Center,
                lineHeight = 28.sp,
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(140.dp)
                .fillMaxWidth(0.9f)
        ) {
            Button(
                onClick = { navController.navigate(Screen.ReceiveScreen.route) },
                colors = ButtonDefaults.buttonColors(DevkitWalletColors.accent1),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .height(160.dp)
                    .padding(vertical = 8.dp, horizontal = 8.dp)
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
            ) {
                Text(
                    text = "receive",
                    fontSize = 16.sp,
                    fontFamily = firaMono,
                    textAlign = TextAlign.End,
                    lineHeight = 28.sp,
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .align(Alignment.Bottom)
                )
            }

            Button(
                onClick = { navController.navigate(Screen.SendScreen.route) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = DevkitWalletColors.accent2,
                    disabledContainerColor = DevkitWalletColors.accent2,
                ),
                shape = RoundedCornerShape(16.dp),
                enabled = networkAvailable,
                modifier = Modifier
                    .height(160.dp)
                    .padding(vertical = 8.dp, horizontal = 8.dp)
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
            ) {
                Text(
                    text = "send",
                    fontSize = 16.sp,
                    fontFamily = firaMono,
                    textAlign = TextAlign.End,
                    lineHeight = 28.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Bottom)
                )
            }
        }
    }
}

fun isOnline(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities =
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
    if (capabilities != null) {
        when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            }
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            }
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
    }
    return false
}
