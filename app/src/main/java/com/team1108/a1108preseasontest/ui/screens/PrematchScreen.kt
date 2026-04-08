package com.team1108.a1108preseasontest.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
@Composable
fun MatchScoutPrematchScreen(
    onStartClick: () -> Unit,
    onBackClick: () -> Unit,
    scoutingViewModel: ScoutingViewModel = viewModel()
) {
    val uiState by scoutingViewModel.uiState.collectAsState()
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
    var showConfirmationDialog by remember { mutableStateOf(false) }

    BackHandler {
        showConfirmationDialog = true
    }

    if (showConfirmationDialog) {
        ConfirmationDialog(
            onConfirm = {
                scoutingViewModel.resetScoutingState()
                onBackClick()
                showConfirmationDialog = false
            },
            onDismiss = { showConfirmationDialog = false }
        )
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Match Type Switch in top-left
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Quals",
                    fontSize = 18.sp,
                    color = if (!uiState.isPracticeOrPlayoff) Color.Unspecified else Color.Gray,
                    fontWeight = if (!uiState.isPracticeOrPlayoff) FontWeight.Bold else FontWeight.Normal
                )
                Switch(
                    checked = uiState.isPracticeOrPlayoff,
                    onCheckedChange = { scoutingViewModel.updatePracticeOrPlayoff(it) },
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Text(
                    text = "Practice or Playoffs",
                    fontSize = 18.sp,
                    color = if (uiState.isPracticeOrPlayoff) Color.Unspecified else Color.Gray,
                    fontWeight = if (uiState.isPracticeOrPlayoff) FontWeight.Bold else FontWeight.Normal
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text("MATCH SCOUTING", fontSize = 48.sp, fontWeight = FontWeight.Bold)

                Divider(modifier = Modifier.padding(vertical = 10.dp))

                Text("Your Robot's Alliance", fontSize = 36.sp, fontWeight = FontWeight.Bold)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // RED ALLIANCE BUTTON
                    Button(
                        onClick = { scoutingViewModel.selectAlliance("Red") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000)),
                        border = if (uiState.allianceColor == "Red") BorderStroke(4.dp, Color.Black) else null,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 100.dp, max = 150.dp)
                            .padding(8.dp)
                    ) {
                        Text("Red Alliance", fontSize = 30.sp, fontWeight = FontWeight.Bold)
                    }

                    // BLUE ALLIANCE BUTTON
                    Button(
                        onClick = { scoutingViewModel.selectAlliance("Blue") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                        border = if (uiState.allianceColor == "Blue") BorderStroke(4.dp, Color.Black) else null,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 100.dp, max = 150.dp)
                            .padding(8.dp)
                    ) {
                        Text("Blue Alliance", fontSize = 30.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 12.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = uiState.scoutName,
                        onValueChange = { scoutingViewModel.updateScoutName(it) },
                        label = { Text("YOUR Name") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        textStyle = TextStyle(fontSize = 48.sp),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 100.dp, max = 150.dp)
                            .padding(4.dp)
                    )
                    OutlinedTextField(
                        value = if (uiState.teamNumber == 0) "" else uiState.teamNumber.toString(),
                        onValueChange = { scoutingViewModel.updateTeamNumber(it) },
                        label = { Text("Team #") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = TextStyle(fontSize = 48.sp),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 100.dp, max = 150.dp)
                            .padding(4.dp)
                    )
                    OutlinedTextField(
                        value = if (uiState.matchNumber == 0) "" else uiState.matchNumber.toString(),
                        onValueChange = { scoutingViewModel.updateMatchNumber(it) },
                        label = { Text("Match #") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = TextStyle(fontSize = 48.sp),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 100.dp, max = 150.dp)
                            .padding(4.dp)
                    )

                }

                Divider(modifier = Modifier.padding(vertical = 12.dp))

                Text("Your Match Prediction", fontSize = 36.sp, fontWeight = FontWeight.Bold)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // RED PREDICTION BUTTON
                    Button(
                        onClick = { scoutingViewModel.updatePrediction("Red") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000)),
                        border = if (uiState.alliancePrediction == "Red") BorderStroke(4.dp, Color.Black) else null,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 100.dp, max = 150.dp)
                            .padding(8.dp),
                        enabled = !uiState.isScouting
                    ) {
                        Text(if (uiState.isScouting) "nice" else "Red Alliance", fontSize = 30.sp, fontWeight = FontWeight.Bold)
                    }

                    // BLUE PREDICTION BUTTON
                    Button(
                        onClick = { scoutingViewModel.updatePrediction("Blue") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                        border = if (uiState.alliancePrediction == "Blue") BorderStroke(4.dp, Color.Black) else null,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 100.dp, max = 150.dp)
                            .padding(8.dp),
                        enabled = !uiState.isScouting
                    ) {
                        Text(if (uiState.isScouting) "try" else "Blue Alliance", fontSize = 30.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = { 
                        scoutingViewModel.startScouting()
                        onStartClick() 
                    },
                    modifier = Modifier
                        .heightIn(min = 100.dp, max = 150.dp)
                        .widthIn(min = 600.dp, max = 1200.dp)
                        .padding(8.dp)
                ) {
                    Text("Start Match", fontSize = 30.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp")
@Composable
fun ScoutPreview3() {
    MatchScoutPrematchScreen(onStartClick = {}, onBackClick = {})
}
