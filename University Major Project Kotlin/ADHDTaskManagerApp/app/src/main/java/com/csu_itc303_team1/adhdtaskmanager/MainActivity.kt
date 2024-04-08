package com.csu_itc303_team1.adhdtaskmanager

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.csu_itc303_team1.adhdtaskmanager.ui.completed_screen.CompletedScreen
import com.csu_itc303_team1.adhdtaskmanager.ui.help_screen.HelpScreen
import com.csu_itc303_team1.adhdtaskmanager.ui.leaderboard_screen.LeaderboardScreen
import com.csu_itc303_team1.adhdtaskmanager.ui.leaderboard_screen.LeaderboardViewModel
import com.csu_itc303_team1.adhdtaskmanager.ui.leaderboard_screen.usersList
import com.csu_itc303_team1.adhdtaskmanager.ui.pomodoro_timer.PomodoroTimerScreen
import com.csu_itc303_team1.adhdtaskmanager.ui.reward_screen.RewardViewModel
import com.csu_itc303_team1.adhdtaskmanager.ui.reward_screen.RewardsScreen
import com.csu_itc303_team1.adhdtaskmanager.ui.settings_screen.SettingsScreen
import com.csu_itc303_team1.adhdtaskmanager.ui.settings_screen.SettingsViewModel
import com.csu_itc303_team1.adhdtaskmanager.ui.settings_screen.SettingsViewModelFactory
import com.csu_itc303_team1.adhdtaskmanager.ui.sign_in.SignInScreen
import com.csu_itc303_team1.adhdtaskmanager.ui.sign_in.SignInViewModel
import com.csu_itc303_team1.adhdtaskmanager.ui.theme.ADHDTaskManagerTheme
import com.csu_itc303_team1.adhdtaskmanager.ui.todo_screen.TodoScreen
import com.csu_itc303_team1.adhdtaskmanager.ui.todo_screen.TodoViewModel
import com.csu_itc303_team1.adhdtaskmanager.utils.alarm_manager.AlarmSchedulerImpl
import com.csu_itc303_team1.adhdtaskmanager.utils.blurBitmap
import com.csu_itc303_team1.adhdtaskmanager.utils.captureScreenshotWhenReady
import com.csu_itc303_team1.adhdtaskmanager.utils.connectivity.ConnectivityObserverImpl
import com.csu_itc303_team1.adhdtaskmanager.utils.firebase.AuthUiClient
import com.csu_itc303_team1.adhdtaskmanager.utils.firebase.FirebaseCallback
import com.csu_itc303_team1.adhdtaskmanager.utils.firestore_utils.Response
import com.csu_itc303_team1.adhdtaskmanager.utils.firestore_utils.UsersRepo
import com.csu_itc303_team1.adhdtaskmanager.utils.firestore_utils.UsersViewModel
import com.csu_itc303_team1.adhdtaskmanager.utils.local_database.TodoDatabase
import com.csu_itc303_team1.adhdtaskmanager.utils.nav_utils.Screen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory


/**
 * Main activity
 *
 * @constructor Create empty Main activity
 */
@Suppress("UNCHECKED_CAST")
class MainActivity : ComponentActivity() {

    private val connectivityObserver = ConnectivityObserverImpl(this)

    // Instantiate the UsersViewModel
    private val usersViewModel by viewModels<UsersViewModel>()


    private val settingsViewModel by viewModels<SettingsViewModel> {
        SettingsViewModelFactory(application, usersViewModel)
    }

    private val factory = SupportFactory(SQLiteDatabase.getBytes(BuildConfig.TODO_DATABASE_PASSPHRASE.toCharArray()))
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            TodoDatabase::class.java,
            "todo.db"
        ).openHelperFactory(factory).fallbackToDestructiveMigration().build()
    }

    private val rewardViewModel by viewModels<RewardViewModel>(
        factoryProducer = {
            object: ViewModelProvider.Factory{
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return RewardViewModel(application) as T
                }
            }
        }
    )

    private lateinit var navController: NavHostController
    private lateinit var leadViewModel: LeaderboardViewModel
    private lateinit var userViewModel: UsersViewModel
    private val usersRepo by lazy {
        UsersRepo()
    }


    private val googleAuthUiClient by lazy {
        AuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext),
            usersRepo = usersRepo
        )
    }

    // Boolean value to check if the user is signed in or not
    private val isSignedIn by lazy {
        mutableStateOf(false)
    }


    private var defaultProfileImageUrl: String? = null

    /**
     * Fetch default profile image
     *
     */
    fun fetchDefaultProfileImage() {
        val storageReference = FirebaseStorage.getInstance().reference
        val defaultProfileImageRef = storageReference.child("default-user-profile-picture/default_image.jpg")

        defaultProfileImageRef.downloadUrl.addOnSuccessListener { uri ->
            defaultProfileImageUrl = uri.toString()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class,
        InternalCoroutinesApi::class
    )

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val alarmManager = AlarmSchedulerImpl(this)


        val viewModel by viewModels<TodoViewModel>(
            factoryProducer = {
                object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return TodoViewModel(db.todoDao) as T
                    }
                }
            }
        )

        userViewModel = ViewModelProvider(this) [UsersViewModel::class.java]

        googleAuthUiClient.addAuthStateListener {
            isSignedIn.value = it
            if (it) { // if signed in

                // Define contentView here
                val contentView = findViewById<ViewGroup>(android.R.id.content)
                val db = FirebaseFirestore.getInstance()
                val userRef = db.collection("users").document(FirebaseAuth.getInstance().currentUser?.uid ?: return@addAuthStateListener)
                val uID = googleAuthUiClient.getSignedInUser()?.userId!!


                val id = googleAuthUiClient.getSignedInUser()?.userId.toString()
                userViewModel.checkUserInFirestore(id, googleAuthUiClient)


                // Capture and blur screenshot
                captureScreenshotWhenReady(contentView) { screenshot ->
                    val blurredScreenshot = blurBitmap(screenshot, applicationContext)

                    // Display blurred screenshot as a background
                    val blurredBackground = ImageView(applicationContext)
                    blurredBackground.setImageBitmap(blurredScreenshot)
                    val params = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                    contentView.addView(blurredBackground, params)

                    // Inflate the custom toast layout
                    val layoutInflater =
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    val customToastRoot = layoutInflater.inflate(R.layout.custom_toast, null)

                    val customToastMessage =
                        customToastRoot.findViewById<TextView>(R.id.custom_toast_message)
                    customToastMessage.text =
                        "Welcome back! Be sure to check out the leaderboard for the latest standings"

                    // Find the LottieAnimationView and start the animation
                    val lottieAnimation =
                        customToastRoot.findViewById<com.airbnb.lottie.LottieAnimationView>(R.id.lottieAnimation)
                    lottieAnimation.playAnimation()

                    // Create a PopupWindow with custom view
                    val customPopup = PopupWindow(
                        customToastRoot,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        false
                    )
                    customPopup.animationStyle = android.R.style.Animation_Toast
                    if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                        customPopup.showAtLocation(
                            findViewById(android.R.id.content),
                            Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL,
                            0,
                            0
                        )
                    }

                    // Use a Handler to control the duration of the PopupWindow
                    Handler(Looper.getMainLooper()).postDelayed({
                        customPopup.dismiss()
                        // Remove or hide blurred background when done
                        contentView.removeView(blurredBackground)
                    }, 6000) // Dismiss popup after 6 seconds
                }
            }
        }

        fetchDefaultProfileImage()


        setContent {


            val isDarkTheme by settingsViewModel.isDarkTheme.observeAsState(initial = false)
            val signInViewModel = viewModel<SignInViewModel>()
            val signInState by signInViewModel.state.collectAsState()
            // Retrieve's Leaderboard data onCreate
            leadViewModel = ViewModelProvider(this)[LeaderboardViewModel::class.java]
            userViewModel = ViewModelProvider(this) [UsersViewModel::class.java]
            // Puts it into a readable format
            getResponseUsingCallback()


            ADHDTaskManagerTheme(darkTheme = isDarkTheme) {


                /**
                 * This is where the Pomodoro Timer Notification is created
                 */

                val permission = rememberPermissionState(
                    permission = Manifest.permission.POST_NOTIFICATIONS
                )

                LaunchedEffect(key1 = permission.hasPermission){
                    if (!permission.hasPermission) {
                        permission.launchPermissionRequest()
                    }
                }

                // variables for remembering the state of the Coroutine Scope and Scaffold
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                navController = rememberNavController()
                rewardViewModel.allRewards.observeAsState(listOf())



                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { // In this Section is contents of the actual screen. A padding value had to
                    // be added in the lambda form.
                        contentPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(contentPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        // icons to mimic drawer destinations
                        val screenIcons = listOf(
                            R.drawable.ic_home,
                            R.drawable.ic_complete,
                            R.drawable.ic_rewards,
                            R.drawable.ic_leaderboard,
                            R.drawable.ic_pomodoro_timer,
                            R.drawable.ic_settings,
                            R.drawable.ic_help
                        )

                        // Create a list of Screen objects
                        val screens = listOf(
                            Screen.TodoScreen,
                            Screen.CompletedScreen,
                            Screen.RewardsScreen,
                            Screen.LeaderboardScreen,
                            Screen.PomodoroTimerScreen,
                            Screen.SettingsScreen,
                            Screen.HelpScreen,
                        )

                        val selectedItem = remember { mutableStateOf(screens[0]) }
                        LaunchedEffect(key1 = !isSignedIn.value){
                            drawerState.close()
                            selectedItem.value = screens[0]
                        }

                        ModalNavigationDrawer(
                            drawerState = drawerState,
                            gesturesEnabled = isSignedIn.value,
                            drawerContent = {
                                ModalDrawerSheet(
                                    drawerContainerColor = MaterialTheme.colorScheme.background,
                                    drawerTonalElevation = 2.dp,
                                    modifier = Modifier.padding(top = 64.dp)
                                ) {
                                    LazyColumn(content = {
                                        screens.forEach { screen ->
                                            item {
                                                NavigationDrawerItem(
                                                    modifier = Modifier.padding(
                                                        top = 20.dp,
                                                        start = 12.dp,
                                                        end = 12.dp
                                                    ),
                                                    colors = NavigationDrawerItemDefaults.colors(
                                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                                        selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                                        selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                                                        unselectedContainerColor = MaterialTheme.colorScheme.background,
                                                        unselectedIconColor = MaterialTheme.colorScheme.primary,
                                                        unselectedTextColor = MaterialTheme.colorScheme.primary
                                                    ),
                                                    icon = {
                                                        Icon(
                                                            painter = painterResource(
                                                                id = screenIcons[screens.indexOf(
                                                                    screen
                                                                )]
                                                            ),
                                                            contentDescription = screen.title,
                                                        )
                                                    },
                                                    label = {
                                                        Text(text = screen.title)
                                                    },
                                                    selected = screen.route == selectedItem.value.route,
                                                    onClick = {
                                                        selectedItem.value = screen
                                                        scope.launch {
                                                            drawerState.close()
                                                        }
                                                        navController.navigate(screen.route)
                                                    },
                                                )
                                            }
                                        }
                                        item {
                                            NavigationDrawerItem(
                                                colors = NavigationDrawerItemDefaults.colors(
                                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                                    selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                                                    unselectedContainerColor = MaterialTheme.colorScheme.background,
                                                    unselectedIconColor = MaterialTheme.colorScheme.primary,
                                                    unselectedTextColor = MaterialTheme.colorScheme.primary
                                                ),
                                                icon = {
                                                    Icon(
                                                        imageVector = Icons.Filled.ExitToApp,
                                                        contentDescription = "Sign Out",
                                                    )
                                                },
                                                label = {
                                                    Text(
                                                        text = "Sign Out",
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                },
                                                selected = false,
                                                onClick = {
                                                    scope.launch {
                                                        googleAuthUiClient.signOut(navController = navController)
                                                        Toast
                                                            .makeText(
                                                                applicationContext,
                                                                "Signed out",
                                                                Toast.LENGTH_LONG
                                                            )
                                                            .show()
                                                    }
                                                },
                                                modifier = Modifier.padding(
                                                    NavigationDrawerItemDefaults.ItemPadding
                                                )
                                            )
                                        }

                                        item {
                                            if (googleAuthUiClient.isUserAnonymous()) {
                                                Row(modifier = Modifier.fillMaxWidth()) {
                                                    Text(
                                                        text = "You are signed in anonymously, if you sign out you will lose all of your data.",
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Black,
                                                        color = Color(0xFFFC8B8B),
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        modifier = Modifier
                                                            .padding(
                                                                start = 20.dp,
                                                                end = 15.dp,
                                                                top = 10.dp
                                                            )
                                                            .height(40.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    )
                                }
                            }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize(),
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.Top
                            ) {

                                // The content itself is the navController's current state, or Home Screen
                                // on Default
                                val state by viewModel.state.collectAsState()
                                if (isSignedIn.value) {
                                    state.userId = googleAuthUiClient.getSignedInUser()?.userId ?: ""
                                    Log.d("MainActivity", "User ID: ${state.userId}")
                                }
                                //val rewardState by rewardViewModel.collectAsState()
                                val todoEvent = viewModel::onEvent


                                val launcher = rememberLauncherForActivityResult(
                                    contract = ActivityResultContracts.StartIntentSenderForResult(),
                                    onResult = { result ->
                                        if (result.resultCode == RESULT_OK) {
                                            lifecycleScope.launch {
                                                val signInResult =
                                                    googleAuthUiClient.signInWithIntent(
                                                        intent = result.data ?: return@launch
                                                    )
                                                signInViewModel.onSignInResult(signInResult)
                                            }
                                        }
                                    }
                                )

                                // NavHost for controlling the pages.
                                NavHost(
                                    navController = navController,
                                    startDestination = if (isSignedIn.value) Screen.TodoScreen.route else Screen.SignInScreen.route,
                                ) {
                                    // Home screen/to-do screen
                                    composable(
                                        route = Screen.TodoScreen.route
                                    ) {
                                        TodoScreen(
                                            state = state,
                                            onEvent = todoEvent,
                                            rewardViewModel = rewardViewModel,
                                            usersViewModel = userViewModel,
                                            alarmScheduler = alarmManager,
                                            navScope = scope,
                                            drawerState = drawerState
                                        )
                                    }

                                    // Settings Screen
                                    composable(route = Screen.SettingsScreen.route) {
                                        SettingsScreen(
                                            settingsViewModel = settingsViewModel,
                                            currentUser = googleAuthUiClient,
                                            context = applicationContext,
                                            scope = scope,
                                            drawerState = drawerState
                                        )
                                    }
                                    // Leaderboard Screen
                                    composable(
                                        route = Screen.LeaderboardScreen.route
                                    ) {
                                        LeaderboardScreen(
                                            connectivityObserver,
                                            scope,
                                            drawerState
                                        )
                                    }

                                    // Rewards Screen
                                    composable(
                                        route = Screen.RewardsScreen.route
                                    ) {
                                        RewardsScreen(
                                            rewardViewModel,
                                            userViewModel,
                                            connectivityObserver,
                                            scope,
                                            drawerState
                                        )
                                    }

                                    // Completed Task Screen
                                    composable(
                                        route = Screen.CompletedScreen.route
                                    ) {
                                        CompletedScreen(
                                            state,
                                            scope,
                                            drawerState
                                        )
                                    }

                                    // Sign In Screen
                                    composable(
                                        route = Screen.SignInScreen.route
                                    ) {


                                        LaunchedEffect(key1 = Unit) {
                                            if (googleAuthUiClient.getSignedInUser() != null) {
                                                navController.navigate("todo_screen")
                                                val id = googleAuthUiClient.getSignedInUser()?.userId.toString()
                                                //userViewModel.checkUserInFirestore(id, googleAuthUiClient)

                                            }
                                        }


                                        LaunchedEffect(key1 = signInState.isSignInSuccessful) {
                                            if (signInState.isSignInSuccessful) {
                                                Toast.makeText(
                                                    applicationContext,
                                                    "Sign in successful",
                                                    Toast.LENGTH_LONG
                                                ).show()

                                                navController.navigate("todo_screen")
                                                signInViewModel.resetState()
                                                val id = googleAuthUiClient.getSignedInUser()?.userId.toString()
                                                //userViewModel.checkUserInFirestore(id, googleAuthUiClient)
                                            }
                                        }

                                        SignInScreen(
                                            state = signInState,
                                            onSignInClick = {
                                                lifecycleScope.launch {
                                                    val signInIntentSender =
                                                        googleAuthUiClient.signIn()
                                                    launcher.launch(
                                                        IntentSenderRequest.Builder(
                                                            signInIntentSender ?: return@launch
                                                        ).build()
                                                    )
                                                }
                                            },
                                            onAnonymousSignIn = {
                                                lifecycleScope.launch {
                                                    googleAuthUiClient.signInAnonymously()
                                                    Toast.makeText(
                                                        applicationContext,
                                                        "Signed in anonymously",
                                                        Toast.LENGTH_LONG
                                                    ).show()

                                                    signInViewModel.userIsAnonymous()

                                                    navController.navigate("todo_screen")
                                                }
                                            }
                                        )
                                    }

                                    composable(
                                        route = Screen.HelpScreen.route
                                    ) {
                                        HelpScreen(
                                            scope,
                                            drawerState
                                        )
                                    }

                                    composable(
                                        route = Screen.PomodoroTimerScreen.route
                                    ) {

                                        PomodoroTimerScreen(
                                            settingsViewModel = settingsViewModel, // Pass the instance here
                                            initialWorkTime = 1500L * 1000L,
                                            initialBreakTime = 300L * 1000L,
                                            handleColor = MaterialTheme.colorScheme.primary,
                                            inactiveBarColor = MaterialTheme.colorScheme.surface,
                                            activeBarColor = MaterialTheme.colorScheme.primary,
                                            context = applicationContext,
                                            activity = this@MainActivity,
                                            modifier = Modifier.size(300.dp),
                                            scope = scope,
                                            drawerState = drawerState
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            rewardViewModel.allRewards.observeAsState(listOf())
        }
    }

    // creates Arraylist of users from the Firestore database
    private fun getResponseUsingCallback() {
        leadViewModel.getResponseUsingCallback((object : FirebaseCallback {
            override fun onResponse(response: Response) {
                usersList(response)
            }
        }))
    }

}
