package com.example.helloagent2

import android.util.Log
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject

class OpenAIClient(
    private val openAIClientEventListener: OpenAIClientEventListener,
) : WebSocketListener() {
    private val TAG = "OpenAIClient"
    private var webSocket: okhttp3.WebSocket
    private val openApiKey = "YOUR_KEY"
    private val wsURL = "wss://api.openai.com/v1/realtime?model=gpt-4o-realtime-preview-2024-10-01"
    init {
        val client = okhttp3.OkHttpClient()
        val request = okhttp3.Request.Builder()
            .url(wsURL)
            .addHeader("Authorization", "Bearer $openApiKey")
            .addHeader("OpenAI-Beta", "realtime=v1")
            .build()

        webSocket = client.newWebSocket(request, this)
    }

    override fun onOpen(webSocket: okhttp3.WebSocket, response: okhttp3.Response) {
        Log.d(TAG, "WebSocket connection opened")
        openAIClientEventListener.onOpen()
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
        openAIClientEventListener.onClosing(reason)
    }

    override fun onMessage(webSocket: okhttp3.WebSocket, text: String) {
        Log.d(TAG, "Message received: $text")

        val eventJson = JSONObject(text)

        when (eventJson.optString("type")) {
            "session.created" -> {
                sendSessionUpdate()
            }

            "conversation.item.input_audio_transcription.completed" -> {
                val questionText = eventJson.optString("transcript", "")
                Log.i(TAG, "User Question: $questionText")
            }
        }
        openAIClientEventListener.onEvent(eventJson)
    }

    fun sendAudioData(base64Audio: String) {
        val json = JSONObject().apply {
            put("type", "input_audio_buffer.append")
            put("audio", base64Audio)
        }
        //Log.i(TAG,"base64Audio.length = ${base64Audio.length}")
        webSocket.send(json.toString())
    }

    private fun sendSessionUpdate() {
        val config = """{
            "type": "session.update",
            "session": {
                "instructions": "You are a helpful, witty and friendly agent.",
                "turn_detection":  {
                   "type": "server_vad",
                    "threshold": 0.5,
                    "prefix_padding_ms": 300,
                    "silence_duration_ms": 500
                },
                "voice": "alloy",
                "temperature": 0.7,
                "max_response_output_tokens": 2048,
                "modalities": ["text", "audio"],
                "input_audio_format": "pcm16",
                "output_audio_format": "pcm16",
                "input_audio_transcription": {
                    "model": "whisper-1"
                },
                "tool_choice": "auto"
            }
        }"""
        webSocket.send(config)
    }
}
