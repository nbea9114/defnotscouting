package com.team1108.a1108preseasontest.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class OpenScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirstScreen(
                onMatchScoutingClick = {},
                onSuperScoutingClick = {},
                onPitScoutingClick = {},
                onSnakeGameClick = {}
            )
        }
    }
}
private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
@Composable
fun FirstScreen(
    onMatchScoutingClick: () -> Unit,
    onSuperScoutingClick: () -> Unit,
    onPitScoutingClick: () -> Unit,
    onSnakeGameClick: () -> Unit
) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val activity = context.findActivity()
        val originalOrientation = activity?.requestedOrientation
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
        onDispose {
            if (originalOrientation != null) {
                activity.requestedOrientation = originalOrientation
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                val configuration = LocalConfiguration.current
                val screenHeight = configuration.screenHeightDp.dp
                val buttonModifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(screenHeight * 0.15f)
                    .widthIn(max = 400.dp)
                    .heightIn(min = 60.dp, max = 200.dp)

                Image(
                    painter = painterResource(id = com.team1108.a1108preseasontest.R.drawable.panther_1108),
                    contentDescription = "Team Logo",
                    modifier = Modifier
                        .height(150.dp)
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("2026 Rebuilt Scouting", fontSize = 64.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(15.dp))

                Button(
                    onClick = onMatchScoutingClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = buttonModifier
                ) {
                    Text("Match Scouting", fontSize = 52.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = onSuperScoutingClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = buttonModifier
                ) {
                    Text("Super Scouting", fontSize = 52.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = onPitScoutingClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = buttonModifier
                ) {
                    Text("Pit Scouting", fontSize = 52.sp, color = Color.White)
                }
            }

            Button(
                onClick = onSnakeGameClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Text("Snake Game")
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp")
@Composable
fun ScoutPreview2() {
    FirstScreen(
        onMatchScoutingClick = {},
        onSuperScoutingClick = {},
        onPitScoutingClick = {},
        onSnakeGameClick = {}
    )
}
