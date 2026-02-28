//
//
//
//package com.example.liftnepal
//
//import androidx.compose.runtime.Composable
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import com.example.liftnepal.presentation.auth.ForgetPasswordScreen
//import com.example.liftnepal.presentation.auth.LoginScreen
//import com.example.liftnepal.presentation.auth.SignupScreen
//import com.example.liftnepal.presentation.dashboard.DashboardScreen
//import com.example.liftnepal.presentation.dashboard.RiderDashboard
//import com.example.liftnepal.presentation.splash.SplashScreen
//import com.example.liftnepal.presentation.viewmodel.AuthViewModel
//
//@Composable
//fun AppNavigation() {
//    val navController = rememberNavController()
//    val viewModel: AuthViewModel = viewModel()
//
//    NavHost(navController = navController, startDestination = "splash") {
//        composable("splash")          { SplashScreen(navController, viewModel) }
//        composable("login")           { LoginScreen(navController, viewModel) }
//        composable("signup")          { SignupScreen(navController, viewModel) }
//        composable("forget_password") { ForgetPasswordScreen(navController, viewModel) }
//        composable("dashboard")       { DashboardScreen(navController, viewModel) }
//        composable("rider_dashboard") { RiderDashboard(navController, viewModel) }  // ← NEW
//    }
//}

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

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: AuthViewModel = viewModel()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash")           { SplashScreen(navController, viewModel) }
        composable("login")            { LoginScreen(navController, viewModel) }
        composable("signup")           { SignupScreen(navController, viewModel) }
        composable("forget_password")  { ForgetPasswordScreen(navController, viewModel) }
        composable("dashboard")        { DashboardScreen(navController, viewModel) }
        composable("rider_dashboard")  { RiderDashboard(navController, viewModel) }
        composable("admin_dashboard")  { AdminDashboard(navController, viewModel) }  // ← NEW
    }
}