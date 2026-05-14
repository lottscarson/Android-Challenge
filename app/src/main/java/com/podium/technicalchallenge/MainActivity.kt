package com.podium.technicalchallenge

import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.podium.technicalchallenge.common.AppTheme
import com.podium.technicalchallenge.dashboard.DashboardDestination
import com.podium.technicalchallenge.detail.MovieDetailDestination
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "dashboard",
                    modifier = Modifier.safeDrawingPadding()
                ) {
                    composable("dashboard") {
                        DashboardDestination(
                            onMovieClick = { id -> navController.navigate("detail/$id") }
                        )
                    }
                    composable(
                        route = "detail/{movieId}",
                        arguments = listOf(navArgument("movieId") { type = NavType.IntType })
                    ) {
                        MovieDetailDestination(
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
