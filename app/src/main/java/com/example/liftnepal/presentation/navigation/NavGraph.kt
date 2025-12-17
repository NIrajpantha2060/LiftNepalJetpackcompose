package com.example.liftnepal.presentation.navigation

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.liftnepal.presentation.auth.LoginScreen
import com.example.liftnepal.presentation.auth.SignupScreen
import com.example.liftnepal.presentation.dashboard.DashboardScreen
import com.example.liftnepal.presentation.splash.SplashScreen

@Composable
fun NavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screens.SplashScreen.route
    ) {
        composable(route = Screens.SplashScreen.route) {
            SplashScreen(navController = navController)
        }
        composable(route = Screens.LoginScreen.route) {
            LoginScreen(navController = navController)
        }
        composable(route = Screens.SignupScreen.route) {
            SignupScreen(navController = navController)
        }
        composable(route = Screens.DashboardScreen.route) {
            DashboardScreen(navController = navController)
        }
    }
}