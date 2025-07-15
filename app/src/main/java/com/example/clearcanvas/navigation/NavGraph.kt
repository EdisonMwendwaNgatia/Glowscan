package com.example.clearcanvas.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.clearcanvas.screens.*

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


}

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) { SplashScreen(navController) }
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.SignUp.route) { SignUpScreen(navController) }
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.Camera.route) { CameraScreen(navController) }
        composable(Screen.Analysis.route) { AnalysisScreen(navController) }
        composable(Screen.Result.route) { ResultScreen(navController) }
        composable(Screen.Profile.route) { ProfileScreen(navController) }
        composable(Screen.Journal.route) {
            JournalScreen(context = androidx.compose.ui.platform.LocalContext.current)
        }



    }
}
