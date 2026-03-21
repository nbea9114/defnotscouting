package com.team1108.a1108preseasontest.ui.screens

import android.content.Context
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ScoutingState(
    // Prematch
    val matchNumber: Int = 0,
    val scoutName: String = "",
    val teamNumber: Int = 0,
    val allianceColor: String? = null,
    val alliancePrediction: String? = null,
    val isScouting: Boolean = false,
    val showPrematchInfoMissingDialog: Boolean = false,

    // Match Scout
    val autoScore: Int = 0,
    val autoAccuracy: Int = -1,
    val teleopScore: Int = 0,
    val teleopAccuracy: Int = -1,
    val robotBroke: Boolean = false,
    val robotStuck: Boolean = false,
    val robotDefense: Boolean = false,
    val robotNoMove: Boolean = false,
    val robotAutoClimb: Boolean = false,
    val humanPlayerGreat: Boolean = false,
    val hanging: Int = 0,
    val showRockGif: Boolean = false,

    // Super Scout
    val superScoutNotes: List<Pair<String, String>> = List(3) { "" to "" },
    val superScoutRanks: Map<Int, Int> = emptyMap(), // Rank per team index
    val hubActive: Boolean = false,
    val superScoutTeamDidNotShow: List<Boolean> = List(3) { false },

    // Pit Scout
    val pitDriveTrain: String = "",
    val pitCarFav: String = "",
    val pitAutoClimb: Boolean = false,
    val pitClimbLevel: Int = 0,
    val hopperCount: String = "",
    val humanPlayerStation: Boolean = false,
    val groundIntake: Boolean = false,
    val canTrench: Boolean = false,
    val canBump: Boolean = false,
    val generalKindness: Float = 3f,
    val favoriteColor: String = "",
    val pitOtherNotes: String = "",

    // QR Code data
    val generatedQrBitmap: androidx.compose.ui.graphics.ImageBitmap? = null,
    val csvDataToSave: String? = null,
    val csvFileName: String? = null,
    val csvHeader: String? = null,
    val qrCodeTitle: String = ""
)

class ScoutingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ScoutingState())
    val uiState = _uiState.asStateFlow()

    // --- General Update Functions ---

    fun startScouting() {
        _uiState.update { it.copy(isScouting = true) }
    }

    fun updateMatchNumber(number: String) {
        _uiState.update { it.copy(matchNumber = number.toIntOrNull() ?: 0) }
    }

    fun updateScoutName(name: String) {
        _uiState.update { it.copy(scoutName = name) }
    }

    fun updateTeamNumber(number: String) {
        _uiState.update { it.copy(teamNumber = number.toIntOrNull() ?: 0) }
    }

    fun selectAlliance(color: String?) {
        _uiState.update { it.copy(allianceColor = color) }
    }

    fun updatePrediction(prediction: String?) {
        if (!uiState.value.isScouting) {
            _uiState.update { it.copy(alliancePrediction = prediction) }
        }
    }

    fun dismissPrematchInfoMissingDialog() {
        _uiState.update { it.copy(showPrematchInfoMissingDialog = false) }
    }

    fun dismissQrCode() {
        _uiState.update { it.copy(generatedQrBitmap = null, qrCodeTitle = "") }
    }

    // --- Match Scout Update Functions ---
    fun updateAutoScore(delta: Int) {
        _uiState.update { currentState ->
            val newScore = (currentState.autoScore + delta).coerceAtLeast(0)
            if (newScore >= 1000 && currentState.autoScore < 1000) {
                showAndHideRockGif()
            }
            currentState.copy(autoScore = newScore)
        }
    }

    fun updateTeleopScore(delta: Int) {
        _uiState.update { currentState ->
            val newScore = (currentState.teleopScore + delta).coerceAtLeast(0)
            if (newScore >= 1000 && currentState.teleopScore < 1000) {
                showAndHideRockGif()
            }
            currentState.copy(teleopScore = newScore)
        }
    }

    private fun showAndHideRockGif() {
        viewModelScope.launch {
            _uiState.update { it.copy(showRockGif = true) }
            delay(5000)
            _uiState.update { it.copy(showRockGif = false) }
        }
    }

    fun updateAutoAccuracy(accuracy: Int) {
        _uiState.update { it.copy(autoAccuracy = accuracy) }
    }

    fun updateTeleopAccuracy(accuracy: Int) {
        _uiState.update { it.copy(teleopAccuracy = accuracy) }
    }

    fun updateRobotBroke(value: Boolean) {
        _uiState.update { it.copy(robotBroke = value) }
    }

    fun updateRobotStuck(value: Boolean) {
        _uiState.update { it.copy(robotStuck = value) }
    }

    fun updateRobotDefense(value: Boolean) {
        _uiState.update { it.copy(robotDefense = value) }
    }

    fun updateRobotNoMove(value: Boolean) {
        _uiState.update { it.copy(robotNoMove = value) }
    }

    fun updateRobotAutoClimb(value: Boolean) {
        _uiState.update { it.copy(robotAutoClimb = value) }
    }

    fun updateHumanPlayerGreat(value: Boolean) {
        _uiState.update { it.copy(humanPlayerGreat = value) }
    }

    fun updateHanging(value: Int) {
        _uiState.update { it.copy(hanging = value) }
    }

    // --- Pit Scout Update Functions ---
    fun updatePitDriveTrain(driveTrain: String) {
        _uiState.update { it.copy(pitDriveTrain = driveTrain) }
    }

    fun updatePitCarFav(driveTrain: String) {
        _uiState.update { it.copy(pitDriveTrain = driveTrain) }
    }

    fun updatePitAutoClimb(value: Boolean) {
        _uiState.update { it.copy(pitAutoClimb = value) }
    }

    fun updatePitClimbLevel(value: Int) {
        _uiState.update { it.copy(pitClimbLevel = value) }
    }

    fun updateHopperCount(count: String) {
        _uiState.update { it.copy(hopperCount = count) }
    }

    fun updateHumanPlayerStation(value: Boolean) {
        _uiState.update { it.copy(humanPlayerStation = value) }
    }

    fun updateGroundIntake(value: Boolean) {
        _uiState.update { it.copy(groundIntake = value) }
    }

    fun updateCanTrench(value: Boolean) {
        _uiState.update { it.copy(canTrench = value) }
    }

    fun updateCanBump(value: Boolean) {
        _uiState.update { it.copy(canBump = value) }
    }

    fun updateGeneralKindness(value: Float) {
        _uiState.update { it.copy(generalKindness = value) }
    }

    fun updateFavoriteColor(color: String) {
        _uiState.update { it.copy(favoriteColor = color) }
    }

    fun updatePitOtherNotes(notes: String) {
        _uiState.update { it.copy(pitOtherNotes = notes) }
    }

    // --- Super Scout Update Functions ---

    fun updateSuperScoutTeam(index: Int, teamNumber: String) {
        _uiState.update { currentState ->
            val updatedNotes = currentState.superScoutNotes.toMutableList()
            if (index in 0..updatedNotes.lastIndex) {
                updatedNotes[index] = updatedNotes[index].copy(first = teamNumber)
                currentState.copy(superScoutNotes = updatedNotes)
            } else {
                currentState
            }
        }
    }

    fun updateSuperScoutNote(index: Int, note: String) {
        _uiState.update { currentState ->
            val updatedNotes = currentState.superScoutNotes.toMutableList()
            if (index in 0..updatedNotes.lastIndex) {
                updatedNotes[index] = updatedNotes[index].copy(second = note)
                currentState.copy(superScoutNotes = updatedNotes)
            } else {
                currentState
            }
        }
    }

    fun updateSuperScoutRank(teamIndex: Int, rank: Int) {
        _uiState.update { currentState ->
            val currentRanks = currentState.superScoutRanks.toMutableMap()

            if (currentRanks[teamIndex] == rank) {
                currentRanks.remove(teamIndex)
            } else {
                val otherTeamWithRank = currentRanks.filterValues { it == rank }.keys.firstOrNull()
                if (otherTeamWithRank != null) {
                    currentRanks.remove(otherTeamWithRank)
                }
                currentRanks[teamIndex] = rank
            }
            currentState.copy(superScoutRanks = currentRanks)
        }
    }

    fun updateHubActive(isActive: Boolean) {
        _uiState.update { it.copy(hubActive = isActive) }
    }

    fun updateSuperScoutTeamDidNotShow(index: Int, didNotShow: Boolean) {
        _uiState.update { currentState ->
            val updatedDidNotShow = currentState.superScoutTeamDidNotShow.toMutableList()
            if (index in 0..updatedDidNotShow.lastIndex) {
                updatedDidNotShow[index] = didNotShow
                // Also clear the team number if the box is checked
                if (didNotShow) {
                    val updatedNotes = currentState.superScoutNotes.toMutableList()
                    updatedNotes[index] = updatedNotes[index].copy(first = "")
                    currentState.copy(
                        superScoutTeamDidNotShow = updatedDidNotShow,
                        superScoutNotes = updatedNotes
                    )
                } else {
                    currentState.copy(superScoutTeamDidNotShow = updatedDidNotShow)
                }
            } else {
                currentState
            }
        }
    }

    // --- File & Submission Logic ---

    private fun isMatchScoutPrematchInfoMissing(): Boolean {
        val state = uiState.value
        return state.scoutName.isBlank() ||
                state.matchNumber == 0 ||
                state.teamNumber == 0 ||
                state.allianceColor == null
    }

    private fun isSuperScoutPrematchInfoMissing(): Boolean {
        val state = uiState.value
        val requiredTeams = state.superScoutNotes.filterIndexed { index, _ -> !state.superScoutTeamDidNotShow[index] }
        return state.scoutName.isBlank() ||
                state.matchNumber == 0 ||
                state.allianceColor == null ||
                requiredTeams.any { it.first.isBlank() }
    }

    private fun getCurrentTimestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun saveCsvData(context: Context, fileName: String, data: String, header: String? = null) {
        try {
            val directory = context.getExternalFilesDir(null)
            if (directory != null) {
                val file = File(directory, fileName)
                val writeHeader = header != null && !file.exists()

                FileWriter(file, true).use { writer ->
                    if (writeHeader) {
                        writer.append(header)
                        writer.append("\n")
                    }
                    writer.append(data)
                    writer.append("\n")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun generateQrBitmap(data: String): androidx.compose.ui.graphics.ImageBitmap? {
        return try {
            val bitMatrix = MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, 400, 400)
            BarcodeEncoder().createBitmap(bitMatrix).asImageBitmap()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun generateMatchScoutCsv(state: ScoutingState): String {
         return listOf(
            getCurrentTimestamp(),
            state.scoutName,
            state.matchNumber,
            state.teamNumber,
            state.allianceColor ?: "",
            state.alliancePrediction ?: "",
            state.autoScore,
            state.autoAccuracy,
            state.robotAutoClimb,
            state.robotNoMove,
            state.teleopScore,
            state.teleopAccuracy,
            state.robotDefense,
            state.robotBroke,
            state.robotStuck,
            state.humanPlayerGreat,
            state.hanging == 1,
            state.hanging == 2,
            state.hanging == 3
        ).joinToString(",")
    }

    private fun generateSuperScoutCsv(state: ScoutingState): String {
        val (team1, note1, rank1) = if (state.superScoutTeamDidNotShow[0]) {
            Triple("", "", "")
        } else {
            Triple(
                state.superScoutNotes.getOrNull(0)?.first ?: "",
                (state.superScoutNotes.getOrNull(0)?.second ?: "").replace(",", ";").replace("\n", " "),
                state.superScoutRanks.get(0)?.toString() ?: ""
            )
        }

        val (team2, note2, rank2) = if (state.superScoutTeamDidNotShow[1]) {
            Triple("", "", "")
        } else {
            Triple(
                state.superScoutNotes.getOrNull(1)?.first ?: "",
                (state.superScoutNotes.getOrNull(1)?.second ?: "").replace(",", ";").replace("\n", " "),
                state.superScoutRanks.get(1)?.toString() ?: ""
            )
        }

        val (team3, note3, rank3) = if (state.superScoutTeamDidNotShow[2]) {
            Triple("", "", "")
        } else {
            Triple(
                state.superScoutNotes.getOrNull(2)?.first ?: "",
                (state.superScoutNotes.getOrNull(2)?.second ?: "").replace(",", ";").replace("\n", " "),
                state.superScoutRanks.get(2)?.toString() ?: ""
            )
        }

        return listOf(
            getCurrentTimestamp(),
            state.scoutName,
            state.allianceColor ?: "",
            state.matchNumber,
            team1, note1, rank1,
            state.scoutName, state.matchNumber, team2, note2, rank2,
            state.scoutName, state.matchNumber, team3, note3, rank3,
            state.scoutName, state.hubActive
        ).joinToString(",")
    }

    private fun generatePitScoutCsv(state: ScoutingState): String {
        return listOf(
            getCurrentTimestamp(),
            state.scoutName,
            state.teamNumber,
            state.pitDriveTrain,
            state.pitAutoClimb,
            state.hopperCount,
            state.humanPlayerStation,
            state.groundIntake,
            state.canTrench,
            state.canBump,
            state.pitClimbLevel,
            state.generalKindness.toInt(),
            state.pitCarFav,
            state.pitOtherNotes.replace(",", ";").replace("\n", " ")
        ).joinToString(",")
    }

    fun createMatchScoutQrCode(context: Context) {
        if (isMatchScoutPrematchInfoMissing()) {
            _uiState.update { it.copy(showPrematchInfoMissingDialog = true) }
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val csvData = generateMatchScoutCsv(uiState.value)
            val header = "Timestamp,Scouter Name,Match Number,Team #,Alliance Color,Alliance Prediction,Auto Score,Auto Accuracy,Robot Auto Climb,Robot No Move,Teleop Score,Teleop Accuracy,Robot Defense,Robot Broke,Robot Stuck,Human Player Great,Hanging 1,Hanging 2,Hanging 3"
            val bitmap = generateQrBitmap(csvData)
            if (bitmap != null) {
                _uiState.update {
                    it.copy(
                        generatedQrBitmap = bitmap,
                        csvDataToSave = csvData,
                        csvFileName = "match_scout.csv",
                        csvHeader = header,
                        qrCodeTitle = "Match Scout"
                    )
                }
            }
        }
    }

    fun createSuperScoutQrCode(context: Context) {
        if (isSuperScoutPrematchInfoMissing()) {
            _uiState.update { it.copy(showPrematchInfoMissingDialog = true) }
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val csvData = generateSuperScoutCsv(uiState.value)
            val header = "Timestamp,Scouter Name,Alliance Color,Match Number,Team 1,Note 1,Rank 1,Scouter Name,Match Number,Team 2,Note 2,Rank 2,Scouter Name,Match Number,Team 3,Note 3,Rank 3,Scouter Name,Hub Active"
            val bitmap = generateQrBitmap(csvData)
            if (bitmap != null) {
                _uiState.update {
                    it.copy(
                        generatedQrBitmap = bitmap,
                        csvDataToSave = csvData,
                        csvFileName = "super_scout.csv",
                        csvHeader = header,
                        qrCodeTitle = "Super Scout"
                    )
                }
            }
        }
    }

    fun createPitScoutQrCode(context: Context) {
        if (uiState.value.teamNumber == 0 || uiState.value.scoutName.isBlank()) {
            _uiState.update { it.copy(showPrematchInfoMissingDialog = true) }
            return
        }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val csvData = generatePitScoutCsv(uiState.value)
                val header = "Timestamp,Scouter Name,Team #,Drive Train,Auto Climb,Climb Level,Hopper Count,Human Player Station,Ground Intake,Can Trench,Can cross bump,General Kindness,Favorite Car,Other Notes"
                saveCsvData(context, "pit_scout.csv", csvData, header)
            }
            resetScoutingState()
        }
    }


    fun savePendingCsvData(context: Context) {
        val state = uiState.value
        if (state.csvDataToSave != null && state.csvFileName != null) {
            saveCsvData(context, state.csvFileName, state.csvDataToSave, state.csvHeader)
        }
    }

    fun finalizeSubmission() {
        val currentState = uiState.value
        _uiState.value = ScoutingState(
            scoutName = currentState.scoutName,
            matchNumber = currentState.matchNumber + 1
        )
    }

    fun resetScoutingState() {
        val currentState = uiState.value
        _uiState.value = ScoutingState(
            scoutName = currentState.scoutName,
            matchNumber = currentState.matchNumber,
            allianceColor = currentState.allianceColor,
            teamNumber =  currentState.teamNumber
        )
    }
}
