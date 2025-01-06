package com.helloyogesh.mycanvas

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import androidx.core.view.HapticFeedbackConstantsCompat

fun playSound(context: Context, sound: Int) {
    // view?.withNotNull {
    val mediaPlayer = MediaPlayer.create(context, sound)
    mediaPlayer.start()
    // }
}

fun triggerHapticFeedbackCompose(context: Context,
                                 view: View,
                                 hapticFeedbackConstants: Int = HapticFeedbackConstantsCompat.LONG_PRESS

) {
    if (isSamsungPhone()) {
        context.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                triggerHapticFeedback(it)
            }
        }
    }else{
        view.performHapticFeedback(
            hapticFeedbackConstants,
            HapticFeedbackConstantsCompat.FLAG_IGNORE_VIEW_SETTING
        )
    }
}

fun isSamsungPhone(): Boolean {
    return Build.BRAND.contains(other = "Samsung", ignoreCase = true)
}

@RequiresApi(Build.VERSION_CODES.O)
fun triggerHapticFeedback(context: Context) {
    val vibrator: Vibrator? = context.getSystemService(Vibrator::class.java)

    vibrator?.let {
        if (it.hasVibrator()) {
            val effect = VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
            it.vibrate(effect)
        }
    }

}

val Int.nonScaledSp
    @Composable
    get() = (this / LocalDensity.current.fontScale).sp
