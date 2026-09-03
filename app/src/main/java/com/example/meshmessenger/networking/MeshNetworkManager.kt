package com.example.meshmessenger.networking
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

import android.content.Context
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy

class MeshNetworkManager(
    private val context: Context
) {
    private val _discoveredDevices = MutableStateFlow<Map<String, String>>(emptyMap())
    val discoveredDevices: StateFlow<Map<String, String>> = _discoveredDevices

    private val _receivedMessages = MutableStateFlow<String?>(null)
    val receivedMessages: StateFlow<String?> = _receivedMessages

    private val _connectedEndpointId = MutableStateFlow<String?>(null)
    val connectedEndpointId: StateFlow<String?> = _connectedEndpointId

    private val connectionsClient: ConnectionsClient =
        Nearby.getConnectionsClient(context)

    private val serviceId = context.packageName

    private val strategy = Strategy.P2P_CLUSTER

    /**
     * Start advertising this device so nearby devices can discover it.
     */
    fun startAdvertising() {

        val advertisingOptions = AdvertisingOptions.Builder()
            .setStrategy(strategy)
            .build()

        connectionsClient.startAdvertising(
            "Mesh Messenger",
            serviceId,
            connectionLifecycleCallback,
            advertisingOptions
        )
            .addOnSuccessListener {
                println("Mesh: Advertising started")
            }
            .addOnFailureListener { exception ->
                println("Mesh: Advertising failed: ${exception.message}")
            }
    }

    /**
     * Start discovering nearby Mesh Messenger devices.
     */
    fun startDiscovery() {

        val discoveryOptions = DiscoveryOptions.Builder()
            .setStrategy(strategy)
            .build()

        connectionsClient.startDiscovery(
            serviceId,
            endpointDiscoveryCallback,
            discoveryOptions
        )
            .addOnSuccessListener {
                println("Mesh: Discovery started")
            }
            .addOnFailureListener { exception ->
                println("Mesh: Discovery failed: ${exception.message}")
            }
    }

    /**
     * Stop advertising this device.
     */
    fun stopAdvertising() {
        connectionsClient.stopAdvertising()
        println("Mesh: Advertising stopped")
    }

    /**
     * Stop discovering nearby devices.
     */
    fun stopDiscovery() {
        connectionsClient.stopDiscovery()
        println("Mesh: Discovery stopped")
    }

    /**
     * Stop all Nearby Connections.
     */
    fun stopAllConnections() {
        connectionsClient.stopAllEndpoints()
        println("Mesh: All connections stopped")
    }

    /**
     * Called when another device is discovered.
     */
    private val endpointDiscoveryCallback =
        object : EndpointDiscoveryCallback() {

            override fun onEndpointFound(
                endpointId: String,
                discoveredEndpointInfo: com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
            ) {

                println(
                    "Mesh: Found device: ${discoveredEndpointInfo.endpointName}"
                )

                _discoveredDevices.value =
                    _discoveredDevices.value + (
                            endpointId to discoveredEndpointInfo.endpointName
                            )

                connectionsClient.requestConnection(
                    "Mesh Messenger",
                    endpointId,
                    connectionLifecycleCallback
                )
                    .addOnSuccessListener {
                        println("Mesh: Connection request sent")
                    }
                    .addOnFailureListener { exception ->
                        println(
                            "Mesh: Connection request failed: ${exception.message}"
                        )
                    }
            }

            override fun onEndpointLost(endpointId: String) {

                _discoveredDevices.value =
                    _discoveredDevices.value - endpointId

                println("Mesh: Device lost: $endpointId")
            }
        }

    /**
     * Handles connection requests and connection state.
     */
    private val connectionLifecycleCallback =
        object : ConnectionLifecycleCallback() {

            override fun onConnectionInitiated(
                endpointId: String,
                connectionInfo: ConnectionInfo
            ) {

                println(
                    "Mesh: Connection initiated with ${connectionInfo.endpointName}"
                )

                connectionsClient.acceptConnection(
                    endpointId,
                    payloadCallback
                )
                    .addOnSuccessListener {
                        println("Mesh: Connection accepted")
                    }
                    .addOnFailureListener { exception ->
                        println(
                            "Mesh: Failed to accept connection: ${exception.message}"
                        )
                    }
            }

            override fun onConnectionResult(
                endpointId: String,
                result: com.google.android.gms.nearby.connection.ConnectionResolution
            ) {

                when (result.status.statusCode) {

                    ConnectionsStatusCodes.STATUS_OK -> {
                        _connectedEndpointId.value = endpointId

                        println("Mesh: Connected to $endpointId")
                    }

                    ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                        println("Mesh: Connection rejected")
                    }

                    ConnectionsStatusCodes.STATUS_ERROR -> {
                        println("Mesh: Connection error")
                    }

                    else -> {
                        println(
                            "Mesh: Connection result: ${result.status.statusCode}"
                        )
                    }
                }
            }

            override fun onDisconnected(endpointId: String) {
                if (_connectedEndpointId.value == endpointId) {
                    _connectedEndpointId.value = null
                }

                println("Mesh: Disconnected from $endpointId")
            }
        }

    /**
     * Receives messages from connected devices.
     */
    private val payloadCallback =
        object : PayloadCallback() {

            override fun onPayloadReceived(
                endpointId: String,
                payload: Payload
            ) {

                if (payload.type == Payload.Type.BYTES) {

                    val message =
                        payload.asBytes()?.toString(Charsets.UTF_8)

                    _receivedMessages.value = message

                    println(
                        "Mesh: Message received from $endpointId: $message"
                    )
                }
            }

            override fun onPayloadTransferUpdate(
                endpointId: String,
                update: PayloadTransferUpdate
            ) {

                println(
                    "Mesh: Transfer ${update.status} from $endpointId"
                )
            }
        }

    /**
     * Send a text message to a connected device.
     */
    fun sendMessage(
        endpointId: String,
        message: String
    ) {

        val payload =
            Payload.fromBytes(message.toByteArray(Charsets.UTF_8))

        connectionsClient.sendPayload(
            endpointId,
            payload
        )
            .addOnSuccessListener {
                println("Mesh: Message sent")
            }
            .addOnFailureListener { exception ->
                println(
                    "Mesh: Message failed: ${exception.message}"
                )
            }
    }
}