package com.team1108.a1108preseasontest.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
fun SuperScoutPrematchScreen(
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Super Scout Prematch", fontSize = 48.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Text("Your Robot's Alliance", fontSize = 36.sp, fontWeight = FontWeight.Bold)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
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

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = uiState.scoutName,
                onValueChange = { scoutingViewModel.updateScoutName(it) },
                label = { Text("Scout Name") },
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = if (uiState.matchNumber == 0) "" else uiState.matchNumber.toString(),
                onValueChange = { scoutingViewModel.updateMatchNumber(it) },
                label = { Text("Match Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)){
            uiState.superScoutNotes.forEachIndexed { index, pair ->
                Column(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = pair.first,
                        onValueChange = { scoutingViewModel.updateSuperScoutTeam(index, it) },
                        label = { Text("Team ${index + 1}") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.superScoutTeamDidNotShow[index]
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = uiState.superScoutTeamDidNotShow[index],
                            onCheckedChange = { scoutingViewModel.updateSuperScoutTeamDidNotShow(index, it) }
                        )
                        Text("Your team did not show")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Your Match Prediction", fontSize = 36.sp, fontWeight = FontWeight.Bold)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
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

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { 
                scoutingViewModel.startScouting()
                onStartClick()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start Scouting")
        }
    }
}

@Composable
fun ConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Navigation") },
        text = { Text("Are you sure you want to go back? All data for this match will be lost.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Yes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("No")
            }
        }
    )
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp")
@Composable
fun SuperScoutPrematchScreenPreview() {
    SuperScoutPrematchScreen(onStartClick = {}, onBackClick = {})
}
