package com.example.meshmessenger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.meshmessenger.ui.theme.MeshMessengerTheme
import androidx.compose.foundation.clickable
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding

private val Background = Color(0xFF06100D)
private val Card = Color(0xFF14231E)
private val Green = Color(0xFF5DBB63)
private val LightText = Color(0xFFF2F7F3)
private val MutedText = Color(0xFF8A9B92)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
    MeshMessengerTheme {
        var screen by remember { mutableStateOf("home") }

        if (screen == "home") {
            HomeScreen(
                onNewMessage = { screen = "new" }
            )
        } else {
            NewMessageScreen(
                onBack = { screen = "home" }
            )
        }
    }
}
    }
}

@Composable
fun HomeScreen(onNewMessage: () -> Unit) {
    Column(
    modifier = Modifier
        .fillMaxSize()
        .background(Background)
        .systemBarsPadding()
        .padding(16.dp)
){

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(Green, RoundedCornerShape(50.dp)),
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

            Column(modifier = Modifier.weight(1f)) {
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

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
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

        // Mesh network card
        Card(
            colors = CardDefaults.cardColors(containerColor = Card),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "MESH NETWORK",
                        fontSize = 9.sp,
                        color = MutedText
                    )
                    Text(
                        text = "4 devices connected",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = LightText
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "●",
                        color = Green,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "Strong",
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

        Conversation("A", "Alex", "Are you receiving this?", "2m")
        Conversation("R", "Riya", "Message relayed via Device B", "18m")
        Conversation("E", "Emergency Group", "Meeting point updated", "1h")

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = onNewMessage,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Green),
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NearbyDevice("Device A", "3 m away")
            NearbyDevice("Device B", "8 m away")
            NearbyDevice("Device C", "12 m away")
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Messages are end-to-end encrypted",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 9.sp,
            color = MutedText,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
        colors = CardDefaults.cardColors(containerColor = Card),
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
                    .background(Green, RoundedCornerShape(50.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = letter,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
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
fun RowScope.NearbyDevice(name: String, distance: String)  {
    Card(
        colors = CardDefaults.cardColors(containerColor = Card),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.weight(1f)
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "●",
                    color = Green,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = name,
                    color = LightText,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = distance,
                color = MutedText,
                fontSize = 8.sp
            )
        }
    }
}
@Composable
fun NewMessageScreen(onBack: () -> Unit) {
    var selectedDevice by remember { mutableStateOf<String?>(null) }

    if (selectedDevice != null) {
        ChatScreen(
            deviceName = selectedDevice!!,
            onBack = { selectedDevice = null }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp)
    ) {

        Text(
            text = "← Back",
            color = Green,
            fontSize = 16.sp,
            modifier = Modifier.clickable { onBack() }
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

        DeviceOption("Device A", "3 m away") {
            selectedDevice = "Device A"
        }

        DeviceOption("Device B", "8 m away") {
            selectedDevice = "Device B"
        }

        DeviceOption("Device C", "12 m away") {
            selectedDevice = "Device C"
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
        colors = CardDefaults.cardColors(containerColor = Card),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() }
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
    onBack: () -> Unit
) {
    var message by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf<String>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .systemBarsPadding()
            .padding(horizontal = 16.dp)
    ) {

        // Header
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
                modifier = Modifier.clickable { onBack() }
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

        // Messages area
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

        // Input area
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
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