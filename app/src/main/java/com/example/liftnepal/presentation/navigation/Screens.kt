package com.example.liftnepal.presentation.navigation


sealed class Screens(val route: String) {
    object SplashScreen : Screens("splash_screen")
    object LoginScreen : Screens("login_screen")
    object SignupScreen : Screens("signup_screen")
    object DashboardScreen : Screens("dashboard_screen")
}