package com.team1108.a1108preseasontest.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
@Composable
fun PitScoutScreen(
    scoutingViewModel: ScoutingViewModel = viewModel(),
    onScoutSubmit: () -> Unit
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
    val scrollState = rememberScrollState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = uiState.scoutName,
                onValueChange = { scoutingViewModel.updateScoutName(it) },
                label = { Text("Scout Name") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = if (uiState.teamNumber == 0) "" else uiState.teamNumber.toString(),
                onValueChange = { scoutingViewModel.updateTeamNumber(it) },
                label = { Text("Team Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Drive Train", fontWeight = FontWeight.SemiBold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf("Swerve", "Tank", "Other").forEach { driveTrain ->
                val isSelected = uiState.pitDriveTrain == driveTrain
                OutlinedButton(
                    onClick = { scoutingViewModel.updatePitDriveTrain(driveTrain) },
                    border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else ButtonDefaults.outlinedButtonBorder
                ) {
                    Text(driveTrain)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Shooter Type", fontWeight = FontWeight.SemiBold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf("No Shooter", "Drum", "Single", "Double").forEach { shooterType ->
                val isSelected = uiState.pitShooterType == shooterType
                OutlinedButton(
                    onClick = { scoutingViewModel.updatePitShooterType(shooterType) },
                    border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else ButtonDefaults.outlinedButtonBorder
                ) {
                    Text(shooterType)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            // TURRET TOGGLE
            val hasTurret = uiState.pitHasTurret
            OutlinedButton(
                onClick = { scoutingViewModel.updatePitHasTurret(!uiState.pitHasTurret) },
                border = if (hasTurret) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else ButtonDefaults.outlinedButtonBorder,
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) {
                Text("Turret?")
            }

            // SHUTTLE TOGGLE
            val canShuttle = uiState.pitCanShuttle
            OutlinedButton(
                onClick = { scoutingViewModel.updatePitCanShuttle(!uiState.pitCanShuttle) },
                border = if (canShuttle) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else ButtonDefaults.outlinedButtonBorder,
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) {
                Text("Shuttle?")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // AUTO CLIMB TOGGLE
        val isAutoClimbSelected = uiState.pitAutoClimb
        OutlinedButton(
            onClick = { scoutingViewModel.updatePitAutoClimb(!uiState.pitAutoClimb) },
            border = if (isAutoClimbSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else ButtonDefaults.outlinedButtonBorder,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Auto Climb?")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.hopperCount,
            onValueChange = { scoutingViewModel.updateHopperCount(it) },
            label = { Text("Hopper capacity (estimate)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Intake From", fontWeight = FontWeight.SemiBold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = uiState.humanPlayerStation,
                        onCheckedChange = { scoutingViewModel.updateHumanPlayerStation(it) }
                    )
                    Text("Human Player Station")
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = uiState.groundIntake,
                        onCheckedChange = { scoutingViewModel.updateGroundIntake(it) }
                    )
                    Text("Ground")
                }
            }
        }

        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Traverse Through", fontWeight = FontWeight.SemiBold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = uiState.canTrench,
                        onCheckedChange = { scoutingViewModel.updateCanTrench(it) }
                    )
                    Text("Trench")
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = uiState.canBump,
                        onCheckedChange = { scoutingViewModel.updateCanBump(it) }
                    )
                    Text("Bump")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // CLIMB LEVEL SELECTION (Styled like Match Scouting's Hanging selection)
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Climb Level", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    OutlinedButton(
                        onClick = { scoutingViewModel.updatePitClimbLevel(0) },
                        modifier = Modifier.padding(horizontal = 4.dp),
                        border = if (uiState.pitClimbLevel == 0) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else ButtonDefaults.outlinedButtonBorder
                    ) {
                        Text("No")
                    }
                    listOf(1, 2, 3).forEach { level ->
                        OutlinedButton(
                            onClick = { scoutingViewModel.updatePitClimbLevel(level) },
                            modifier = Modifier.padding(horizontal = 4.dp),
                            border = if (uiState.pitClimbLevel == level) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else ButtonDefaults.outlinedButtonBorder
                        ) {
                            Text("L$level")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("General Kindness", fontWeight = FontWeight.SemiBold)
            Text(uiState.generalKindness.toInt().toString())
        }
        Slider(
            value = uiState.generalKindness,
            onValueChange = { scoutingViewModel.updateGeneralKindness(it) },
            valueRange = 1f..5f,
            steps = 3
        )

        Text("Ford v Ferrari", fontWeight = FontWeight.SemiBold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf("Ford", "Ferrari").forEach { carFav ->
                val isSelected = uiState.pitCarFav == carFav
                OutlinedButton(
                    onClick = { scoutingViewModel.updatePitCarFav(carFav) },
                    border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else ButtonDefaults.outlinedButtonBorder
                ) {
                    Text(carFav)
                }
            }
        }

        OutlinedTextField(
            value = uiState.pitOtherNotes,
            onValueChange = { scoutingViewModel.updatePitOtherNotes(it) },
            label = { Text("Other Notes") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                scoutingViewModel.createPitScoutQrCode(context)
                scoutingViewModel.resetScoutingState()
                onScoutSubmit()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit")
        }
    }
}

@Preview(showBackground = true, device = "spec:width=800dp,height=1280dp")
@Composable
fun PitScoutScreenPreview() {
    PitScoutScreen(onScoutSubmit = {})
}
