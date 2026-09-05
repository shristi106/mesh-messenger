package com.example.meshmessenger

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.meshmessenger.networking.MeshNetworkManager
import com.example.meshmessenger.ui.theme.MeshMessengerTheme

private val Background = Color(0xFF06100D)
private val Card = Color(0xFF14231E)
private val Green = Color(0xFF5DBB63)
private val LightText = Color(0xFFF2F7F3)
private val MutedText = Color(0xFF8A9B92)

class MainActivity : ComponentActivity() {

    private lateinit var meshNetworkManager: MeshNetworkManager

    private val permissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val allGranted = permissions.values.all { it }

            if (allGranted) {
                meshNetworkManager.startAdvertising()
                meshNetworkManager.startDiscovery()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        meshNetworkManager = MeshNetworkManager(this)

        val permissions = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                arrayOf(
                    Manifest.permission.BLUETOOTH_ADVERTISE,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.NEARBY_WIFI_DEVICES
                )
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                arrayOf(
                    Manifest.permission.BLUETOOTH_ADVERTISE,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN
                )
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }

            else -> {
                emptyArray()
            }
        }

        if (
            permissions.all {
                checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED
            }
        ) {
            meshNetworkManager.startAdvertising()
            meshNetworkManager.startDiscovery()
        } else {
            permissionLauncher.launch(permissions)
        }

        setContent {
            MeshMessengerTheme {

                var screen by remember {
                    mutableStateOf("home")
                }

                when (screen) {

                    "home" -> {
                        HomeScreen(
                            onNewMessage = {
                                screen = "new"
                            },
                            meshNetworkManager = meshNetworkManager
                        )
                    }

                    "new" -> {
                        NewMessageScreen(
                            onBack = {
                                screen = "home"
                            },
                            meshNetworkManager = meshNetworkManager
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        meshNetworkManager.stopAdvertising()
        meshNetworkManager.stopDiscovery()
        meshNetworkManager.stopAllConnections()

        super.onDestroy()
    }
}

@Composable
fun HomeScreen(
    onNewMessage: () -> Unit,
    meshNetworkManager: MeshNetworkManager
) {
    val discoveredDevices by
    meshNetworkManager.discoveredDevices.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .systemBarsPadding()
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        Green,
                        RoundedCornerShape(50.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "M",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "MESH MESSENGER • HOME",
                    fontSize = 10.sp,
                    color = MutedText
                )

                Text(
                    text = "Mesh Messenger",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = LightText
                )

                Text(
                    text = "Infrastructure-free messaging",
                    fontSize = 11.sp,
                    color = MutedText
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "●",
                    color = Green,
                    fontSize = 16.sp
                )

                Text(
                    text = "ONLINE",
                    color = Green,
                    fontSize = 8.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Card
            ),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "MESH NETWORK",
                        fontSize = 9.sp,
                        color = MutedText
                    )

                    Text(
                        text = "${discoveredDevices.size} devices discovered",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = LightText
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "●",
                        color = Green,
                        fontSize = 20.sp
                    )

                    Text(
                        text = if (discoveredDevices.isNotEmpty()) {
                            "Active"
                        } else {
                            "Searching"
                        },
                        fontSize = 9.sp,
                        color = Green
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Recent conversations",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = LightText
        )

        Spacer(modifier = Modifier.height(10.dp))

        Conversation(
            "A",
            "Alex",
            "Are you receiving this?",
            "2m"
        )

        Conversation(
            "R",
            "Riya",
            "Message relayed via Device B",
            "18m"
        )

        Conversation(
            "E",
            "Emergency Group",
            "Meeting point updated",
            "1h"
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = onNewMessage,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Green
            ),
            shape = RoundedCornerShape(10.dp)
        ) {

            Text(
                text = "+ New Message",
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Nearby",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = LightText
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (discoveredDevices.isEmpty()) {

            Text(
                text = "Searching for nearby devices...",
                color = MutedText,
                fontSize = 12.sp
            )

        } else {

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                discoveredDevices.values.forEach { deviceName ->

                    DeviceOption(
                        name = deviceName,
                        distance = "Nearby",
                        onClick = onNewMessage
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Nearby Connections secure the device-to-device connection",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 9.sp,
            color = MutedText,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun Conversation(
    letter: String,
    name: String,
    message: String,
    time: String
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Card
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(
                        Green,
                        RoundedCornerShape(50.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = letter,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = name,
                    color = LightText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                Text(
                    text = message,
                    color = MutedText,
                    fontSize = 9.sp
                )
            }

            Text(
                text = time,
                color = MutedText,
                fontSize = 8.sp
            )
        }
    }
}

@Composable
fun NewMessageScreen(
    onBack: () -> Unit,
    meshNetworkManager: MeshNetworkManager
) {
    val discoveredDevices by
    meshNetworkManager.discoveredDevices.collectAsState()

    var selectedDeviceId by remember {
        mutableStateOf<String?>(null)
    }

    var selectedDeviceName by remember {
        mutableStateOf<String?>(null)
    }

    if (
        selectedDeviceId != null &&
        selectedDeviceName != null
    ) {

        ChatScreen(
            deviceName = selectedDeviceName!!,
            endpointId = selectedDeviceId!!,
            meshNetworkManager = meshNetworkManager,
            onBack = {
                selectedDeviceId = null
                selectedDeviceName = null
            }
        )

        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .systemBarsPadding()
            .padding(16.dp)
    ) {

        Text(
            text = "← Back",
            color = Green,
            fontSize = 16.sp,
            modifier = Modifier.clickable {
                onBack()
            }
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "New Message",
            color = LightText,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Select a nearby device to start messaging",
            color = MutedText,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(25.dp))

        if (discoveredDevices.isEmpty()) {

            Text(
                text = "Searching for nearby devices...",
                color = MutedText,
                fontSize = 13.sp
            )

        } else {

            discoveredDevices.forEach { (endpointId, deviceName) ->

                DeviceOption(
                    name = deviceName,
                    distance = "Nearby",
                    onClick = {
                        selectedDeviceId = endpointId
                        selectedDeviceName = deviceName
                    }
                )
            }
        }
    }
}

@Composable
fun DeviceOption(
    name: String,
    distance: String,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Card
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable {
                onClick()
            }
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "●",
                color = Green,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {

                Text(
                    text = name,
                    color = LightText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = distance,
                    color = MutedText,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun ChatScreen(
    deviceName: String,
    endpointId: String,
    meshNetworkManager: MeshNetworkManager,
    onBack: () -> Unit
) {
    var message by remember {
        mutableStateOf("")
    }

    var messages by remember {
        mutableStateOf(listOf<String>())
    }

    val receivedMessage by
    meshNetworkManager.receivedMessages.collectAsState()

    LaunchedEffect(receivedMessage) {
        if (receivedMessage != null) {
            val newMessage = "Received: $receivedMessage"

            if (!messages.contains(newMessage)) {
                messages = messages + newMessage
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .systemBarsPadding()
            .padding(horizontal = 16.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "←",
                color = Green,
                fontSize = 24.sp,
                modifier = Modifier.clickable {
                    onBack()
                }
            )

            Spacer(modifier = Modifier.width(15.dp))

            Column {

                Text(
                    text = deviceName,
                    color = LightText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Connected through mesh",
                    color = Green,
                    fontSize = 10.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Bottom
        ) {

            messages.forEach { msg ->

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Green
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {

                        Text(
                            text = msg,
                            color = Color.Black,
                            modifier = Modifier.padding(
                                horizontal = 14.dp,
                                vertical = 10.dp
                            )
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            OutlinedTextField(
                value = message,
                onValueChange = {
                    message = it
                },
                modifier = Modifier.weight(1f),
                placeholder = {

                    Text(
                        text = "Type a message...",
                        color = MutedText
                    )
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {

                    if (message.isNotBlank()) {

                        meshNetworkManager.sendMessage(
                            endpointId,
                            message
                        )

                        messages = messages + message

                        message = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Green
                )
            ) {

                Text(
                    text = "Send",
                    color = Color.Black
                )
            }
        }
    }
}