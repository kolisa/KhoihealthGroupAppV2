package com.khoihealth.app.navigation

sealed class Screen(val route: String) {
    // Auth
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object ProfileSetup : Screen("profile_setup")

    // Main bottom nav
    object Dashboard : Screen("dashboard")
    object Devices : Screen("devices")
    object Goals : Screen("goals")
    object Reports : Screen("reports")
    object Settings : Screen("settings")

    // Device sub-screens
    object ScanDevices : Screen("scan_devices")
    object DeviceDetail : Screen("device_detail/{deviceAddress}") {
        fun createRoute(address: String) = "device_detail/$address"
    }

    // Health detail screens
    object HeartRateDetail : Screen("heart_rate_detail")
    object SleepDetail : Screen("sleep_detail")
    object StepsDetail : Screen("steps_detail")
    object SpO2Detail : Screen("spo2_detail")
    object BloodPressureDetail : Screen("blood_pressure_detail")
}
