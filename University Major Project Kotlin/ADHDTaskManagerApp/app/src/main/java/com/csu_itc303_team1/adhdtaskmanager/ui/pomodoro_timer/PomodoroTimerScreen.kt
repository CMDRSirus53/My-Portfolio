package com.csu_itc303_team1.adhdtaskmanager.ui.pomodoro_timer

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.csu_itc303_team1.adhdtaskmanager.ui.settings_screen.SettingsViewModel
import com.csu_itc303_team1.adhdtaskmanager.ui.ui_components.MainTopAppBar
import com.csu_itc303_team1.adhdtaskmanager.utils.permissions.isDoNotDisturbEnabled
import com.csu_itc303_team1.adhdtaskmanager.utils.permissions.toggleDoNotDisturb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PomodoroTimerScreen(
    settingsViewModel: SettingsViewModel,
    initialWorkTime: Long,
    initialBreakTime: Long,
    handleColor: Color,
    inactiveBarColor: Color,
    activeBarColor: Color,
    modifier: Modifier = Modifier,
    initialValue: Float = 0f,
    strokeWidth: Dp = 5.dp,
    context: Context,
    activity: Activity,
    scope: CoroutineScope,
    drawerState: DrawerState
) {
    val workTime = settingsViewModel.workTimerValue.value?.let {
        it.toLong() * 60000 // Convert minutes to milliseconds
    } ?: initialWorkTime

    val breakTime = settingsViewModel.breakTimerValue.value?.let {
        it.toLong() * 60000 // Convert minutes to milliseconds
    } ?: initialBreakTime

    // Rest of your existing code remains the same...
    var size by remember { mutableStateOf(IntSize.Zero) }
    var progress by remember { mutableFloatStateOf(initialValue) }
    var currentTime by remember { mutableLongStateOf(0L) }
    var timerRoundsCount by remember { mutableIntStateOf(4) }
    var isTimerRunning by remember { mutableStateOf(false) }
    var isWorkTime by remember { mutableStateOf(true) }  // Set this to true
    var isBreakTime by remember { mutableStateOf(false) } // Set this to false
    var seconds by remember { mutableIntStateOf(0) }



    LaunchedEffect(key1 = isTimerRunning, key2 = currentTime) {
        if (currentTime > 0 && isTimerRunning) {
            delay(1000)
            currentTime -= 1000
            if (isWorkTime) {
                progress = currentTime.toFloat() / workTime.toFloat()
            } else if (isBreakTime) {
                progress = currentTime.toFloat() / breakTime.toFloat()
            }
            if (seconds == 0) {
                seconds = 59
            } else {
                seconds -= 1
            }
        } else if (currentTime == 0L && isTimerRunning) {
            if (isWorkTime) {
                timerRoundsCount -= 1
                isWorkTime = false
                isBreakTime = true
                currentTime = breakTime
            } else if (isBreakTime) {
                isWorkTime = true
                isBreakTime = false
                currentTime = workTime
            } else if (timerRoundsCount == 0) {
                isTimerRunning = false
                timerRoundsCount = 4
                currentTime = workTime
                isWorkTime = true
                isBreakTime = false
            }
        }
    }
    LaunchedEffect(key1 = isTimerRunning, key2 = isBreakTime) {
        if (isTimerRunning && isDoNotDisturbEnabled(context) && !isBreakTime) {
            toggleDoNotDisturb(context, activity)
        } else if (!isTimerRunning && !isDoNotDisturbEnabled(context) && isBreakTime) {
            toggleDoNotDisturb(context, activity)
        }
    }

    Scaffold(
        topBar = { MainTopAppBar(scope = scope, drawerState = drawerState)},
        content = {paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier.padding(top = 20.dp)
                ) {
                    Text(
                        text = "Pomodoro Timer",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.weight(1f))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = modifier.onSizeChanged { size = it },
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = modifier) {
                            drawArc(
                                color = inactiveBarColor,
                                startAngle = -215f,
                                sweepAngle = 250f,
                                useCenter = false,
                                size = Size(size.width.toFloat(), size.height.toFloat()),
                                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
                            )
                            drawArc(
                                color = if (isWorkTime) {
                                    activeBarColor
                                } else {
                                    Color.Green
                                },
                                startAngle = -215f,
                                sweepAngle = 250f * progress,
                                useCenter = false,
                                size = Size(size.width.toFloat(), size.height.toFloat()),
                                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
                            )

                            val center = Offset(size.width / 2f, size.height / 2f)
                            val beta = (250f * progress + 145f) * (Math.PI / 180f).toFloat()
                            val radius = size.width / 2f
                            val aSide = cos(beta) * radius
                            val bSide = sin(beta) * radius

                            drawPoints(
                                listOf(
                                    Offset(center.x + aSide, center.y + bSide)
                                ),
                                pointMode = PointMode.Points,
                                color = if (isWorkTime) {
                                    handleColor
                                } else {
                                    Color.Green
                                },
                                strokeWidth = (strokeWidth * 3f).toPx(),
                                cap = StrokeCap.Round
                            )
                        }
                        Text(
                            text = "%02d".format((currentTime / 1000L) / 60) + ":" + "%02d".format(
                                seconds
                            ),
                            fontSize = 44.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Button(
                        modifier = Modifier
                            .padding(top = 120.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = Color.White
                        ),
                        onClick = {
                            isTimerRunning = false
                            currentTime = workTime
                            isWorkTime = true
                            isBreakTime = false

                            timerRoundsCount = 4
                            seconds = 0
                            if (!isDoNotDisturbEnabled(context)) {
                                toggleDoNotDisturb(context, activity)
                            }
                        }) {
                        Text(
                            text = "Stop",
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Button(
                        onClick = {
                            if (currentTime <= 0L) {
                                currentTime = if (isWorkTime) {
                                    workTime
                                } else {
                                    breakTime
                                }
                                isTimerRunning = true

                            } else isTimerRunning = !isTimerRunning
                        },
                        modifier = Modifier
                            .padding(top = 120.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isTimerRunning || currentTime <= 0L) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.primaryContainer
                            },
                            contentColor = if (!isTimerRunning || currentTime <= 0L) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.background
                            }
                        )
                    ) {
                        Text(
                            text = if (!isTimerRunning && currentTime <= 0L) {
                                "Start"
                            } else if (isTimerRunning && currentTime >= 0L) {
                                "Pause"
                            } else {
                                "Resume"
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    )
}