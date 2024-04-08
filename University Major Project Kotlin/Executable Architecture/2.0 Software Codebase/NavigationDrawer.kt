package com.csu_itc303_team1.adhdtaskmanager

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// The Header of the Navigation Menu
@Composable
fun DrawerHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 64.dp),
        contentAlignment = Alignment.Center
    ){
        Text(text = "Header", fontSize = 60.sp)
    }
}

// Drawer Item for each Screen the Menu will point to will be in the form of a DrawerItem
@Composable
fun DrawerItem(item: Screen, selected: Boolean, onItemClick: (Screen) -> Unit){

    val background = if(selected) R.color.white else android.R.color.transparent

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(item) }
            .height(45.dp)
            .background(colorResource(id = background))
            .padding(start = 10.dp)
    ) {
        // Menu Icon for the Drawer Item
        Image(
            painter = painterResource(id = item.icon),
            contentDescription = item.title,
            colorFilter = ColorFilter.tint(Color.Black),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .height(24.dp)
                .width(24.dp)
        )
        Spacer(modifier = Modifier.width(7.dp))
        // Text for the Drawer Item
        Text(
            text = item.title,
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}

// This section is the main part of the Navigation Menu, the Drawer Body.
@Composable
fun DrawerBody(scope: CoroutineScope, scaffoldState: ScaffoldState, navController: NavController) {

    // Create a list of Screen objects
    val screens = listOf(
        Screen.TodoScreen,
        Screen.RewardsScreen,
        Screen.LeaderboardScreen,
        Screen.SettingsScreen,

    )

    // Column to store all the items
    Column(
        modifier = Modifier
            .background(color = Color.White)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        // For Each Screen, it creates a Drawer Item
        screens.forEach { items ->
            DrawerItem(item = items, selected = currentRoute == items.route, onItemClick = {

                // When an item is clicked, it navigates to the route of the screen object
                // and displays the screen on the Main Activity Screen. It then remembers which
                // screen it is on
                navController.navigate(items.route) {
                    navController.graph.startDestinationRoute?.let { route ->
                        popUpTo(route) {
                            saveState = true
                        }
                    }
                    launchSingleTop = true
                    restoreState = true
                }

                scope.launch {
                    scaffoldState.drawerState.close()
                }
            })
        }
    }
}