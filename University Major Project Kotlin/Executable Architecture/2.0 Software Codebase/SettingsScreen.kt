package com.csu_itc303_team1.adhdtaskmanager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// For now, this is just a placeholder code for a functional screen

@Composable
fun SettingsScreen() {

    Column(verticalArrangement = Arrangement.Center) {
        SwitchRow(
            title = "Example: Dark Mode",
            desc = "Changes the Application's Theme from Light to Dark.",
            checked = false,
            onCheckedChange = null,
            enabled = true
        )
        Divider(color = Color.Black, thickness = 1.dp)
        EDropDownRow()
        Divider(color = Color.Black, thickness = 1.dp)
        TextFieldEdittor()
        Divider(color = Color.Black, thickness = 1.dp)
    }

}
