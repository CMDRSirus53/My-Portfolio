package com.csu_itc303_team1.adhdtaskmanager.ui.settings_screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.ContentAlpha
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

// Switch Row

@Composable
fun SwitchRow(
    title: String,
    desc: String,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    enabled: Boolean = true,
    titleColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Unspecified,  // Default value if not provided
    titleFontWeight: androidx.compose.ui.text.font.FontWeight = androidx.compose.ui.text.font.FontWeight.Normal,  // Default value if not provided
    descColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Unspecified,
    titleFontSize: TextUnit = MaterialTheme.typography.bodyMedium.fontSize


) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ){

        Column(
            modifier = Modifier.weight(1.0f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ){

            val contentAlpha = if (enabled) ContentAlpha.high else ContentAlpha.disabled

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = titleFontSize),
                maxLines = 1,
                modifier = Modifier.alpha(contentAlpha),
                color = titleColor,
                fontWeight = titleFontWeight
            )


            Text(
                text = desc,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.alpha(contentAlpha),
                color = descColor  // Apply the color
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange, // Update this line
            enabled = enabled)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1
        )

        Text(
            text = "The Month on which you were born.",
            style = MaterialTheme.typography.bodyMedium
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

    Row {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {

            Text(
                text = "Example: Change Profile Name",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )

            Text(
                text = "Change the Name on your profile",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Current Profile Name:",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = profileName.value.text,
                style = MaterialTheme.typography.bodyMedium
            )

            TextField(
                value = profileName.value,
                onValueChange = {profileName.value = it}
            )
        }
    }
}