package com.khoihealth.app.health.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material.icons.filled.Water
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.khoihealth.app.devices.ble.BleState
import com.khoihealth.app.navigation.Screen
import com.khoihealth.app.ui.components.HealthMetricCard
import com.khoihealth.app.ui.components.KhoiBottomNavBar
import com.khoihealth.app.ui.components.LargeProgressRing
import com.khoihealth.app.ui.components.SectionHeader
import com.khoihealth.app.ui.components.StatRow
import com.khoihealth.app.ui.theme.KhoiTeal50
import com.khoihealth.app.ui.theme.KhoiTeal700
import com.khoihealth.app.ui.theme.MetricCalories
import com.khoihealth.app.ui.theme.MetricHeart
import com.khoihealth.app.ui.theme.MetricSleep
import com.khoihealth.app.ui.theme.MetricSpo2
import com.khoihealth.app.ui.theme.MetricSteps
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onHeartRateClick: () -> Unit,
    onSleepClick: () -> Unit,
    onDevicesClick: () -> Unit,
    onNavigate: (String) -> Unit,
    viewModel: HealthDashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val greeting = getGreeting(state.userName)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = greeting,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = SimpleDateFormat("EEEE, dd MMM", Locale.getDefault()).format(Date()),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    if (state.batteryLevel >= 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Icon(Icons.Default.BatteryFull, null, tint = KhoiTeal700, modifier = Modifier.size(16.dp))
                            Text("${state.batteryLevel}%", style = MaterialTheme.typography.labelSmall, color = KhoiTeal700)
                        }
                    }
                    IconButton(onClick = { viewModel.syncDevice() }) {
                        Icon(Icons.Default.Refresh, null)
                    }
                    IconButton(onClick = {}) {
                        BadgedBox(badge = { Badge { } }) {
                            Icon(Icons.Default.Notifications, null)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            KhoiBottomNavBar(
                currentRoute = Screen.Dashboard.route,
                onNavigate = onNavigate
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Connection banner
            item {
                val bleState = state.bleState
                if (bleState != BleState.Connected && bleState != BleState.Ready) {
                    DeviceBanner(bleState = bleState, onClick = onDevicesClick)
                }
            }

            // Steps hero card
            item {
                val steps = state.summary.steps
                val goal = state.goals.dailySteps
                val progress = if (goal > 0) (steps?.steps ?: 0).toFloat() / goal else 0f

                StepsHeroCard(
                    steps = steps?.steps ?: 0,
                    goal = goal,
                    calories = steps?.calories ?: 0,
                    distanceKm = steps?.distanceKm ?: 0.0,
                    activeMinutes = steps?.activeMinutes ?: 0,
                    progress = progress,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Metrics grid
            item {
                SectionHeader(
                    title = "Today's Health",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val heartRate = state.summary.latestHeartRate
                    val sleep = state.summary.latestSleep
                    val spo2 = state.summary.latestSpO2
                    val bp = state.summary.latestBp

                    item {
                        HealthMetricCard(
                            icon = Icons.Default.FavoriteBorder,
                            label = "Heart Rate",
                            value = if (heartRate != null) "${heartRate.bpm}" else "--",
                            unit = "bpm",
                            accentColor = MetricHeart,
                            onClick = onHeartRateClick,
                            modifier = Modifier.size(148.dp)
                        )
                    }
                    item {
                        HealthMetricCard(
                            icon = Icons.Default.NightsStay,
                            label = "Sleep",
                            value = if (sleep != null) "${sleep.totalMinutes / 60}h ${sleep.totalMinutes % 60}m" else "--",
                            unit = "last night",
                            accentColor = MetricSleep,
                            onClick = onSleepClick,
                            modifier = Modifier.size(148.dp)
                        )
                    }
                    if (spo2 != null) {
                        item {
                            HealthMetricCard(
                                icon = Icons.Default.Water,
                                label = "SpO₂",
                                value = "${spo2.percentage}",
                                unit = "%",
                                accentColor = MetricSpo2,
                                modifier = Modifier.size(148.dp)
                            )
                        }
                    }
                    if (bp != null) {
                        item {
                            HealthMetricCard(
                                icon = Icons.Default.FavoriteBorder,
                                label = "Blood Pressure",
                                value = "${bp.systolic}/${bp.diastolic}",
                                unit = "mmHg",
                                accentColor = MetricHeart,
                                modifier = Modifier.size(148.dp)
                            )
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun StepsHeroCard(
    steps: Int,
    goal: Int,
    calories: Int,
    distanceKm: Double,
    activeMinutes: Int,
    progress: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(KhoiTeal700, KhoiTeal700.copy(alpha = 0.8f))
                    ),
                    shape = MaterialTheme.shapes.extraLarge
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Steps",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = steps.toString(),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "/ $goal goal",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Column {
                            Text("${String.format("%.1f", distanceKm)} km", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("Distance", color = Color.White.copy(0.7f), fontSize = 11.sp)
                        }
                        Column {
                            Text("$calories", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("kcal", color = Color.White.copy(0.7f), fontSize = 11.sp)
                        }
                        Column {
                            Text("$activeMinutes", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("active min", color = Color.White.copy(0.7f), fontSize = 11.sp)
                        }
                    }
                }

                LargeProgressRing(
                    progress = progress,
                    color = Color.White,
                    size = 110.dp,
                    strokeWidth = 8.dp
                ) {
                    Text(
                        text = "${(progress * 100).roundToInt()}%",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun DeviceBanner(bleState: BleState, onClick: () -> Unit) {
    val message = when (bleState) {
        is BleState.Scanning -> "Scanning for devices..."
        is BleState.Connecting -> "Connecting..."
        is BleState.Disconnected -> "Device disconnected. Tap to connect"
        is BleState.Error -> "Connection error. Tap to retry"
        else -> "No device connected. Tap to pair"
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = KhoiTeal50),
        shape = MaterialTheme.shapes.large,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Default.Watch, null, tint = KhoiTeal700)
            Text(message, style = MaterialTheme.typography.bodySmall, color = KhoiTeal700)
        }
    }
}

private fun getGreeting(name: String): String {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    val timeGreeting = when (hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        in 17..20 -> "Good evening"
        else -> "Good night"
    }
    return if (name.isNotBlank()) "$timeGreeting, ${name.split(" ").first()}" else timeGreeting
}
