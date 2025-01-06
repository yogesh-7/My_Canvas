package com.helloyogesh.mycanvas

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.random.nextLong

@Composable
fun RadarLoadingSpinner(
    radarColor: Color = Color(0xff4ef279),
    radarSize: Dp = 200.dp,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rotate")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8_000, easing = LinearEasing)
        ), label = ""
    )

    Box(
        Modifier
            .rotate(rotation)
            .size(radarSize),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .drawBehind {
                    drawArc(
                        brush = Brush.sweepGradient(
                            Pair(.7f, Color.Transparent),
                            Pair(1f, radarColor.copy(alpha = .5f)),
                        ),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = true,
                        alpha = .3f
                    )
                    drawArc(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                radarColor.copy(alpha = .6f),
                            )
                        ),
                        startAngle = 0f,
                        sweepAngle = -180f,
                        useCenter = false,
                        style = Stroke(
                            width = 2f,
                        )
                    )
                }
        )

        Box(
            Modifier
                .fillMaxWidth(.5f)
                .height(2.dp)
                .align(Alignment.CenterEnd)
                .background(
                    color = radarColor.copy(alpha = .8f),
                    shape = CircleShape
                )
        )
        Box(
            Modifier
                .size(10.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = CircleShape,
                )
                .background(
                    color = radarColor,
                    shape = CircleShape
                )
        )
    }

    Box(
        Modifier.size(radarSize)
    ) {
        for (i in 0..8) Blip(rotation, radarColor, radarSize)
    }
}

@Composable
private fun BoxScope.Blip(degrees: Float, radarColor: Color, radarSize: Dp) {

    val alpha = remember { Animatable(0f) }
    val blipScale = remember { Animatable(1f) }
    val ringScale = remember { Animatable(1f) }

    var intOffset by remember { mutableStateOf(IntOffset.Zero) }
    val degreesState by rememberUpdatedState(degrees)
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        while (true) {
            delay(Random.nextLong(100L..8_000L))
            val radius = with(density) { radarSize.roundToPx() / 2 }
            intOffset = convertDegreesToCartesian(
                degreesState,
                Random.nextInt((radius * .2f).toInt()..(radius * .8f).toInt())
            )
            scope.launch {
                blipScale.snapTo(.5f)
                blipScale.animateTo(
                    .8f + Random.nextFloat(),
                    spring(
                        dampingRatio = Spring.DampingRatioHighBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
            scope.launch {
                ringScale.snapTo(.2f)
                ringScale.animateTo(
                    targetValue = Random.nextInt(1..4).toFloat(),
                    animationSpec = tween(1000, easing = LinearEasing)
                )

            }
            alpha.snapTo(1f)
            alpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 500, delayMillis = 300)
            )

        }
    }

    Box(
        Modifier
            .offset { intOffset }
            .alpha(alpha.value)
            .align(Alignment.Center),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .size(20.dp * ringScale.value)
                .border(
                    Dp.Hairline,
                    color = radarColor.copy(alpha = .4f),
                    shape = CircleShape
                )
        )
        Box(
            Modifier
                .scale(blipScale.value)
                .size(5.dp)
                .background(
                    color = radarColor,
                    shape = CircleShape
                )
        )
    }

}

private fun degreesToRadians(degrees: Double) = degrees * PI / 180.0
private fun convertDegreesToCartesian(angle: Float, radius: Int): IntOffset {
    val angleRadians = degreesToRadians(angle.toDouble())
    val x = (radius * cos(angleRadians)).toInt()
    val y = (radius * sin(angleRadians)).toInt()
    return IntOffset(x, y)
}