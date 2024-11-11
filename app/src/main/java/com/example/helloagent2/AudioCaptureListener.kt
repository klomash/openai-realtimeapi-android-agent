package com.example.helloagent2

interface AudioCaptureListener {
    fun onBufferReady(base64Audio: String)
}