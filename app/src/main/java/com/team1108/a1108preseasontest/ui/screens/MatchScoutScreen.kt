package com.team1108.a1108preseasontest.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.team1108.a1108preseasontest.R
private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
@Composable
fun MatchScoutScreen(
    scoutingViewModel: ScoutingViewModel,
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
    if (uiState.generatedQrBitmap != null) {
        QrCodeDialog(
            qrBitmap = uiState.generatedQrBitmap!!,
            title = uiState.qrCodeTitle,
            onDismiss = scoutingViewModel::dismissQrCode,
            onFinalize = {
                scoutingViewModel.savePendingCsvData(context)
                scoutingViewModel.finalizeSubmission()
                onScoutSubmit()
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        MatchScoutScreenContent(
            matchNumber = uiState.matchNumber,
            teamNumber = uiState.teamNumber,
            autoFuelScore = uiState.autoScore,
            teleFuelScore = uiState.teleopScore,
            autoAccuracy = uiState.autoAccuracy,
            teleAccuracy = uiState.teleopAccuracy,
            robotBroke = uiState.robotBroke,
            robotStuck = uiState.robotStuck,
            robotDefense = uiState.robotDefense,
            driverSkill = uiState.driverSkill,
            robotNoMove = uiState.robotNoMove,
            humanPlayerGreat = uiState.humanPlayerGreat,
            robotAutoClimb = uiState.robotAutoClimb,
            robotShuttle = uiState.robotShuttle,
            hanging = uiState.hanging,
            matchComments = uiState.matchComments,
            onAutoFuelChange = { scoutingViewModel.updateAutoScore(it) },
            onTeleFuelChange = { scoutingViewModel.updateTeleopScore(it) },
            onAutoAccuracyChange = { scoutingViewModel.updateAutoAccuracy(it) },
            onTeleAccuracyChange = { scoutingViewModel.updateTeleopAccuracy(it) },
            onRobotBrokeChange = { scoutingViewModel.updateRobotBroke(it) },
            onRobotStuckChange = { scoutingViewModel.updateRobotStuck(it) },
            onRobotDefenseChange = { scoutingViewModel.updateRobotDefense(it) },
            onDriverSkillChange = { scoutingViewModel.updateDriverSkill(it) },
            onRobotNoMoveChange = { scoutingViewModel.updateRobotNoMove(it) },
            onHumanPlayerGreatChange = { scoutingViewModel.updateHumanPlayerGreat(it) },
            onRobotAutoClimbChange = { scoutingViewModel.updateRobotAutoClimb(it) },
            onRobotShuttleChange = { scoutingViewModel.updateRobotShuttle(it) },
            onHangingChange = { scoutingViewModel.updateHanging(it) },
            onMatchCommentsChange = { scoutingViewModel.updateMatchComments(it) },
            onSubmitClick = {
                scoutingViewModel.createMatchScoutQrCode(context)
            }
        )

        if (uiState.showRockGif) {
            val imageLoader = ImageLoader.Builder(context)
                .components {
                    if (Build.VERSION.SDK_INT >= 28) {
                        add(ImageDecoderDecoder.Factory())
                    } else {
                        add(GifDecoder.Factory())
                    }
                }
                .build()
            Image(
                painter = rememberAsyncImagePainter(R.drawable.rock_one_eyebrow_raised_rock_staring, imageLoader),
                contentDescription = "The Rock GIF",
                modifier = Modifier.fillMaxSize()
            )
        }

    }
}

@Composable
fun MatchScoutScreenContent(
    matchNumber: Int,
    teamNumber: Int,
    autoFuelScore: Int,
    teleFuelScore: Int,
    autoAccuracy: Int,
    teleAccuracy: Int,
    robotBroke: Boolean,
    robotStuck: Boolean,
    robotDefense: Int,
    driverSkill: Int,
    robotNoMove: Boolean,
    humanPlayerGreat: Boolean,
    robotAutoClimb: Boolean,
    robotShuttle: Boolean,
    hanging: Int,
    matchComments: String,
    onAutoFuelChange: (Int) -> Unit,
    onTeleFuelChange: (Int) -> Unit,
    onAutoAccuracyChange: (Int) -> Unit,
    onTeleAccuracyChange: (Int) -> Unit,
    onRobotBrokeChange: (Boolean) -> Unit,
    onRobotStuckChange: (Boolean) -> Unit,
    onRobotDefenseChange: (Int) -> Unit,
    onDriverSkillChange: (Int) -> Unit,
    onRobotNoMoveChange: (Boolean) -> Unit,
    onHumanPlayerGreatChange: (Boolean) -> Unit,
    onRobotAutoClimbChange: (Boolean) -> Unit,
    onRobotShuttleChange: (Boolean) -> Unit,
    onHangingChange: (Int) -> Unit,
    onMatchCommentsChange: (String) -> Unit,
    onSubmitClick: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Match: $matchNumber",
                    fontSize = 22.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "MATCH SCOUTING",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Team: $teamNumber",
                    fontSize = 22.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            // AUTO COUNTER
            ValueScoringSection(
                label = "Auto Fuel Shot",
                count = autoFuelScore,
                onValueChange = onAutoFuelChange
            )
            AccuracySelection(
                label = "Auto Accuracy",
                selectedAccuracy = autoAccuracy,
                onAccuracySelected = onAutoAccuracyChange
            )

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Robot CLIMBED during auto", modifier = Modifier.weight(1f), fontSize = 18.sp)
                Switch(
                    checked = robotAutoClimb,
                    onCheckedChange = onRobotAutoClimbChange
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Robot DID NOT MOVE during Auto?", modifier = Modifier.weight(1f), fontSize = 18.sp)
                Switch(
                    checked = robotNoMove,
                    onCheckedChange = onRobotNoMoveChange
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // TELE COUNTER
            ValueScoringSection(
                label = "Teleop Fuel Shot",
                count = teleFuelScore,
                onValueChange = onTeleFuelChange
            )
            AccuracySelection(
                label = "Teleop Accuracy",
                selectedAccuracy = teleAccuracy,
                onAccuracySelected = onTeleAccuracyChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            HangingSelection(
                label = "Climbed",
                selectedValue = hanging,
                onValueSelected = onHangingChange
            )

            Spacer(modifier = Modifier.height(24.dp))

            RatingSelection(
                label = "Robot PLAYED Defence",
                selectedValue = robotDefense,
                onValueSelected = onRobotDefenseChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            RatingSelection(
                label = "Driver Skill",
                selectedValue = driverSkill,
                onValueSelected = onDriverSkillChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Robot SHUTTLED?", modifier = Modifier.weight(1f), fontSize = 18.sp)
                Switch(
                    checked = robotShuttle,
                    onCheckedChange = onRobotShuttleChange
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Robot Broke/Disabled?", modifier = Modifier.weight(1f), fontSize = 18.sp)
                Switch(
                    checked = robotBroke,
                    onCheckedChange = onRobotBrokeChange
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Robot Stuck for longer than 3s", modifier = Modifier.weight(1f), fontSize = 18.sp)
                Switch(
                    checked = robotStuck,
                    onCheckedChange = onRobotStuckChange
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Your HUMAN PLAYER was INCREDIBLE", modifier = Modifier.weight(1f), fontSize = 18.sp)
                Switch(
                    checked = humanPlayerGreat,
                    onCheckedChange = onHumanPlayerGreatChange
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Any other notes/comments", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = matchComments,
                onValueChange = onMatchCommentsChange,
                label = { Text("Comments or concerns") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(40.dp))

            // SUBMIT BUTTON
            Button(
                onClick = onSubmitClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("SUBMIT", color = Color.White)
            }
        }
    }
}

@Composable
fun RatingSelection(label: String, selectedValue: Int, onValueSelected: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 8.dp)) {
        Text(label, fontWeight = FontWeight.SemiBold)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Slider(
                value = selectedValue.toFloat(),
                onValueChange = { onValueSelected(it.toInt()) },
                valueRange = 0f..10.0f,
                steps = 9,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "$selectedValue/10",
                textAlign = TextAlign.Center,
                modifier = Modifier.width(60.dp)
            )
        }
    }
}

@Composable
fun AccuracySelection(label: String, selectedAccuracy: Int, onAccuracySelected: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 8.dp)) {
        Text(label, fontWeight = FontWeight.SemiBold)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Slider(
                value = selectedAccuracy.toFloat(),
                onValueChange = { onAccuracySelected(it.toInt()) },
                valueRange = 0f..100f,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "$selectedAccuracy%",
                textAlign = TextAlign.Center,
                modifier = Modifier.width(50.dp)
            )
        }
    }
}

@Composable
fun HangingSelection(label: String, selectedValue: Int, onValueSelected: (Int) -> Unit) {
    val hangingOptions = listOf(1, 2, 3)

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 8.dp)) {
        Text(label, fontWeight = FontWeight.SemiBold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            OutlinedButton(
                onClick = { onValueSelected(0) },
                modifier = Modifier.padding(horizontal = 4.dp),
                border = if (selectedValue == 0) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else ButtonDefaults.outlinedButtonBorder
            ) {
                Text("No")
            }
            hangingOptions.forEach { level ->
                OutlinedButton(
                    onClick = { onValueSelected(level) },
                    modifier = Modifier.padding(horizontal = 4.dp),
                    border = if (selectedValue == level) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else ButtonDefaults.outlinedButtonBorder
                ) {
                    Text("Level $level")
                }
            }
        }
    }
}

@Composable
fun ValueScoringSection(label: String, count: Int, onValueChange: (Int) -> Unit) {
    @Composable
    fun ScoreButton(value: Int, onClick: (Int) -> Unit) {
        Button(
            onClick = { onClick(value) },
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Text(text = if (value > 0) "+$value" else value.toString())
        }
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                ScoreButton(value = -25, onClick = onValueChange)
                ScoreButton(value = -5, onClick = onValueChange)
                ScoreButton(value = -1, onClick = onValueChange)

                Text("$count", fontSize = 36.sp, modifier = Modifier.padding(horizontal = 16.dp))

                ScoreButton(value = 1, onClick = onValueChange)
                ScoreButton(value = 5, onClick = onValueChange)
                ScoreButton(value = 25, onClick = onValueChange)
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=800dp,height=1280dp")
@Composable
fun MatchScoutPreview() {
    MatchScoutScreen(onScoutSubmit = {}, scoutingViewModel = ScoutingViewModel())
}
