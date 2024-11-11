package com.example.helloagent2

import android.media.AudioFormat
import android.media.AudioTrack
import android.media.AudioManager
import java.util.LinkedList
import java.util.Queue
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class AudioPlay : Thread() {
    private val sampleRateHz = 24000
    private var isRunning = false
    private val audioQueue: Queue<ByteArray> = LinkedList()
    private var track: AudioTrack = AudioTrack(
        AudioManager.STREAM_MUSIC,
        sampleRateHz,
        AudioFormat.CHANNEL_OUT_MONO,
        AudioFormat.ENCODING_PCM_16BIT,
        AudioTrack.getMinBufferSize(
            sampleRateHz,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        ) * 10,
        AudioTrack.MODE_STREAM
    ).apply { play() }

    init {
        start()
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun appendAudioData(base64AudioData: String) {
        val audioData = Base64.decode(base64AudioData)
        synchronized(audioQueue) {
            audioQueue.add(audioData)
        }
    }

    override fun run() {
        isRunning = true
        while (isRunning) {
            while (audioQueue.isNotEmpty()) {
                val audioData = synchronized(audioQueue) {
                    audioQueue.poll()
                }
                if (audioData != null) {
                    track.write(audioData, 0, audioData.size)
                }
            }
        }
        track.stop()
        track.release()
    }

    fun close() {
        isRunning = false
        clearAudioQueue()
    }

    fun clearAudioQueue() {
        synchronized(audioQueue) {
            audioQueue.clear()
        }
    }
}