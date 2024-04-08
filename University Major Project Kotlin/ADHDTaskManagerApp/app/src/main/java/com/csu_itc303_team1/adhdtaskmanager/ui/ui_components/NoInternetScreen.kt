package com.csu_itc303_team1.adhdtaskmanager.ui.ui_components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NoInternetScreen(){
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(text = "No Internet Connection")
        Icon(
            modifier = Modifier
                .padding(10.dp)
                .size(100.dp),
            imageVector = Icons.Filled.WifiOff,
            contentDescription = "No Internet Connection"
        )

    }
}