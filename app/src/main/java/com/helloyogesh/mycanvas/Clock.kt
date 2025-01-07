package com.helloyogesh.mycanvas

import android.graphics.Paint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Clock(
    seconds: Float = 0f,
    minutes: Float = 0f,
    hours: Float = 0f,
    radius: Dp = 100.dp,
    isLightMode: Boolean
) {
    val clockFaceColor = if (isLightMode) Color.White else Color(0xFF121212)
    val tickColor = if (isLightMode) Color.DarkGray else Color(0xFF555555)
    val majorTickColor = if (isLightMode) Color.Gray else Color(0xFF333333)
    val secondHandColor = if (isLightMode) Color.Red else Color(0xFFFF5555)
    val minuteHandColor = if (isLightMode) Color.Black else Color(0xFFAAAAAA)
    val hourHandColor = if (isLightMode) Color.Black else Color(0xFF888888)

    Canvas(modifier = Modifier.size(radius * 2f)) {
        // Draw clock face with shadow
        drawContext.canvas.nativeCanvas.apply {
            drawCircle(
                center.x,
                center.y,
                radius.toPx(),
                Paint().apply {
                    color = clockFaceColor.toArgb()
                    setShadowLayer(
                        /* radius = */ 50f,
                        /* dx = */ 0f,
                        /* dy = */ 0f,
                        /* shadowColor = */ android.graphics.Color.argb(50, 0, 0, 0)
                    )
                }
            )
        }

        // Draw clock ticks (lines)
        for (i in 0..59) {
            val angleInRad = i * (360f / 60f) * (PI.toFloat() / 180f)
            val isMajorTick = i % 5 == 0
            val lineLength = if (isMajorTick) radius.toPx() * 0.15f else radius.toPx() * 0.1f
            val strokeWidth = if (isMajorTick) 2.dp.toPx() else 1.dp.toPx()
            val lineColor = if (isMajorTick) majorTickColor else tickColor

            val lineStart = Offset(
                x = radius.toPx() * cos(angleInRad) + center.x,
                y = radius.toPx() * sin(angleInRad) + center.y
            )
            val lineEnd = Offset(
                x = (radius.toPx() - lineLength) * cos(angleInRad) + center.x,
                y = (radius.toPx() - lineLength) * sin(angleInRad) + center.y
            )
            drawLine(
                color = lineColor,
                start = lineStart,
                end = lineEnd,
                strokeWidth = strokeWidth
            )
        }

        // Draw seconds hand
        val secondsHandLength = radius.toPx() * 0.8f
        rotate(degrees = seconds * (360f / 60f), pivot = center) {
            drawLine(
                color = secondHandColor,
                start = center,
                end = Offset(
                    x = center.x,
                    y = center.y - secondsHandLength
                ),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        // Draw minutes hand
        val minutesHandLength = radius.toPx() * 0.7f
        rotate(degrees = minutes * (360f / 60f), pivot = center) {
            drawLine(
                color = minuteHandColor,
                start = center,
                end = Offset(
                    x = center.x,
                    y = center.y - minutesHandLength
                ),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        // Draw hours hand
        val hoursHandLength = radius.toPx() * 0.5f
        rotate(degrees = hours * (360f / 12f), pivot = center) {
            drawLine(
                color = hourHandColor,
                start = center,
                end = Offset(
                    x = center.x,
                    y = center.y - hoursHandLength
                ),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun SquareClock(
    seconds: Float = 0f,
    minutes: Float = 0f,
    hours: Float = 0f,
    size: Dp = 250.dp,
    isLightMode: Boolean = true
) {
    val clockFaceColor = if (isLightMode) Color.White else Color(0xFF121212)
    val borderColor = if (isLightMode) Color.Gray else Color(0xFFBBBBBB)
    val numberColor = if (isLightMode) Color.Black else Color.White
    val hourHandColor = if (isLightMode) Color.Black else Color(0xFFBBBBBB)
    val minuteHandColor =  if (isLightMode) Color.Black else Color(0xFFBBBBBB)
    val secondHandColor = Color.Red
    val textStyle = TextStyle(fontSize = 18.sp, color = numberColor, fontWeight = FontWeight.Bold)

    val density = LocalDensity.current

    // Get current time and date (compatible with older Android versions)
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
    val formattedDate = remember { dateFormat.format(calendar.time) }

    Box {
        Canvas(modifier = Modifier.size(size)) {
            val squareSide = with(density) { size.toPx() }

            // Draw border with rounded corners
            drawRoundRect(
                color = borderColor,
                size = Size(squareSide, squareSide),
                cornerRadius = CornerRadius(16.dp.toPx()),
                style = Stroke(width = 6.dp.toPx())
            )

            // Draw clock face background
            drawRoundRect(
                color = clockFaceColor,
                size = Size(squareSide - 12.dp.toPx(), squareSide - 12.dp.toPx()),
                cornerRadius = CornerRadius(16.dp.toPx()),
                topLeft = Offset(6.dp.toPx(), 6.dp.toPx())
            )
        }

        Box(
            modifier = Modifier.size(size),
            contentAlignment = Alignment.Center
        ) {
            // Draw clock numbers
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                (1..12).forEach { number ->
                    val angle = (number * 30 - 90) * (Math.PI / 180f).toFloat()
                    val x = with(density) { (cos(angle) * (size.toPx() / 2.5f)).toDp() }
                    val y = with(density) { (sin(angle) * (size.toPx() / 2.5f)).toDp() }

                    Text(
                        text = number.toString(),
                        style = textStyle,
                        modifier = Modifier
                            .offset(x, y)
                            .align(Alignment.Center)
                    )
                }
            }

            // Show time and date
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Time above center
                    Text(
                        text = "${hours.toInt()}:${minutes.toInt()}:${seconds.toInt()}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = numberColor,
                        modifier = Modifier.offset(y = (-20).dp)
                    )

                    // Spacer to create some gap between time and date
                    Spacer(modifier = Modifier.height(4.dp))

                    // Date below center
                    Text(
                        text = formattedDate,
                        fontSize = 12.sp,
                        color = numberColor,
                        modifier = Modifier.offset(y = 20.dp)
                    )
                }
            }

            // Clock Hands
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Second hand
                rotate(degrees = seconds * 6f, pivot = center) {
                    drawLine(
                        color = secondHandColor,
                        start = center,
                        end = Offset(center.x, center.y - (with(density) { size.toPx() / 3f })),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }

                // Minute hand
                rotate(degrees = minutes * 6f, pivot = center) {
                    drawLine(
                        color = minuteHandColor,
                        start = center,
                        end = Offset(center.x, center.y - (with(density) { size.toPx() / 3.5f })),
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }

                // Hour hand
                rotate(degrees = hours * 30f, pivot = center) {
                    drawLine(
                        color = hourHandColor,
                        start = center,
                        end = Offset(center.x, center.y - (with(density) { size.toPx() / 5f })),
                        strokeWidth = 4.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }

                // Center point
                drawCircle(
                    color = hourHandColor,
                    radius = 4.dp.toPx(),
                    center = center
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun SquareClockPreview() {
    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    // Continuously update the current time every second
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            currentTime = System.currentTimeMillis()
        }
    }

    // Calculate the IST offset in milliseconds
    val istOffsetMillis = 5 * 3600 * 1000L + 30 * 60 * 1000L
    val istTime = currentTime + istOffsetMillis

    // Calculate seconds, minutes, and hours in IST
    val seconds = (istTime / 1000f) % 60f
    val minutes = ((istTime / 1000f) / 60) % 60f
    val hours = ((istTime / 1000f) / 3600) % 12f // 12-hour format
    val displayHours = if (hours == 0f) 12f else hours // Handle 12-hour clock behavior

    SquareClock(
        seconds = seconds,
        minutes = minutes,
        hours = displayHours,
        isLightMode = false
    )
}


