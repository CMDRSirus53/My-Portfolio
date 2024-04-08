package com.csu_itc303_team1.adhdtaskmanager

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

// Switch Row

@Composable
fun SwitchRow(
    title: String,
    desc: String,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    enabled: Boolean = true,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        //.toggleable(
        // value = checked,
        // enabled = enabled,
        //role = Role.Switch,
        //onValueChange = onCheckedChange
        //),
        verticalAlignment = Alignment.CenterVertically
    ){

        Column(
            modifier = Modifier.weight(1.0f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ){

            val contentAlpha = if (enabled) ContentAlpha.high else ContentAlpha.disabled

            Text(
                text = title,
                style = MaterialTheme.typography.body1,
                maxLines = 1,
                modifier = Modifier.alpha(contentAlpha)
            )

            Text(
                text = desc,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.alpha(contentAlpha)
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = null,
            enabled = enabled)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EDropDownRow (){
    val context = LocalContext.current
    val months = arrayOf("January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December")
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(months[0]) }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ){

        Text(
            text = "Example: Birth Month",
            style = MaterialTheme.typography.body1,
            maxLines = 1
        )

        Text(
            text = "The Month on which you were born.",
            style = MaterialTheme.typography.body2
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                }
            ) {
                TextField(
                    value = selectedText,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    months.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(text = item) },
                            onClick = {
                                selectedText = item
                                expanded = false
                                Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun TextFieldEdittor () {

    val profileName = remember { mutableStateOf(TextFieldValue()) }

    Row() {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {

            Text(
                text = "Example: Change Profile Name",
                style = MaterialTheme.typography.body1,
                maxLines = 1
            )

            Text(
                text = "Change the Name on your profile",
                style = MaterialTheme.typography.body2
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Current Profile Name:",
                style = MaterialTheme.typography.body2
            )

            Text(
                text = profileName.value.text,
                style = MaterialTheme.typography.body1
            )

            TextField(
                value = profileName.value,
                onValueChange = {profileName.value = it}
            )
        }
    }
}