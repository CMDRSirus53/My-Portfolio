package com.csu_itc303_team1.adhdtaskmanager

import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AppTopAppBar(scope: CoroutineScope, scaffoldState: ScaffoldState){

    // This is for the Bar at the top of the application. This will be present on every page.
    TopAppBar(
        title = { Text(text = "ADHD Task Manager", fontSize = 18.sp) }, // Label on the bar
        navigationIcon = {                                              // Navigation Icon
            IconButton(onClick = {
                scope.launch {                          // Opens Navigation Drawer
                    scaffoldState.drawerState.open()
                }
            }) {
                Icon(Icons.Filled.Menu, "")     // Can change icon of the Navigation Icon here
            }
        },
        backgroundColor = MaterialTheme.colors.primary,         // Colours of the bar
        contentColor = MaterialTheme.colors.onPrimary
    )
}
