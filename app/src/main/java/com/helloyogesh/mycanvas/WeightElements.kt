package com.helloyogesh.mycanvas

import android.graphics.Color
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.graphics.withRotation
import kotlin.math.*



@Composable
fun Scale(
    modifier: Modifier = Modifier,
    style: ScaleStyle = ScaleStyle(),
    minWeight: Int = 30,
    maxWeight: Int = 200,
    initialWeight: Int = 68,
    onWeightChange: (Int) -> Unit
) {
    // Radius of the circular scale
    val radius = style.radius
    val scaleWidth = style.scaleWidth

    // Remembered states for interactive properties
    var center by remember { mutableStateOf(Offset.Zero) }
    var circleCenter by remember { mutableStateOf(Offset.Zero) }
    var angle by remember { mutableFloatStateOf(0f) } // Tracks the rotation angle of the scale
    var dragStartedAngle by remember { mutableFloatStateOf(0f) } // Stores the angle when dragging starts
    var oldAngle by remember { mutableFloatStateOf(angle) } // Stores the previous angle after dragging ends
    var isDragging by remember { mutableStateOf(false) } // Tracks if the user is currently dragging

    val view = LocalView.current

    // Play sound when dragging starts
    LaunchedEffect(isDragging) {
        playSound(view.context, R.raw.tick)
    }

    Canvas(
        modifier = modifier
            .pointerInput(true) {
                detectDragGestures(
                    onDragStart = { offset ->
                        isDragging = true
                        // Calculate the starting angle relative to the circle center
                        dragStartedAngle = -atan2(
                            circleCenter.x - offset.x,
                            circleCenter.y - offset.y
                        ) * (180f / PI.toFloat())
                    },
                    onDragEnd = {
                        isDragging = false
                        oldAngle = angle // Update the stored angle after drag ends
                    }
                ) { change, _ ->
                    // Calculate the angle during dragging
                    val touchAngle = -atan2(
                        circleCenter.x - change.position.x,
                        circleCenter.y - change.position.y
                    ) * (180f / PI.toFloat())

                    // Update the angle and clamp it within the weight range
                    val newAngle = oldAngle + (touchAngle - dragStartedAngle)
                    angle = newAngle.coerceIn(
                        minimumValue = initialWeight - maxWeight.toFloat(),
                        maximumValue = initialWeight - minWeight.toFloat()
                    )

                    // Notify parent composable about the weight change
                    onWeightChange((initialWeight - angle).roundToInt())
                }
            }
    ) {
        center = this.center // Set canvas center
        circleCenter = Offset(
            center.x,
            scaleWidth.toPx() / 2f + radius.toPx()
        )

        // Calculate outer and inner radii for the circular scale
        val outerRadius = radius.toPx() + scaleWidth.toPx() / 2f
        val innerRadius = radius.toPx() - scaleWidth.toPx() / 2f

        // Draw the outer circular scale with shadow effect
        drawContext.canvas.nativeCanvas.apply {
            drawCircle(
                circleCenter.x,
                circleCenter.y,
                radius.toPx(),
                Paint().apply {
                    strokeWidth = scaleWidth.toPx()
                    color = Color.WHITE
                    setStyle(Paint.Style.STROKE)
                    setShadowLayer(60f, 0f, 0f, Color.argb(50, 0, 0, 0))
                }
            )
        }

        // Draw the scale lines
        for (i in minWeight..maxWeight) {
            // Convert the weight value to an angle on the scale
            val angleInRad = (i - initialWeight + angle - 90) * ((PI / 180f).toFloat())

            // Determine the type of scale line (normal, 5-step, 10-step)
            val lineType = when {
                i % 10 == 0 -> LineType.TenStep
                i % 5 == 0 -> LineType.FiveStep
                else -> LineType.Normal
            }

            // Get line length and color based on the type
            val lineLength = when (lineType) {
                LineType.Normal -> style.normalLineLength.toPx()
                LineType.FiveStep -> style.fiveStepLineLength.toPx()
                LineType.TenStep -> style.tenStepLineLength.toPx()
            }
            val lineColor = when (lineType) {
                LineType.Normal -> style.normalLineColor
                LineType.FiveStep -> style.fiveStepLineColor
                LineType.TenStep -> style.tenStepLineColor
            }

            // Calculate line start and end points
            val lineStart = Offset(
                x = (outerRadius - lineLength) * cos(angleInRad) + circleCenter.x,
                y = (outerRadius - lineLength) * sin(angleInRad) + circleCenter.y
            )
            val lineEnd = Offset(
                x = outerRadius * cos(angleInRad) + circleCenter.x,
                y = outerRadius * sin(angleInRad) + circleCenter.y
            )

            // Draw the line
            drawLine(color = lineColor, start = lineStart, end = lineEnd, strokeWidth = 1.dp.toPx())

            // Draw numbers for 10-step lines
            if (lineType is LineType.TenStep) {
                val textRadius = outerRadius -
                        lineLength -
                        5.dp.toPx() -
                        style.textSize.toPx()
                val x = textRadius * cos(angleInRad) + circleCenter.x
                val y = textRadius * sin(angleInRad) + circleCenter.y
                drawContext.canvas.nativeCanvas.withRotation(
                    degrees = angleInRad * (180f / PI.toFloat()) + 90f,
                    pivotX = x,
                    pivotY = y
                ) {
                    drawText(
                        abs(i).toString(),
                        x,
                        y,
                        Paint().apply {
                            textSize = style.textSize.toPx()
                            textAlign = Paint.Align.CENTER
                        }
                    )
                }
            }
        }

        // Draw the scale indicator
        val middleTop = Offset(
            x = circleCenter.x,
            y = circleCenter.y - innerRadius - style.scaleIndicatorLength.toPx()
        )
        val bottomLeft = Offset(
            x = circleCenter.x - 4f,
            y = circleCenter.y - innerRadius
        )
        val bottomRight = Offset(
            x = circleCenter.x + 4f,
            y = circleCenter.y - innerRadius
        )
        val indicator = Path().apply {
            moveTo(middleTop.x, middleTop.y)
            lineTo(bottomLeft.x, bottomLeft.y)
            lineTo(bottomRight.x, bottomRight.y)
            lineTo(middleTop.x, middleTop.y)
        }
        drawPath(path = indicator, color = style.scaleIndicatorColor)
    }
}


@Composable
fun ScaleWithButtons(
    modifier: Modifier = Modifier,
    style: ScaleStyle = ScaleStyle(),
    minWeight: Int = 30,
    maxWeight: Int = 200,
    initialWeight: Int = 68,
    onWeightChange: (Int) -> Unit
) {
    val radius = style.radius
    val scaleWidth = style.scaleWidth

    var center by remember { mutableStateOf(Offset.Zero) }
    var circleCenter by remember { mutableStateOf(Offset.Zero) }
    var angle by remember { mutableFloatStateOf(0f) }
    var dragStartedAngle by remember { mutableFloatStateOf(0f) }
    var oldAngle by remember { mutableFloatStateOf(angle) }
    var isDragging by remember { mutableStateOf(false) }
    var currentWeight by remember { mutableIntStateOf(initialWeight) }

    val view = LocalView.current

    LaunchedEffect(isDragging) {
        if (isDragging) playSound(view.context, R.raw.tick)
    }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(600.dp)
                .align(Alignment.BottomCenter)
                .pointerInput(true) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            isDragging = true
                            dragStartedAngle = -atan2(
                                circleCenter.x - offset.x,
                                circleCenter.y - offset.y
                            ) * (180f / PI.toFloat())
                        },
                        onDragEnd = {
                            isDragging = false
                            oldAngle = angle
                        }
                    ) { change, _ ->
                        val touchAngle = -atan2(
                            circleCenter.x - change.position.x,
                            circleCenter.y - change.position.y
                        ) * (180f / PI.toFloat())

                        val newAngle = oldAngle + (touchAngle - dragStartedAngle)
                        angle = newAngle.coerceIn(
                            minimumValue = initialWeight - maxWeight.toFloat(),
                            maximumValue = initialWeight - minWeight.toFloat()
                        )
                        currentWeight = (initialWeight - angle).roundToInt()
                        onWeightChange(currentWeight)
                    }
                }
        ) {
            center = this.center
            circleCenter = Offset(
                center.x,
                center.y
            )

            val outerRadius = radius.toPx() + scaleWidth.toPx() / 2f
            val innerRadius = radius.toPx() - scaleWidth.toPx() / 2f

            drawContext.canvas.nativeCanvas.apply {
                drawCircle(
                    circleCenter.x,
                    circleCenter.y,
                    radius.toPx(),
                    Paint().apply {
                        strokeWidth = scaleWidth.toPx()
                        color = Color.WHITE
                        setStyle(Paint.Style.STROKE)
                        setShadowLayer(60f, 0f, 0f, Color.argb(50, 0, 0, 0))
                    }
                )
            }

            for (i in minWeight..maxWeight) {
                val angleInRad = (i - initialWeight + angle - 90) * ((PI / 180f).toFloat())
                val lineType = when {
                    i % 10 == 0 -> LineType.TenStep
                    i % 5 == 0 -> LineType.FiveStep
                    else -> LineType.Normal
                }

                val lineLength = when (lineType) {
                    LineType.Normal -> style.normalLineLength.toPx()
                    LineType.FiveStep -> style.fiveStepLineLength.toPx()
                    LineType.TenStep -> style.tenStepLineLength.toPx()
                }
                val lineColor = when (lineType) {
                    LineType.Normal -> style.normalLineColor
                    LineType.FiveStep -> style.fiveStepLineColor
                    LineType.TenStep -> style.tenStepLineColor
                }

                val lineStart = Offset(
                    x = (outerRadius - lineLength) * cos(angleInRad) + circleCenter.x,
                    y = (outerRadius - lineLength) * sin(angleInRad) + circleCenter.y
                )
                val lineEnd = Offset(
                    x = outerRadius * cos(angleInRad) + circleCenter.x,
                    y = outerRadius * sin(angleInRad) + circleCenter.y
                )

                drawLine(color = lineColor, start = lineStart, end = lineEnd, strokeWidth = 1.dp.toPx())

                if (lineType is LineType.TenStep) {
                    val textRadius = outerRadius - lineLength - 5.dp.toPx() - style.textSize.toPx()
                    val x = textRadius * cos(angleInRad) + circleCenter.x
                    val y = textRadius * sin(angleInRad) + circleCenter.y
                    drawContext.canvas.nativeCanvas.withRotation(
                        degrees = angleInRad * (180f / PI.toFloat()) + 90f,
                        pivotX = x,
                        pivotY = y
                    ) {
                        drawText(
                            abs(i).toString(),
                            x,
                            y,
                            Paint().apply {
                                textSize = style.textSize.toPx()
                                textAlign = Paint.Align.CENTER
                            }
                        )
                    }
                }
            }

            val middleTop = Offset(
                x = circleCenter.x,
                y = circleCenter.y - innerRadius - style.scaleIndicatorLength.toPx()
            )
            val bottomLeft = Offset(
                x = circleCenter.x - 4f,
                y = circleCenter.y - innerRadius
            )
            val bottomRight = Offset(
                x = circleCenter.x + 4f,
                y = circleCenter.y - innerRadius
            )
            val indicator = Path().apply {
                moveTo(middleTop.x, middleTop.y)
                lineTo(bottomLeft.x, bottomLeft.y)
                lineTo(bottomRight.x, bottomRight.y)
                lineTo(middleTop.x, middleTop.y)
            }
            drawPath(path = indicator, color = style.scaleIndicatorColor)
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    if (currentWeight > minWeight) {
                        currentWeight--
                        angle = (initialWeight - currentWeight).toFloat()
                        oldAngle = angle
                        onWeightChange(currentWeight)
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("-")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    if (currentWeight < maxWeight) {
                        currentWeight++
                        angle = (initialWeight - currentWeight).toFloat()
                        oldAngle = angle
                        onWeightChange(currentWeight)
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("+")
            }
        }
    }
}


