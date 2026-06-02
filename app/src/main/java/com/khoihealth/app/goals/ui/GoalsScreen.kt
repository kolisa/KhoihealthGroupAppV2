package com.khoihealth.app.goals.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.khoihealth.app.goals.domain.model.Goal
import com.khoihealth.app.ui.theme.KhoiTeal700
import com.khoihealth.app.ui.theme.MetricCalories
import com.khoihealth.app.ui.theme.MetricSleep
import com.khoihealth.app.ui.theme.MetricSpo2
import com.khoihealth.app.ui.theme.MetricSteps
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    onBack: () -> Unit,
    viewModel: GoalsViewModel = hiltViewModel()
) {
    val savedGoals by viewModel.goals.collectAsStateWithLifecycle()

    var stepsGoal by remember(savedGoals) { mutableStateOf(savedGoals.dailySteps.toFloat()) }
    var caloriesGoal by remember(savedGoals) { mutableStateOf(savedGoals.dailyCalories.toFloat()) }
    var sleepGoal by remember(savedGoals) { mutableStateOf(savedGoals.sleepHours) }
    var activeGoal by remember(savedGoals) { mutableStateOf(savedGoals.activeMinutes.toFloat()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Goals") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(8.dp))

            GoalSliderCard(
                icon = Icons.Default.DirectionsRun,
                label = "Daily Steps",
                value = stepsGoal.roundToInt(),
                displayValue = "${stepsGoal.roundToInt()} steps",
                range = 1000f..20000f,
                color = MetricSteps,
                onValueChange = { stepsGoal = it }
            )
            Spacer(Modifier.height(12.dp))
            GoalSliderCard(
                icon = Icons.Default.LocalFireDepartment,
                label = "Calories to Burn",
                value = caloriesGoal.roundToInt(),
                displayValue = "${caloriesGoal.roundToInt()} kcal",
                range = 100f..1500f,
                color = MetricCalories,
                onValueChange = { caloriesGoal = it }
            )
            Spacer(Modifier.height(12.dp))
            GoalSliderCard(
                icon = Icons.Default.NightsStay,
                label = "Sleep Duration",
                value = sleepGoal.roundToInt(),
                displayValue = "${String.format("%.1f", sleepGoal)} hours",
                range = 4f..12f,
                color = MetricSleep,
                onValueChange = { sleepGoal = it }
            )
            Spacer(Modifier.height(12.dp))
            GoalSliderCard(
                icon = Icons.Default.FitnessCenter,
                label = "Active Minutes",
                value = activeGoal.roundToInt(),
                displayValue = "${activeGoal.roundToInt()} minutes",
                range = 10f..120f,
                color = KhoiTeal700,
                onValueChange = { activeGoal = it }
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.saveGoals(
                        Goal(
                            dailySteps = stepsGoal.roundToInt(),
                            dailyCalories = caloriesGoal.roundToInt(),
                            sleepHours = sleepGoal,
                            activeMinutes = activeGoal.roundToInt()
                        )
                    )
                    onBack()
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(containerColor = KhoiTeal700)
            ) {
                Text("Save Goals", fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun GoalSliderCard(
    icon: ImageVector,
    label: String,
    value: Int,
    displayValue: String,
    range: ClosedFloatingPointRange<Float>,
    color: androidx.compose.ui.graphics.Color,
    onValueChange: (Float) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(icon, null, tint = color, modifier = Modifier.padding(0.dp))
                    Text(label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                }
                Text(
                    displayValue,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
            Slider(
                value = value.toFloat(),
                onValueChange = onValueChange,
                valueRange = range,
                colors = SliderDefaults.colors(thumbColor = color, activeTrackColor = color),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
