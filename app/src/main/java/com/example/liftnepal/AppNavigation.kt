package com.example.liftnepal

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.liftnepal.presentation.auth.ForgetPasswordScreen
import com.example.liftnepal.presentation.auth.LoginScreen
import com.example.liftnepal.presentation.auth.SignupScreen
import com.example.liftnepal.presentation.dashboard.AdminDashboard
import com.example.liftnepal.presentation.dashboard.DashboardScreen
import com.example.liftnepal.presentation.dashboard.RiderDashboard
import com.example.liftnepal.presentation.splash.SplashScreen
import com.example.liftnepal.presentation.viewmodel.AuthViewModel
import com.example.liftnepal.presentation.viewmodel.RideViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val rideViewModel: RideViewModel = viewModel()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash")           { SplashScreen(navController, authViewModel) }
        composable("login")            { LoginScreen(navController, authViewModel) }
        composable("signup")           { SignupScreen(navController, authViewModel) }
        composable("forget_password")  { ForgetPasswordScreen(navController, authViewModel) }

        composable("dashboard") {
            DashboardScreen(
                navController = navController,
                authViewModel = authViewModel,
                rideViewModel = rideViewModel
            )
        }

        composable("rider_dashboard") {
            RiderDashboard(
                navController = navController,
                authViewModel = authViewModel,
                rideViewModel = rideViewModel
            )
        }

        composable("admin_dashboard") {
            AdminDashboard(
                navController = navController,
                viewModel = authViewModel,
                rideViewModel = rideViewModel  // ✅ Fixed
            )
        }
    }
}