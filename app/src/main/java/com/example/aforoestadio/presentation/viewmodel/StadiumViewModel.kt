package com.example.aforoestadio.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aforoestadio.data.model.AssignmentStatus
import com.example.aforoestadio.data.model.ProcessedEvent
import com.example.aforoestadio.data.model.StadiumState
import com.example.aforoestadio.data.repository.StadiumRepository
import com.example.aforoestadio.data.websocket.ConnectionState
import com.example.aforoestadio.data.websocket.WebSocketClient
import com.example.aforoestadio.domain.engine.AssignmentEngine
import com.example.aforoestadio.domain.engine.EventProcessor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class StadiumViewModel : ViewModel() {

    private val repository = StadiumRepository()

    private val engine = AssignmentEngine()
    private val processor = EventProcessor(engine, repository)

    private val wsClient = WebSocketClient(viewModelScope)
    
    val stadiumState: StateFlow<StadiumState> = repository.state

    val connectionState: StateFlow<ConnectionState> = wsClient.connectionState

    private val _snackbarEvent = MutableSharedFlow<ProcessedEvent>()
    val snackbarEvent: SharedFlow<ProcessedEvent> = _snackbarEvent.asSharedFlow()

    init {
        connectAndProcess()
    }

    private fun connectAndProcess() {
        wsClient.connect()

        viewModelScope.launch(Dispatchers.Default) {
            for (event in wsClient.eventChannel) {
                val result = processor.process(event)
                if (result.status == AssignmentStatus.ACCEPTED) {
                    _snackbarEvent.emit(result)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        wsClient.disconnect()
    }
}