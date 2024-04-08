package com.csu_itc303_team1.adhdtaskmanager

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.DrawerValue
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.csu_itc303_team1.adhdtaskmanager.database.local.TodoDatabase
import com.csu_itc303_team1.adhdtaskmanager.ui.theme.ADHDTaskManagerTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory


@Suppress("UNCHECKED_CAST")
class MainActivity : ComponentActivity() {

    private val passPhrase = "passPhrase"
    private val factory = SupportFactory(SQLiteDatabase.getBytes(passPhrase.toCharArray()))
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            TodoDatabase::class.java,
            "todo.db"
        )/*.openHelperFactory(factory)*/.build()
    }

//    private val rewardDB by lazy {
//        Room.databaseBuilder(
//            applicationContext,
//            RewardDatabase::class.java, "reward_database.db"
//        ).createFromAsset("reward.db").build()}

    private val rewardViewModel by viewModels<RewardViewModel>(
        factoryProducer = {
            object: ViewModelProvider.Factory{
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return RewardViewModel(application) as T
                }
            }
        }
    )



    private val viewModel by viewModels<TodoViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TodoViewModel(db.todoDao) as T
                }
            }
        }
    )

    private lateinit var navController: NavHostController
    private lateinit var leadViewModel: LeaderboardViewModel
    //private lateinit var rewardViewModel: RewardViewModel

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @OptIn(ExperimentalPermissionsApi::class)
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Retrieve's Leaderboard data onCreate
            leadViewModel = ViewModelProvider(this)[LeaderboardViewModel::class.java]
            // Puts it into a readable format
            getResponseUsingCallback()
            //initialRewards(rewardViewModel)
            //rewardViewModel = ViewModelProvider(this) [RewardViewModel::class.java]


            ADHDTaskManagerTheme {

                // The Navigation Bar and Drawer will appear on the Main Activity (Every Screen)
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    // variables for remembering the state of the Coroutine Scope and Scaffold
                    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
                    val scope = rememberCoroutineScope()
                    navController = rememberNavController()

                    Scaffold(
                        modifier = Modifier
                            .padding(0.dp)
                            .fillMaxWidth()
                        ,
                        scaffoldState = scaffoldState,
                        // Creating the Top Bar
                        topBar = { AppTopAppBar(scope = scope, scaffoldState = scaffoldState) },
                        // Drawer content is what is inside the navigation drawer when clicking the
                        // menu icon. This case, A header and all the menu options in the drawer body
                        drawerContent = {
                            DrawerHeader()
                            DrawerBody(scope = scope, scaffoldState = scaffoldState, navController = navController)
                        }
                    ) { // In this Section is contents of the actual screen. A padding value had to
                        // be added in the lambda form.
                            contentPadding ->
                        Box(modifier = Modifier.padding(contentPadding))

                        val notificationPermission = rememberPermissionState(
                            android.Manifest.permission.POST_NOTIFICATIONS
                        )

                        if (!notificationPermission.hasPermission) {
                            PermissionDialog(
                                onEvent = viewModel::onEvent,
                                notificationPermission = notificationPermission
                            )
                        }

//                        val owner = LocalViewModelStoreOwner.current
//
//                        owner?.let{
//                            val rewardViewModel: RewardViewModel = viewModel(
//                                it,
//                                "RewardViewModel",
//                                RewardViewModelFactory(LocalContext.current.applicationContext as Application))
//                            RewardSetup(rewardViewModel)
//                            TodoRewardSetup(rewardViewModel)
//                        }


                        // The content itself is the navController's current state, or Home Screen
                        // on Default
                        val state by viewModel.state.collectAsState()
                        //val rewardState by rewardViewModel.collectAsState()

                        SetupNavGraph(
                            navController = navController,
                            state = state,
                            event = viewModel::onEvent,
                            rewardViewModel = rewardViewModel
                        )
                    }
                }
            }
        }
    }

    //Shows the notification
    // TODO - Make this show the notification when a task is due
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun showNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(this, "channel1")
            .setContentTitle("Task Reminder")
            .setContentText("You have a task due soon!")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
        notificationManager.notify(1, notification)
    }


    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    private fun PermissionDialog(
        modifier: Modifier = Modifier,
        notificationPermission: PermissionState,
        onEvent: (TodoEvent) -> Unit
    ){
        AlertDialog(
            modifier = modifier,
            onDismissRequest = {onEvent(TodoEvent.hideDialog)},
            title = {Text(text = "Notification Permission Required")},
            text = {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "This app requires notification permission to work")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        notificationPermission.launchPermissionRequest()
                    }
                ) {
                    Text(text = "Give Permission")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        onEvent(TodoEvent.hideDialog)
                    }
                ) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    // creates Arraylist of users from the firestore database
    private fun getResponseUsingCallback() {
        leadViewModel.getResponseUsingCallback((object : FirebaseCallback {
            override fun onResponse(response: Response) {
                usersList(response)
            }
        }))
    }

//    class RewardViewModelFactory(val application: Application):
//            ViewModelProvider.Factory{
//                override fun <T: ViewModel> create(modelClass: Class<T>): T {
//                    return RewardViewModel(application) as T
//                }
//            }

}