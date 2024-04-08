package com.csu_itc303_team1.adhdtaskmanager

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Welcome To The Help Page",style = TextStyle(fontSize = 24.sp,
                    color = Color.Blue, fontStyle = FontStyle.Italic)
                )
                },

                )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.fillMaxWidth()
            ) {

            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /*TODO*/

                }
            ) {
                Icon(Icons.Filled.Home, contentDescription = "Home")
            }
        },

        ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(20.dp, 20.dp, 0.dp, 0.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.Start,

            ) {
            Spacer(modifier = Modifier.height(86.dp))
            Text("Hello",style = TextStyle(fontSize = 20.sp))


            Spacer(modifier = Modifier.height(36.dp))
            Text("World",style = TextStyle(fontSize = 20.sp))
        }
    }
}