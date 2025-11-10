package com.example.clearcanvas.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.clearcanvas.screens.*
import com.example.clearcanvas.viewmodel.ImageViewModel

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Home : Screen("home")
    object Camera : Screen("camera")
    object Analysis : Screen("analysis")
    object Result : Screen("result")
    object Profile : Screen("profile")
    object Journal : Screen("journal")
    object Library : Screen("library")
    object Dermatologist : Screen("dermatologist")
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) { SplashScreen(navController) }
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.SignUp.route) { SignUpScreen(navController) }
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.Camera.route) { CameraScreen(navController) }
        composable(Screen.Analysis.route) {
            AnalysisScreen(navController = navController)
        }
        composable(
            route = "${Screen.Result.route}/{analysisResult}",
            arguments = listOf(
                navArgument("analysisResult") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val analysisResultJson = backStackEntry.arguments?.getString("analysisResult")
            ResultScreen(
                navController = navController,
                analysisData = analysisResultJson
            )
        }
        composable(Screen.Profile.route) { ProfileScreen(navController) }
        composable(Screen.Journal.route) {
            JournalScreen(context = androidx.compose.ui.platform.LocalContext.current)
        }
        composable(Screen.Library.route) { ProductLibraryScreen(navController) }
        composable(Screen.Dermatologist.route) { DermatologistsScreen(navController) }
    }
}