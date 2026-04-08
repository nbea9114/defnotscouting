package com.team1108.a1108preseasontest.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team1108.a1108preseasontest.R

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
@Composable
fun SuperScoutScreen(
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

    if (uiState.showPrematchInfoMissingDialog) {
        PrematchInfoMissingDialog(onDismiss = scoutingViewModel::dismissPrematchInfoMissingDialog)
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

    SuperScoutScreenContent(
        matchNumber = uiState.matchNumber,
        superScoutNotes = uiState.superScoutNotes,
        superScoutRanks = uiState.superScoutRanks,
        superScoutTeamDidNotShow = uiState.superScoutTeamDidNotShow,
        hubActive = uiState.hubActive,
        onNoteChange = scoutingViewModel::updateSuperScoutNote,
        onRankChange = scoutingViewModel::updateSuperScoutRank,
        onHubActiveChange = scoutingViewModel::updateHubActive,
        onSubmitClick = { scoutingViewModel.createSuperScoutQrCode(context) }
    )
}

@Composable
fun SuperScoutScreenContent(
    matchNumber: Int,
    superScoutNotes: List<Pair<String, String>>,
    superScoutRanks: Map<Int, Int>,
    superScoutTeamDidNotShow: List<Boolean>,
    hubActive: Boolean,
    onNoteChange: (Int, String) -> Unit,
    onRankChange: (Int, Int) -> Unit,
    onHubActiveChange: (Boolean) -> Unit,
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
            Text("SUPER SCOUTING", fontSize = 28.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(20.dp))

            Text("Match: $matchNumber", fontSize = 22.sp)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                superScoutNotes.forEachIndexed { index, pair ->
                    val isNoShow = superScoutTeamDidNotShow.getOrElse(index) { false }
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Team: ${pair.first}${if (isNoShow) " (No Show)" else ""}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isNoShow) Color.Gray else Color.Unspecified
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = pair.second,
                            onValueChange = { onNoteChange(index, it) },
                            label = { 
                                Text(
                                    when {
                                        isNoShow -> "Team marked as No Show"
                                        pair.first.isNotBlank() -> "Add notes..."
                                        else -> "Team number needed to add notes"
                                    }
                                ) 
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp),
                            enabled = pair.first.isNotBlank() && !isNoShow
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Rank",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isNoShow) Color.Gray else Color.Unspecified
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                        ) {
                            (1..3).forEach { rank ->
                                val isSelected = superScoutRanks[index] == rank
                                OutlinedButton(
                                    onClick = { onRankChange(index, rank) },
                                    border = if (isSelected) BorderStroke(4.dp, MaterialTheme.colorScheme.primary) else ButtonDefaults.outlinedButtonBorder,
                                    enabled = !isNoShow
                                ) {
                                    Text("$rank")
                                }
                            }
                        }

                    }
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text("Hub is active IMMEDIATELY AFTER AUTO", modifier = Modifier.weight(1f), fontSize = 18.sp)
                Switch(
                    checked = hubActive,
                    onCheckedChange = onHubActiveChange
                )
            }
            Spacer(modifier = Modifier.weight(1f))

            // SUBMIT BUTTON
            Button(
                onClick = onSubmitClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
            ) {
                Text("SUBMIT", color = Color.White, fontSize = 24.sp)
            }
        }
    }
}

@Composable
fun QrCodeDialog(qrBitmap: ImageBitmap, title: String, onDismiss: () -> Unit, onFinalize: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontSize = 48.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.kj_pov),
                    contentDescription = "KJ POV",
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(2f)
                ) {
                    Image(
                        bitmap = qrBitmap,
                        contentDescription = "QR Code",
                        modifier = Modifier.size(300.dp)
                    )
                    Text("Scan this code to submit your data.", textAlign = TextAlign.Center)
                }
                Image(
                    painter = painterResource(id = R.drawable.lebron_thumb),
                    contentDescription = "LeBron Thumb",
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onFinalize) { 
                Text("Done")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Dismiss")
            }
        }
    )
}

@Composable
fun PrematchInfoMissingDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Missing Information") },
        text = { Text("Please fill out all prematch information before submitting.") },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp")
@Composable
fun SuperScoutPreview() {
    SuperScoutScreen(scoutingViewModel = ScoutingViewModel(), onScoutSubmit = {})
}
