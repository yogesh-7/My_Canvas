package com.helloyogesh.mycanvas

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.TimeZone

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                MainScreen() // Call your MainScreen composable here
            }
        }
    }
}

@SuppressLint("AutoboxingStateCreation")
@Composable
fun MainScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Mutable state to hold the current system time
        var isLightMode by remember { mutableStateOf(true) }

        // Get the current system time (in UTC)
        val currentMillis = remember { System.currentTimeMillis() }

        // Use Calendar and TimeZone to get IST time
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"))
        calendar.timeInMillis = currentMillis

        // Extract hours, minutes, seconds in IST
        var seconds by remember { mutableStateOf(calendar.get(Calendar.SECOND).toFloat()) }
        var minutes by remember { mutableStateOf(calendar.get(Calendar.MINUTE).toFloat()) }
        var hours by remember { mutableStateOf(calendar.get(Calendar.HOUR_OF_DAY).toFloat()) }

        // Continuously update the time every second
        LaunchedEffect(key1 = seconds) {
            while (true) {
                delay(1000L)

                // Update the time from the calendar
                calendar.timeInMillis = System.currentTimeMillis()

                // Update the time components
                seconds = calendar.get(Calendar.SECOND).toFloat()
                minutes = calendar.get(Calendar.MINUTE).toFloat()
                hours = calendar.get(Calendar.HOUR_OF_DAY).toFloat()

                // Convert to 12-hour format
                if (hours >= 12) {
                    hours -= 12
                }
                if (hours == 0f) {
                    hours = 12f // Correct the zero hour to display 12
                }
            }
        }

        // Log the updated IST time
        Log.d("MainActivity", "IST Time Seconds: $seconds, Minutes: $minutes, Hours: $hours")

        // Pass the dynamically calculated IST time to the Clock composable
        Clock(
            seconds = seconds,
            minutes = minutes,
            hours = hours,
            isLightMode = isLightMode
        )
        LightDarkSwitch(
            isLightMode = isLightMode,
            onModeChange = { isLightMode = it }
        )
        SquareClock(
            seconds = seconds,
            minutes = minutes,
            hours = hours,
            isLightMode = isLightMode
        )
    }
}





@Preview(showBackground = true)
@Composable
fun MyCanvasPreview() {
    MainScreen()
}