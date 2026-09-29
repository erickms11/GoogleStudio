package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.AppDatabase
import com.example.data.EquilibriumRepository
import com.example.ui.screens.CheckInScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.EquilibriumViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = EquilibriumRepository(
            database.checkInDao(),
            database.monthlyGoalsDao(),
            database.pillarWeightsDao()
        )
        val viewModelFactory = EquilibriumViewModel.Factory(repository)

        setContent {
            MyApplicationTheme {
                val viewModel: EquilibriumViewModel = viewModel(factory = viewModelFactory)
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBackground
                ) {
                    EquilibriumNavHost(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun EquilibriumNavHost(viewModel: EquilibriumViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {
        composable("dashboard") {
            DashboardScreen(
                viewModel = viewModel,
                onNavigateToCheckIn = {
                    navController.navigate("checkin")
                },
                onNavigateToHistory = {
                    navController.navigate("history")
                },
                onNavigateToGoals = {
                    navController.navigate("goals")
                },
                onNavigateToWeights = {
                    navController.navigate("weights_calibration")
                },
                onNavigateToAvatar = {
                    navController.navigate("avatar")
                },
                onNavigateToPillarsCustomization = {
                    navController.navigate("pillars_customization")
                }
            )
        }

        composable("avatar") {
            com.example.ui.screens.AvatarScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToCheckIn = {
                    navController.navigate("checkin")
                }
            )
        }

        composable("pillars_customization") {
            com.example.ui.screens.PillarsCustomizationScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("weights_calibration") {
            com.example.ui.screens.WeightsCalibrationScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("goals") {
            com.example.ui.screens.MonthlyGoalsScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("checkin") {
            CheckInScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("history") {
            HistoryScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
