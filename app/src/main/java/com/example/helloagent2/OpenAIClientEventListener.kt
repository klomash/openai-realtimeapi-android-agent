package com.example.helloagent2

import org.json.JSONObject

interface OpenAIClientEventListener {
    fun onEvent(eventJson: JSONObject)
    fun onOpen()
    fun onClosing(reason: String)
}