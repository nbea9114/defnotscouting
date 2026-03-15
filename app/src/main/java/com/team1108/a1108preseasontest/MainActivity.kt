package com.team1108.a1108preseasontest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.team1108.a1108preseasontest.ui.screens.FirstScreen
import com.team1108.a1108preseasontest.ui.screens.MatchScoutScreen
import com.team1108.a1108preseasontest.ui.screens.MatchScoutPrematchScreen
import com.team1108.a1108preseasontest.ui.screens.PitScoutScreen
import com.team1108.a1108preseasontest.ui.screens.SuperScoutPrematchScreen
import com.team1108.a1108preseasontest.ui.screens.ScoutingViewModel
import com.team1108.a1108preseasontest.ui.screens.SuperScoutScreen
import com.team1108.a1108preseasontest.ui.screens.SnakeGameScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScoutingAppHost()
        }
    }
}

@Composable
fun ScoutingAppHost() {
    val navController = rememberNavController()
    val scoutingViewModel: ScoutingViewModel = viewModel()

    NavHost(navController = navController, startDestination = "open_screen") {

        // 1. Open Screen (Main Menu - Starts here)
        composable("open_screen") {
            FirstScreen(
                onMatchScoutingClick = { navController.navigate("match_scout_prematch") },
                onSuperScoutingClick = { navController.navigate("super_scout_prematch") },
                onPitScoutingClick = { navController.navigate("pit_scout") },
                onSnakeGameClick = { navController.navigate("snake_game") }
            )
        }

        // 2a. Match Scout Pre-match Screen
        composable("match_scout_prematch") { 
            MatchScoutPrematchScreen(
                scoutingViewModel = scoutingViewModel,
                onStartClick = { navController.navigate("match_scout") },
                onBackClick = { navController.popBackStack() }
            )
        }

        // 2b. Super Scout Pre-match Screen
        composable("super_scout_prematch") {
            SuperScoutPrematchScreen(
                scoutingViewModel = scoutingViewModel,
                onStartClick = { navController.navigate("super_scout") },
                onBackClick = { navController.popBackStack() }
            )
        }

        // 2b. Super Scout Pre-match Screen
        composable("pit_scout") {
            PitScoutScreen(
                scoutingViewModel = scoutingViewModel,
                onScoutSubmit = { navController.popBackStack("open_screen", inclusive = false)}
            )
        }

        // 3. Match Scouting Screen
        composable("match_scout") {
            MatchScoutScreen(
                scoutingViewModel = scoutingViewModel,
                // On submit, go back to the main menu
                onScoutSubmit = { navController.popBackStack("open_screen", inclusive = false) }
            )
        }

        // 4. Super Scouting Screen
        composable("super_scout") {
            SuperScoutScreen(
                scoutingViewModel = scoutingViewModel,
                // On submit, go back to the main menu
                onScoutSubmit = { navController.popBackStack("open_screen", inclusive = false) }
            )
        }

        // 5. Snake Game Screen
        composable("snake_game") {
            SnakeGameScreen(navController = navController)
        }
    }
}
