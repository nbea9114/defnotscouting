package com.team1108.a1108preseasontest.ui.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.team1108.a1108preseasontest.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AppState {
    var playerName by mutableStateOf("")
}

// --- Data Classes for Game State ---
data class SnakeGameState(
    val snake: List<Coordinate> = listOf(Coordinate(5, 5)),
    val foods: List<Coordinate> = listOf(Coordinate(10, 10)),
    val direction: SnakeDirection = SnakeDirection.RIGHT,
    val score: Int = 0,
    val isGameOver: Boolean = false,
    val showLebron: Boolean = false,
    val lebronCount: Int = 0,
    val backgroundImageIndex: Int = 0
)

data class Coordinate(val x: Int, val y: Int)

enum class SnakeDirection {
    UP, DOWN, LEFT, RIGHT
}

// --- ViewModel for Game Logic ---
class SnakeGameViewModel : ViewModel() {
    private val _gameState = MutableStateFlow(SnakeGameState())
    val gameState = _gameState.asStateFlow()

    private var lastMoveTime = 0L

    companion object {
        const val BOARD_SIZE = 15 // Increased board size for more food
        private const val SNAKE_MOVE_INTERVAL = 200L // Controls snake speed
        private const val GAME_LOOP_DELAY = 4L      // For responsive input
        val backgroundImages = listOf(
            R.drawable.mas,
            R.drawable.ethan,
            R.drawable.kj_pov,
            R.drawable.dispair,
            R.drawable.kj_aura,
            R.drawable.thuggin,
            R.drawable.barret05,
            R.drawable.james_yb,
            R.drawable.kj_weird,
            R.drawable.clipped_d,
            R.drawable.henry_odd,
            R.drawable.kj_geeked,
            R.drawable.kj_locked,
            R.drawable.plug_walk,
            R.drawable.bryce_face,
            R.drawable.flint_belt,
            R.drawable.henry_cody,
            R.drawable.mas_rocket,
            R.drawable.brig_locked,
            R.drawable.barret_phone,
            R.drawable.bryce_geeked,
            R.drawable.henry_locked,
            R.drawable.james_geeked,
            R.drawable.james_playin,
            R.drawable.starks_geeked,
            R.drawable.thankattention,
            R.drawable.dominic_snuggie,
            R.drawable.bryce_moregeeked,
            R.drawable.tuff_tbone,
            R.drawable.dallas_smile,
            R.drawable.kj_madgeeked,
            R.drawable.feefee,
            R.drawable.tom_schull,
            R.drawable.jeremy_locked,
            R.drawable.carson_geeked,
            R.drawable.dallas_sub,
            R.drawable.dallas_geeked,
            R.drawable.henry_insane,
            R.drawable.bryce_madgeeked,
            R.drawable.henry_madgeeked,
            R.drawable.olive,
            R.drawable.cody_smile,
            R.drawable.gang_wawa,
            R.drawable.carlee_geek,
            R.drawable.carlee_geeked,
            R.drawable.safe_buzz
        )
    }

    init {
        viewModelScope.launch {
            gameLoop()
        }
    }

    fun onDirectionChange(newDirection: SnakeDirection) {
        _gameState.update { currentState ->
            val currentVisualDirection = if (currentState.snake.size > 1) {
                val head = currentState.snake[0]
                val neck = currentState.snake[1]
                when {
                    head.x > neck.x -> SnakeDirection.RIGHT
                    head.x < neck.x -> SnakeDirection.LEFT
                    head.y > neck.y -> SnakeDirection.DOWN
                    head.y < neck.y -> SnakeDirection.UP
                    else -> currentState.direction
                }
            } else {
                currentState.direction
            }

            if (currentVisualDirection.isOpposite(newDirection)) {
                currentState
            } else {
                currentState.copy(direction = newDirection)
            }
        }
    }

    fun saveScore(playerName: String, context: Context) {
        val score = _gameState.value.score
        val lebronCount = _gameState.value.lebronCount
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val csvEntry = "$timestamp,$playerName,$score,$lebronCount\n"

        try {
            val file = File(context.getExternalFilesDir(null), "snake_scores.csv")
            FileOutputStream(file, true).use {
                it.write(csvEntry.toByteArray())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun resetGame() {
        _gameState.value = SnakeGameState(
            snake = listOf(Coordinate(5, 5)),
            foods = generateFoods(1, listOf(Coordinate(5, 5)), emptyList()),
            direction = SnakeDirection.RIGHT,
            score = 0,
            isGameOver = false,
            showLebron = false,
            lebronCount = 0
        )
    }

    private suspend fun gameLoop() {
        while (viewModelScope.isActive) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastMoveTime > SNAKE_MOVE_INTERVAL) {
                lastMoveTime = currentTime
                _gameState.update { currentState ->
                    if (currentState.isGameOver) return@update currentState

                    val head = currentState.snake.first()
                    val newHead = head.move(currentState.direction)

                    if (newHead.x !in 0 until BOARD_SIZE || newHead.y !in 0 until BOARD_SIZE || newHead in currentState.snake) {
                        return@update currentState.copy(isGameOver = true)
                    }

                    val newSnake = mutableListOf(newHead).apply { addAll(currentState.snake) }

                    val eatenFood = currentState.foods.find { it == newHead }

                    if (eatenFood != null) {
                        val newScore = currentState.score + 1
                        val newBackgroundImageIndex =
                            if (backgroundImages.size > 1) {
                                var nextIndex = currentState.backgroundImageIndex
                                while (nextIndex == currentState.backgroundImageIndex) {
                                    nextIndex = (0 until backgroundImages.size).random()
                                }
                                nextIndex
                            } else {
                                0
                            }
                        val showLebron = if (newScore >= 15) {
                            (0..99).random() < 10
                        } else {
                            false
                        }
                        val newLebronCount = if(showLebron) currentState.lebronCount + 1 else currentState.lebronCount

                        val foodCount = when {
                            newScore >= 100 -> 16
                            newScore >= 75 -> 8
                            newScore >= 50 -> 4
                            newScore >= 25 -> 2
                            else -> 1
                        }

                        val remainingFoods = currentState.foods.toMutableList()
                        remainingFoods.remove(eatenFood)

                        val newFoods = generateFoods(foodCount - remainingFoods.size, newSnake, remainingFoods)

                        currentState.copy(
                            snake = newSnake,
                            foods = remainingFoods + newFoods,
                            score = newScore,
                            showLebron = showLebron,
                            lebronCount = newLebronCount,
                            backgroundImageIndex = newBackgroundImageIndex
                        )
                    } else {
                        newSnake.removeAt(newSnake.size - 1)
                        currentState.copy(snake = newSnake)
                    }
                }
            }
            delay(GAME_LOOP_DELAY)
        }
    }

    private fun generateFoods(count: Int, snake: List<Coordinate>, existingFoods: List<Coordinate>): List<Coordinate> {
        val foods = mutableListOf<Coordinate>()
        repeat(count) {
            var foodPosition: Coordinate
            do {
                foodPosition = Coordinate(
                    x = (1 until BOARD_SIZE - 1).random(),
                    y = (1 until BOARD_SIZE - 1).random()
                )
            } while (foodPosition in snake || foodPosition in existingFoods || foodPosition in foods)
            foods.add(foodPosition)
        }
        return foods
    }

    private fun SnakeDirection.isOpposite(other: SnakeDirection): Boolean {
        return when (this) {
            SnakeDirection.UP -> other == SnakeDirection.DOWN
            SnakeDirection.DOWN -> other == SnakeDirection.UP
            SnakeDirection.LEFT -> other == SnakeDirection.RIGHT
            SnakeDirection.RIGHT -> other == SnakeDirection.LEFT
        }
    }

    private fun Coordinate.move(direction: SnakeDirection): Coordinate {
        return when (direction) {
            SnakeDirection.UP -> this.copy(y = this.y - 1)
            SnakeDirection.DOWN -> this.copy(y = this.y + 1)
            SnakeDirection.LEFT -> this.copy(x = this.x - 1)
            SnakeDirection.RIGHT -> this.copy(x = this.x + 1)
        }
    }
}

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun SnakeGameScreen(
    gameViewModel: SnakeGameViewModel = viewModel(),
    navController: NavController
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

    val gameState by gameViewModel.gameState.collectAsState()
    val focusRequester = remember { FocusRequester() }
    var showGameOverDialog by remember { mutableStateOf(false) }

    LaunchedEffect(gameState.isGameOver) {
        if (gameState.isGameOver) {
            delay(1000)
            showGameOverDialog = true
        } else {
            showGameOverDialog = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    val direction = when (event.key) {
                        Key.DirectionUp -> SnakeDirection.UP
                        Key.DirectionDown -> SnakeDirection.DOWN
                        Key.DirectionLeft -> SnakeDirection.LEFT
                        Key.DirectionRight -> SnakeDirection.RIGHT
                        else -> null
                    }
                    direction?.let {
                        gameViewModel.onDirectionChange(it)
                        return@onKeyEvent true
                    }
                }
                false
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        Text(
            text = "Score: ${gameState.score} | Lebron Count: ${gameState.lebronCount}",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        BoxWithConstraints {
            Box(
                modifier = Modifier
                    .size(maxWidth * 0.9f)
                    .border(2.dp, Color.Gray)
            ) {
                if (gameState.score >= 0 && !gameState.showLebron) {
                    AsyncImage(
                        model = SnakeGameViewModel.backgroundImages[gameState.backgroundImageIndex],
                        contentDescription = "background image",
                        modifier = Modifier
                            .size(this@BoxWithConstraints.maxWidth)
                            .align(Alignment.Center)
                    )
                }
                if (gameState.showLebron) {
                    AsyncImage(
                        model = R.drawable.lebron_thumb2,
                        contentDescription = "Lebron James giving a thumbs up",
                        modifier = Modifier
                            .size(this@BoxWithConstraints.maxWidth)
                            .align(Alignment.Center)
                    )
                }
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val tileSize = size.width / SnakeGameViewModel.BOARD_SIZE

                    // Draw foods
                    gameState.foods.forEach { food ->
                        drawRect(
                            color = Color.Red,
                            topLeft = Offset(x = food.x * tileSize, y = food.y * tileSize),
                            size = Size(tileSize, tileSize)
                        )
                    }

                    // Draw Snake
                    gameState.snake.forEachIndexed { index, coordinate ->
                        val offset = Offset(coordinate.x * tileSize, coordinate.y * tileSize)
                        drawRect(
                            color = Color.Green,
                            topLeft = offset,
                            size = Size(tileSize, tileSize)
                        )
                        if (index == 0) {
                            val eyeSize = tileSize / 5f
                            val eyeOffsetX = tileSize / 4f
                            val eyeOffsetY = tileSize / 6f

                            // Left Eye
                            drawRect(
                                color = Color.Black,
                                topLeft = offset + Offset(eyeOffsetX, eyeOffsetY),
                                size = Size(eyeSize, eyeSize)
                            )
                            // Right Eye
                            drawRect(
                                color = Color.Black,
                                topLeft = offset + Offset(tileSize - eyeOffsetX - eyeSize, eyeOffsetY),
                                size = Size(eyeSize, eyeSize)
                            )

                            // Smile
                            val smileWidth = tileSize / 2f
                            val smileHeight = tileSize / 8f
                            drawRect(
                                color = Color.Black,
                                topLeft = offset + Offset((tileSize - smileWidth) / 2f, tileSize * 5f / 8f),
                                size = Size(smileWidth, smileHeight)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (gameState.isGameOver) {
            var playerName by remember { mutableStateOf(AppState.playerName) }

            if (showGameOverDialog) {
                val score = gameState.score
                val lebronCount = gameState.lebronCount

                AlertDialog(
                    onDismissRequest = {
                        showGameOverDialog = false
                        gameViewModel.resetGame()
                    },
                    title = { Text("Game Over!") },
                    text = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Your score: $score | Lebron Count: $lebronCount")
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = playerName,
                                onValueChange = {
                                    playerName = it
                                    AppState.playerName = it
                                },
                                label = { Text("Enter your name") },
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row {
                                Button(
                                    onClick = {
                                        if (playerName.isNotBlank()) {
                                            gameViewModel.saveScore(playerName, context)
                                        }
                                        showGameOverDialog = false
                                        navController.navigate("match_scout_prematch")
                                    }
                                ) {
                                    Text("Save and Scout")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (playerName.isNotBlank()) {
                                            gameViewModel.saveScore(playerName, context)
                                        }
                                        showGameOverDialog = false
                                        navController.navigate("super_scout_prematch")
                                    }
                                ) {
                                    Text("Save and Super")
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (playerName.isNotBlank()) {
                                    gameViewModel.saveScore(playerName, context)
                                }
                                showGameOverDialog = false
                                gameViewModel.resetGame()
                            }
                        ) {
                            Text("Save & Play Again")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                showGameOverDialog = false
                                gameViewModel.resetGame()
                            }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }
        } else {
            // Game Controls
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { gameViewModel.onDirectionChange(SnakeDirection.LEFT) },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                        shape = RectangleShape
                    ) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Left", modifier = Modifier.fillMaxSize())
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { gameViewModel.onDirectionChange(SnakeDirection.UP) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f),
                            shape = RectangleShape
                        ) {
                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Up", modifier = Modifier.fillMaxSize())
                        }
                        Button(
                            onClick = { gameViewModel.onDirectionChange(SnakeDirection.DOWN) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f),
                            shape = RectangleShape
                        ) {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Down", modifier = Modifier.fillMaxSize())
                        }
                    }

                    Button(
                        onClick = { gameViewModel.onDirectionChange(SnakeDirection.RIGHT) },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                        shape = RectangleShape
                    ) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Right", modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=800dp,height=1280dp")
@Composable
fun SnakeGameScreenPreview() {
    SnakeGameScreen(navController = rememberNavController())
}
