package com.khoihealth.app.health.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.khoihealth.app.core.utils.DateUtils
import com.khoihealth.app.ui.components.StatRow
import com.khoihealth.app.ui.theme.MetricSleep

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepScreen(
    onBack: () -> Unit,
    viewModel: HealthDashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val sleep = state.summary.latestSleep

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sleep Analysis") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Total sleep hero
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MetricSleep),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.NightsStay, null, tint = Color.White, modifier = Modifier.padding(4.dp))
                    Spacer(Modifier.height(8.dp))
                    Text(
                        if (sleep != null) "${sleep.totalMinutes / 60}h ${sleep.totalMinutes % 60}m" else "--",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text("Total Sleep", color = Color.White.copy(0.8f), style = MaterialTheme.typography.bodyMedium)
                    if (sleep != null) {
                        Text(
                            "Score: ${sleep.sleepScore}/100",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            if (sleep != null) {
                // Sleep breakdown
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Sleep Breakdown", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)

                        SleepPhaseRow("Deep Sleep", sleep.deepSleepMinutes, sleep.totalMinutes, Color(0xFF3F51B5))
                        SleepPhaseRow("Light Sleep", sleep.lightSleepMinutes, sleep.totalMinutes, Color(0xFF7986CB))
                        SleepPhaseRow("REM Sleep", sleep.remSleepMinutes, sleep.totalMinutes, MetricSleep)
                        SleepPhaseRow("Awake", sleep.awakeMinutes, sleep.totalMinutes, Color(0xFFFFB74D))
                    }
                }

                Spacer(Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Sleep Schedule", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        StatRow("Bedtime", DateUtils.formatTime(sleep.startTimestamp), MetricSleep)
                        StatRow("Wake time", DateUtils.formatTime(sleep.endTimestamp), MetricSleep)
                        StatRow("Duration", DateUtils.formatDuration(sleep.totalMinutes), MetricSleep)
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.NightsStay, null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.4f))
                        Spacer(Modifier.height(8.dp))
                        Text("No sleep data yet", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Sync your device to see sleep analysis", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f))
                    }
                }
            }
        }
    }
}

@Composable
private fun SleepPhaseRow(label: String, minutes: Int, total: Int, color: Color) {
    val fraction = if (total > 0) minutes.toFloat() / total else 0f
    Column {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(DateUtils.formatDuration(minutes), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = color,
            trackColor = color.copy(alpha = 0.15f)
        )
    }
}
