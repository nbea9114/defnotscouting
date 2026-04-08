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
    val isPracticeOrPlayoff: Boolean = false,
    val showPrematchInfoMissingDialog: Boolean = false,

    // Match Scout
    val autoScore: Int = 0,
    val autoAccuracy: Int = -1,
    val teleopScore: Int = 0,
    val teleopAccuracy: Int = -1,
    val robotBroke: Boolean = false,
    val robotStuck: Boolean = false,
    val robotDefense: Int = 0,
    val driverSkill: Int = 5,
    val robotNoMove: Boolean = false,
    val robotAutoClimb: Boolean = false,
    val robotShuttle: Boolean = false,
    val humanPlayerGreat: Boolean = false,
    val hanging: Int = 0,
    val matchComments: String = "",
    val showRockGif: Boolean = false,

    // Super Scout
    val superScoutNotes: List<Pair<String, String>> = List(3) { "" to "" },
    val superScoutRanks: Map<Int, Int> = emptyMap(), // Rank per team index
    val hubActive: Boolean = false,
    val superScoutTeamDidNotShow: List<Boolean> = List(3) { false },

    // Pit Scout
    val pitDriveTrain: String = "",
    val pitShooterType: String = "",
    val pitTurretType: String = "",
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

    fun updatePracticeOrPlayoff(value: Boolean) {
        _uiState.update { it.copy(isPracticeOrPlayoff = value) }
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
            if (newScore >= 10000 && currentState.autoScore < 10000) {
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
            if (newScore >= 10000 && currentState.teleopScore < 10000) {
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

    fun updateRobotDefense(value: Int) {
        _uiState.update { it.copy(robotDefense = value) }
    }

    fun updateDriverSkill(value: Int) {
        _uiState.update { it.copy(driverSkill = value) }
    }

    fun updateRobotNoMove(value: Boolean) {
        _uiState.update { it.copy(robotNoMove = value) }
    }

    fun updateRobotAutoClimb(value: Boolean) {
        _uiState.update { it.copy(robotAutoClimb = value) }
    }

    fun updateRobotShuttle(value: Boolean) {
        _uiState.update { it.copy(robotShuttle = value) }
    }

    fun updateHumanPlayerGreat(value: Boolean) {
        _uiState.update { it.copy(humanPlayerGreat = value) }
    }

    fun updateHanging(value: Int) {
        _uiState.update { it.copy(hanging = value) }
    }

    fun updateMatchComments(comments: String) {
        _uiState.update { it.copy(matchComments = comments) }
    }

    // --- Pit Scout Update Functions ---
    fun updatePitDriveTrain(driveTrain: String) {
        _uiState.update { it.copy(pitDriveTrain = driveTrain) }
    }

    fun updatePitShooterType(shooterType: String) {
        _uiState.update { it.copy(pitShooterType = shooterType) }
    }

    fun updatePitTurretType(turretType: String) {
        _uiState.update { it.copy(pitTurretType = turretType) }
    }

    fun updatePitCarFav(carFav: String) {
        _uiState.update { it.copy(pitCarFav = carFav) }
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
                currentState.copy(superScoutTeamDidNotShow = updatedDidNotShow)
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
            state.driverSkill,
            state.robotShuttle,
            state.robotBroke,
            state.robotStuck,
            state.humanPlayerGreat,
            state.hanging == 1,
            state.hanging == 2,
            state.hanging == 3,
            state.matchComments.replace(",", ";").replace("\n", " ")
        ).joinToString(",")
    }

    private fun generateSuperScoutCsv(state: ScoutingState): String {
        val (team1, note1, rank1) = if (state.superScoutTeamDidNotShow[0]) {
            Triple("No Show", "", "")
        } else {
            Triple(
                state.superScoutNotes.getOrNull(0)?.first ?: "",
                (state.superScoutNotes.getOrNull(0)?.second ?: "").replace(",", ";").replace("\n", " "),
                state.superScoutRanks.get(0)?.toString() ?: ""
            )
        }

        val (team2, note2, rank2) = if (state.superScoutTeamDidNotShow[1]) {
            Triple("No Show", "", "")
        } else {
            Triple(
                state.superScoutNotes.getOrNull(1)?.first ?: "",
                (state.superScoutNotes.getOrNull(1)?.second ?: "").replace(",", ";").replace("\n", " "),
                state.superScoutRanks.get(1)?.toString() ?: ""
            )
        }

        val (team3, note3, rank3) = if (state.superScoutTeamDidNotShow[2]) {
            Triple("No Show", "", "")
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
            state.pitShooterType,
            state.pitTurretType,
            state.pitAutoClimb,
            state.pitClimbLevel,    // 5: Climb Level
            state.hopperCount,       // 6: Hopper Count
            state.humanPlayerStation,// 7: Human Player Station
            state.groundIntake,      // 8: Ground Intake
            state.canTrench,         // 9: Can Trench
            state.canBump,           // 10: Can cross bump
            state.generalKindness.toInt(),
            state.pitCarFav,
            state.pitOtherNotes.replace(",", ";").replace("\n", " ")
        ).joinToString(",")
    }

    fun createMatchScoutQrCode(context: Context) {
        val currentState = uiState.value
        if (isMatchScoutPrematchInfoMissing()) {
            _uiState.update { it.copy(showPrematchInfoMissingDialog = true) }
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val csvData = generateMatchScoutCsv(currentState)
            val header = "Timestamp,Scouter Name,Match Number,Team #,Alliance Color,Alliance Prediction,Auto Score,Auto Accuracy,Robot Auto Climb,Robot No Move,Teleop Score,Teleop Accuracy,Robot Defense,Driver Skill,Robot Shuttle,Robot Broke,Robot Stuck,Human Player Great,Hanging 1,Hanging 2,Hanging 3,Comments"
            val fileName = if (currentState.isPracticeOrPlayoff) "match_scout_practice_playoffs.csv" else "match_scout.csv"
            val bitmap = generateQrBitmap(csvData)
            if (bitmap != null) {
                _uiState.update {
                    it.copy(
                        generatedQrBitmap = bitmap,
                        csvDataToSave = csvData,
                        csvFileName = fileName,
                        csvHeader = header,
                        qrCodeTitle = "Match Scout"
                    )
                }
            }
        }
    }

    fun createSuperScoutQrCode(context: Context) {
        val currentState = uiState.value
        if (isSuperScoutPrematchInfoMissing()) {
            _uiState.update { it.copy(showPrematchInfoMissingDialog = true) }
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val csvData = generateSuperScoutCsv(currentState)
            val header = "Timestamp,Scouter Name,Alliance Color,Match Number,Team 1,Note 1,Rank 1,Scouter Name,Match Number,Team 2,Note 2,Rank 2,Scouter Name,Match Number,Team 3,Note 3,Rank 3,Scouter Name,Hub Active"
            val fileName = if (currentState.isPracticeOrPlayoff) "super_scout_practice_playoffs.csv" else "super_scout.csv"
            val bitmap = generateQrBitmap(csvData)
            if (bitmap != null) {
                _uiState.update {
                    it.copy(
                        generatedQrBitmap = bitmap,
                        csvDataToSave = csvData,
                        csvFileName = fileName,
                        csvHeader = header,
                        qrCodeTitle = "Super Scout"
                    )
                }
            }
        }
    }

    fun createPitScoutQrCode(context: Context) {
        val currentState = uiState.value
        if (currentState.teamNumber == 0 || currentState.scoutName.isBlank()) {
            _uiState.update { it.copy(showPrematchInfoMissingDialog = true) }
            return
        }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val csvData = generatePitScoutCsv(currentState)
                val header = "Timestamp,Scouter Name,Team #,Drive Train,Shooter Type,Turret Type,Auto Climb,Climb Level,Hopper Count,Human Player Station,Ground Intake,Can Trench,Can cross bump,General Kindness,Favorite Car,Other Notes"
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
            matchNumber = currentState.matchNumber + 1,
            isPracticeOrPlayoff = currentState.isPracticeOrPlayoff
        )
    }

    fun resetScoutingState() {
        val currentState = uiState.value
        _uiState.value = ScoutingState(
            scoutName = currentState.scoutName,
            matchNumber = currentState.matchNumber,
            allianceColor = currentState.allianceColor,
            isPracticeOrPlayoff = currentState.isPracticeOrPlayoff
        )
    }
}
