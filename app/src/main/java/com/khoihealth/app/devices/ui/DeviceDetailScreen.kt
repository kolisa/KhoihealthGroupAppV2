package com.khoihealth.app.devices.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Battery6Bar
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.khoihealth.app.devices.ble.BleState
import com.khoihealth.app.navigation.Screen
import com.khoihealth.app.ui.components.KhoiBottomNavBar
import com.khoihealth.app.ui.theme.KhoiTeal50
import com.khoihealth.app.ui.theme.KhoiTeal700
import com.khoihealth.app.ui.theme.StatusError
import com.khoihealth.app.ui.theme.StatusSuccess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceDetailScreen(
    deviceAddress: String?,
    onScanClick: () -> Unit,
    onBack: () -> Unit,
    viewModel: DeviceViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showUnbindDialog by remember { mutableStateOf(false) }

    val isConnected = state.bleState == BleState.Connected || state.bleState == BleState.Ready
    val statusColor = when {
        isConnected -> StatusSuccess
        state.bleState is BleState.Connecting -> MaterialTheme.colorScheme.tertiary
        else -> StatusError
    }
    val statusText = when (state.bleState) {
        BleState.Connected, BleState.Ready -> "Connected"
        BleState.Connecting -> "Connecting..."
        BleState.Scanning -> "Scanning..."
        BleState.Disconnected -> "Disconnected"
        is BleState.Error -> "Error"
        else -> "No Device"
    }

    if (showUnbindDialog) {
        AlertDialog(
            onDismissRequest = { showUnbindDialog = false },
            title = { Text("Unbind Device") },
            text = { Text("Are you sure you want to unbind this device? You'll need to pair again to use it.") },
            confirmButton = {
                Button(
                    onClick = { viewModel.unbind(); showUnbindDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Unbind") }
            },
            dismissButton = {
                TextButton(onClick = { showUnbindDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Device") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        },
        bottomBar = {
            KhoiBottomNavBar(
                currentRoute = Screen.Devices.route,
                onNavigate = {}
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Device icon + status
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(KhoiTeal50, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Watch, null, tint = KhoiTeal700, modifier = Modifier.size(56.dp))
            }
            Spacer(Modifier.height(12.dp))

            Text(
                text = state.boundDevice?.name ?: state.uiState.let { "" }.let { "No device paired" },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(statusColor, CircleShape)
                )
                Text(statusText, style = MaterialTheme.typography.bodySmall, color = statusColor)
            }

            Spacer(Modifier.height(24.dp))

            // Device info cards
            if (state.boundDevice != null || isConnected) {
                DeviceInfoGrid(state = state)
                Spacer(Modifier.height(20.dp))

                // Action buttons
                if (isConnected) {
                    Button(
                        onClick = { viewModel.requestBattery() },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = KhoiTeal700),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Icon(Icons.Default.Sync, null, modifier = Modifier.size(18.dp))
                        Text(" Sync Now", fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { viewModel.disconnect() },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Icon(Icons.Default.BluetoothDisabled, null, modifier = Modifier.size(18.dp))
                        Text(" Disconnect")
                    }
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = { showUnbindDialog = true }) {
                        Icon(Icons.Default.LinkOff, null, tint = MaterialTheme.colorScheme.error)
                        Text(" Unbind Device", color = MaterialTheme.colorScheme.error)
                    }
                } else {
                    Button(
                        onClick = onScanClick,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = KhoiTeal700),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Icon(Icons.Default.Search, null)
                        Text(" Scan for Devices", fontWeight = FontWeight.SemiBold)
                    }
                }
            } else {
                NoPairedDeviceView(onScanClick = onScanClick)
            }
        }
    }
}

@Composable
private fun DeviceInfoGrid(state: DeviceUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DeviceInfoItem(
                icon = Icons.Default.Battery6Bar,
                label = "Battery",
                value = if (state.batteryLevel >= 0) "${state.batteryLevel}%" else "Unknown",
                modifier = Modifier.weight(1f)
            )
            DeviceInfoItem(
                icon = Icons.Default.Info,
                label = "Firmware",
                value = state.firmwareVersion ?: "Unknown",
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DeviceInfoItem(
                icon = Icons.Default.BluetoothConnected,
                label = "Address",
                value = state.boundDevice?.address?.take(17) ?: "N/A",
                modifier = Modifier.weight(1f)
            )
            DeviceInfoItem(
                icon = Icons.Default.Watch,
                label = "HR Monitor",
                value = if (state.boundDevice?.supportsHR == true) "Supported" else "N/A",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun DeviceInfoItem(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(icon, null, tint = KhoiTeal700, modifier = Modifier.size(14.dp))
                Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun NoPairedDeviceView(onScanClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Watch,
            null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "No Device Paired",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            "Pair your fitness device to start tracking your health data",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onScanClick,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = KhoiTeal700),
            shape = MaterialTheme.shapes.large
        ) {
            Icon(Icons.Default.Search, null)
            Text(" Pair a Device", fontWeight = FontWeight.SemiBold)
        }
    }
}

private val DeviceUiState.uiState: Any get() = Unit
