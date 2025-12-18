package com.example.liftnepal.presentation.navigation
//
//
//
//
//import androidx.compose.runtime.Composable
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import com.example.liftnepal.presentation.auth.ForgetPasswordScreen
//import com.example.liftnepal.presentation.auth.LoginScreen
//import com.example.liftnepal.presentation.auth.SignupScreen
//import com.example.liftnepal.presentation.dashboard.DashboardScreen
//import com.example.liftnepal.presentation.splash.SplashScreen
//
//@Composable
//fun NavGraph() {
//    val navController = rememberNavController()
//
//    NavHost(
//        navController = navController,
//        startDestination = Screens.Splash.route
//    ) {
//        composable(Screens.Splash.route) {
//            SplashScreen(navController = navController)
//        }
//
//        composable(Screens.Login.route) {
//            LoginScreen(navController = navController)
//        }
//
//        composable(Screens.Signup.route) {
//            SignupScreen(navController = navController)
//        }
//
//        composable(Screens.ForgetPassword.route) {
//            ForgetPasswordScreen(navController = navController)
//        }
//
//        composable(Screens.Dashboard.route) {
//            DashboardScreen()
//        }
//    }
//}