package com.example.aforoestadio.data.websocket

sealed class ConnectionState {
    data object Disconnected : ConnectionState()
    data object Reconnecting : ConnectionState()
    data object ConnectedIdle : ConnectionState()
    data object ConnectedWorking : ConnectionState()
}