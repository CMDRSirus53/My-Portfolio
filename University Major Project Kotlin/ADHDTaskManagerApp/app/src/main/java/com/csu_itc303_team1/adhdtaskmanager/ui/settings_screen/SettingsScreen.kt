package com.csu_itc303_team1.adhdtaskmanager.ui.settings_screen

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.csu_itc303_team1.adhdtaskmanager.ui.ui_components.MainTopAppBar
import com.csu_itc303_team1.adhdtaskmanager.utils.firebase.AuthUiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


// For now, this is just a placeholder code for a functional screen

@OptIn(ExperimentalCoilApi::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    currentUser: AuthUiClient,
    context: Context,
    scope: CoroutineScope,
    drawerState: DrawerState
) {
    var showImagePicker by remember { mutableStateOf(false) }
    val isDarkTheme by settingsViewModel.isDarkTheme.observeAsState(initial = false)
    val imageList by settingsViewModel.profileImages.observeAsState(initial = listOf())
    val currentProfileImage by settingsViewModel.currentUserProfileImage.observeAsState(initial = null)
    var workTimeInput by remember { mutableStateOf(settingsViewModel.getWorkTimerValue().toString()) }
    var breakTimeInput by remember { mutableStateOf(settingsViewModel.getBreakTimerValue().toString()) }
    var selectedCountry by remember { mutableStateOf("") }
    var showCountryDropdown by remember { mutableStateOf(false) }

    // Fetch the profile pictures once when the screen is created
    LaunchedEffect(key1 = Unit, key2 = currentUser.getSignedInUser()?.userId) {
        // Fetch all profile pictures.
        settingsViewModel.fetchProfilePictures()

        // Fetch current user's profile picture.
        val userId = currentUser.getSignedInUser()?.userId
        if (userId != null) {
            settingsViewModel.fetchCurrentUserProfileImage(userId)
        }
    }

    val countryList = listOf(
        "United States", "Canada", "United Kingdom", "Australia", "Germany",
        "India", "France", "Italy", "Spain", "Brazil", "Russia" //... add more countries
    )

    Scaffold (
        topBar = { MainTopAppBar(scope = scope, drawerState = drawerState)},
        content =  {
        Box(modifier = Modifier.fillMaxSize()) {
            // Original Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                verticalArrangement = Arrangement.Top
            ) {

                SwitchRow(
                    title = "Dark Mode",
                    titleColor = Color(4, 64, 170), // Made adjustments here
                    titleFontWeight = FontWeight.Bold, // And here
                    titleFontSize = 20.sp, // Add this line
                    desc = "Changes the Application's Theme from Light to Dark.",
                    descColor = Color(4, 64, 165, 255),
                    checked = isDarkTheme,
                    onCheckedChange = { settingsViewModel.toggleTheme(it) },
                    enabled = true
                )

                Divider(color = Color.Black, thickness = 1.dp)

                Spacer(modifier = Modifier.height(10.dp))

                // Heading
                Text(
                    text = "Update your user profile",
                    color = Color(4, 64, 165),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )

                Spacer(modifier = Modifier.height(5.dp))

                var username by remember { mutableStateOf("") }

                TextField(
                    value = username,
                    onValueChange = { newValue ->
                        if (newValue.length <= 8) {
                            username = newValue
                        }
                    },
                    label = { Text("Username") },
                    singleLine = true // This ensures the TextField behaves like a single-line input.
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(onClick = {
                    scope.launch {
                        currentUser.updateUsername(username)
                        Toast.makeText(context, "Username Updated", Toast.LENGTH_LONG).show()
                        username = ""
                    }
                }) {
                    Text(text = "Update Username")
                }


                Spacer(modifier = Modifier.height(10.dp))

// Title for Country Selection
                Text(text = " ", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                Spacer(modifier = Modifier.height(5.dp))

// Dropdown for Country Selection
                Box(
                    modifier = Modifier
                        .width(LocalConfiguration.current.screenWidthDp.dp * 0.72f) // Set width to 1/3 of screen width
                        .border(
                            width = 1.dp,
                            color = Color.Gray
                        ) // To give a boundary similar to TextField
                        .clickable { showCountryDropdown = !showCountryDropdown }
                        .padding(12.dp)
                ) {
                    Text(text = if (selectedCountry.isNotEmpty()) selectedCountry else "Select a Country")

                    DropdownMenu(expanded = showCountryDropdown, onDismissRequest = { showCountryDropdown = false }) {
                        countryList.forEach { country ->
                            DropdownMenuItem(onClick = {
                                selectedCountry = country
                                showCountryDropdown = false
                            }) {
                                Text(text = country)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(onClick = {
                    scope.launch {
                        currentUser.getSignedInUser()?.userId?.let { userId ->
                            settingsViewModel.updateUserCountry(userId, selectedCountry)
                            Toast.makeText(context, "Country Updated", Toast.LENGTH_LONG).show()
                            selectedCountry = ""
                        }
                    }
                }) {
                    Text(text = "Update Country")
                }






                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = { showImagePicker = true }) {
                        Text(text = "Update Profile Mascot")
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    currentProfileImage?.let { imageUrl ->
                        Image(
                            painter = rememberImagePainter(data = imageUrl),
                            contentDescription = "Current Profile Image",
                            modifier = Modifier.size(100.dp)
                        )
                    }
                }

                Divider(color = Color.Black, thickness = 1.dp)

                Spacer(modifier = Modifier.height(15.dp))

// This should be outside the AnimatedVisibility
// Heading for Pomodoro Timer Adjustment
                Text(
                    text = "Pomodoro Timer Adjustment",
                    color = Color(4, 64, 165),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "Input how long you would like your timer to run for then click the tick to confirm.",
                    color = Color(4, 94, 165, 255),
                )

                Spacer(modifier = Modifier.height(5.dp))

// Wrapping the TextFields and the save button inside a Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Putting the two TextFields next to each other
                    Column {
                        TextField(
                            value = workTimeInput,
                            onValueChange = {
                                workTimeInput = it
                            },
                            label = { Text("Work Timer (minutes)") }
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        TextField(
                            value = breakTimeInput,
                            onValueChange = {
                                breakTimeInput = it
                            },
                            label = { Text("Break Timer (minutes)") }
                        )
                    }

                    // Making the button circular and placed to the right
                    Button(
                        onClick = {
                            val workTime = workTimeInput.toIntOrNull() ?: 0
                            val breakTime = breakTimeInput.toIntOrNull() ?: 0
                            settingsViewModel.saveTimerValues(workTime, breakTime)
                        },
                        modifier = Modifier.size(50.dp),  // Adjust size as needed
                        shape = CircleShape  // Making it circular
                    ) {
                        Text(text = "✓", textAlign = TextAlign.Center)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            AnimatedVisibility(
                visible = showImagePicker,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(initialAlpha = 0.3f),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(targetAlpha = 0.0f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(4, 94, 165, 255))
                ) {
                    IconButton(
                        onClick = { showImagePicker = false },
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(top = 70.dp, start = 10.dp) // Adjust padding to ensure it's visible
                            .background(shape = CircleShape, color = Color.White) // to ensure it's visible
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Blue)
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 120.dp), // Adjust the top padding if needed
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(imageList.chunked(2)) { rowImages ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                rowImages.forEach { imageUrl ->
                                    Image(
                                        painter = rememberImagePainter(data = imageUrl),
                                        contentDescription = "Image from Firebase Storage",
                                        modifier = Modifier
                                            .size(150.dp)
                                            .clickable {
                                                scope.launch {
                                                    currentUser.getSignedInUser()?.userId?.let { userId ->
                                                        settingsViewModel.updateProfileImage(
                                                            userId,
                                                            imageUrl
                                                        )
                                                        Toast
                                                            .makeText(
                                                                context,
                                                                "Profile Image Updated",
                                                                Toast.LENGTH_LONG
                                                            )
                                                            .show()
                                                        showImagePicker = false
                                                    }
                                                }
                                            }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    )
}