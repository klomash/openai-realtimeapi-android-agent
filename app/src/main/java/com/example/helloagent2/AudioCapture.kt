package com.example.helloagent2

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
class AudioCapture (
    private var stopped: Boolean,
    private val audioCaptureListener: AudioCaptureListener)
    : Thread() {
    private val sampleRateHz = 24000;
    private val TAG = "AudioCapture"
    fun startRecording() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO)
        stopped = false
        start()
    }

    fun stopRecording() {
        stopped = true
    }

    @SuppressLint("MissingPermission")
    override fun run() {
        var recorder: AudioRecord? = null

        try
        {
            val minBufferSize = AudioRecord.getMinBufferSize(sampleRateHz, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
            recorder = AudioRecord(
                MediaRecorder.AudioSource.VOICE_COMMUNICATION,
                sampleRateHz,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                minBufferSize * 10
            )
            recorder.startRecording()

            val buffers = ByteArray( minBufferSize * 10 )
            while(!stopped)
            {
                val readBytes = recorder.read(buffers, 0, buffers.size, AudioRecord.READ_NON_BLOCKING)
                if (readBytes > 0) {
                    val base64Audio = Base64.encode(buffers, 0, readBytes)
                    audioCaptureListener.onBufferReady(base64Audio)
                }
            }
        }
        catch(x: Throwable)
        {
            Log.w(TAG, "Error reading audio data: ", x)
        }

        finally
        {
            recorder?.stop()
            recorder?.release()
        }
    }
}