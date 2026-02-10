package com.example.aforoestadio.data.websocket

import com.example.aforoestadio.data.model.EntryEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

class WebSocketClient(
    private val scope: CoroutineScope,
    private val wsUrl: String = "ws://192.168.100.90:8765"
) {
    private val json = Json { ignoreUnknownKeys = true }

    private var webSocket: WebSocket? = null

    private var reconnectJob: Job? = null
    private var inactivityJob: Job? = null

    private val _eventChannel = Channel<EntryEvent>(Channel.BUFFERED)
    val eventChannel: Channel<EntryEvent> = _eventChannel

    private val _connectionState = MutableStateFlow<ConnectionState>(
        ConnectionState.Disconnected
    )
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .retryOnConnectionFailure(true)
        .pingInterval(30, TimeUnit.SECONDS)
        .build()

    fun connect() {
        val request = Request.Builder().url(wsUrl).build()
        webSocket = client.newWebSocket(request, createListener())
    }

    private fun createListener() = object : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            reconnectJob?.cancel()
            _connectionState.value = ConnectionState.ConnectedIdle
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            try {
                val event = json.decodeFromString<EntryEvent>(text)
                scope.launch {
                    _eventChannel.send(event)
                }
                markWorking()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onFailure(
            webSocket: WebSocket,
            t: Throwable,
            response: Response?
        ) {
            _connectionState.value = ConnectionState.Disconnected
            scheduleReconnect()
        }

        override fun onClosed(
            webSocket: WebSocket,
            code: Int,
            reason: String
        ) {
            _connectionState.value = ConnectionState.Disconnected
        }
    }

    private fun markWorking() {
        _connectionState.value = ConnectionState.ConnectedWorking
        inactivityJob?.cancel()
        inactivityJob = scope.launch {
            delay(INACTIVITY_TIMEOUT)
            _connectionState.value = ConnectionState.ConnectedIdle
        }
    }

    private fun scheduleReconnect() {
        reconnectJob?.cancel()
        reconnectJob = scope.launch {
            var delayMs = INITIAL_RECONNECT_DELAY
            while (isActive) {
                _connectionState.value = ConnectionState.Reconnecting
                delay(delayMs)

                connect()

                delay(CONNECTION_CHECK_DELAY)

                val currentState = _connectionState.value
                if (currentState is ConnectionState.ConnectedIdle ||
                    currentState is ConnectionState.ConnectedWorking
                ) {
                    break
                }

                delayMs = (delayMs * 2).coerceAtMost(MAX_RECONNECT_DELAY)
            }
        }
    }

    fun disconnect() {
        reconnectJob?.cancel()
        inactivityJob?.cancel()
        webSocket?.close(NORMAL_CLOSURE_CODE, "Client closing")
        _connectionState.value = ConnectionState.Disconnected
    }

    companion object {
        private const val INACTIVITY_TIMEOUT = 5_000L
        private const val INITIAL_RECONNECT_DELAY = 1_000L
        private const val MAX_RECONNECT_DELAY = 30_000L
        private const val CONNECTION_CHECK_DELAY = 2_000L
        private const val NORMAL_CLOSURE_CODE = 1000
    }
}