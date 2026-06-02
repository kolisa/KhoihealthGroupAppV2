package com.khoihealth.app.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.khoihealth.app.auth.ui.ForgotPasswordScreen
import com.khoihealth.app.auth.ui.LoginScreen
import com.khoihealth.app.auth.ui.ProfileSetupScreen
import com.khoihealth.app.auth.ui.RegisterScreen
import com.khoihealth.app.devices.ui.DeviceDetailScreen
import com.khoihealth.app.devices.ui.ScanScreen
import com.khoihealth.app.goals.ui.GoalsScreen
import com.khoihealth.app.health.ui.DashboardScreen
import com.khoihealth.app.health.ui.HeartRateScreen
import com.khoihealth.app.health.ui.SleepScreen
import com.khoihealth.app.reports.ui.ReportsScreen
import com.khoihealth.app.settings.ui.SettingsScreen

@Composable
fun KhoiNavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() }
    ) {
        // Auth flow
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onRegister = { navController.navigate(Screen.Register.route) },
                onForgotPassword = { navController.navigate(Screen.ForgotPassword.route) }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.ProfileSetup.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBack = { navController.popBackStack() },
                onResetSent = { navController.popBackStack() }
            )
        }
        composable(Screen.ProfileSetup.route) {
            ProfileSetupScreen(
                onComplete = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.ProfileSetup.route) { inclusive = true }
                    }
                }
            )
        }

        // Main screens
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onHeartRateClick = { navController.navigate(Screen.HeartRateDetail.route) },
                onSleepClick = { navController.navigate(Screen.SleepDetail.route) },
                onDevicesClick = { navController.navigate(Screen.Devices.route) },
                onNavigate = { navController.navigate(it) }
            )
        }
        composable(Screen.Devices.route) {
            DeviceDetailScreen(
                deviceAddress = null,
                onScanClick = { navController.navigate(Screen.ScanDevices.route) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Goals.route) {
            GoalsScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Reports.route) {
            ReportsScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Device flow
        composable(Screen.ScanDevices.route) {
            ScanScreen(
                onDeviceConnected = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.DeviceDetail.route,
            arguments = listOf(navArgument("deviceAddress") { type = NavType.StringType })
        ) { backStack ->
            DeviceDetailScreen(
                deviceAddress = backStack.arguments?.getString("deviceAddress"),
                onScanClick = { navController.navigate(Screen.ScanDevices.route) },
                onBack = { navController.popBackStack() }
            )
        }

        // Health detail screens
        composable(Screen.HeartRateDetail.route) {
            HeartRateScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.SleepDetail.route) {
            SleepScreen(onBack = { navController.popBackStack() })
        }
    }
}
