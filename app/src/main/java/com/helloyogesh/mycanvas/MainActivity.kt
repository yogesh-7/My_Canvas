package com.helloyogesh.mycanvas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    var weight by remember {
        mutableIntStateOf(80) // Default weight value
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column {
            Spacer(modifier = Modifier.height(60.dp))

            // Label for the weight display
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = "Selected weight",
                maxLines = 1,
                style = TextStyle(
                    fontSize = 46.nonScaledSp,
                    fontWeight = FontWeight(400),
                    color = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(60.dp))

            // Displays the dynamic weight value prominently
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = "$weight Kg",
                maxLines = 1,
                style = TextStyle(
                    fontSize = 72.nonScaledSp,
                    fontWeight = FontWeight(700),
                    color = Color.Green
                )
            )
        }

        // Scale component to enable weight selection
        Scale(
            style = ScaleStyle(
                scaleWidth = 150.dp // Customize scale's width to fit the design
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .align(Alignment.BottomCenter)
        ) {
            weight = it // Updates the displayed weight when scale is adjusted
        }
    }
}



@Preview(showBackground = true)
@Composable
fun MyCanvasPreview() {
    MainScreen()
}