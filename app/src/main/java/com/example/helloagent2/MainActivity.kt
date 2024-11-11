package com.example.helloagent2

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONObject
import java.util.LinkedList

class MainActivity() : AppCompatActivity(), AudioCaptureListener, OpenAIClientEventListener {
    val TAG = "MainActivity"
    var active: Boolean = false;
    private var audioCapture: AudioCapture? = null
    private var audioPlay: AudioPlay? = null
    private var ongoingConversation: String = ""
    private var conversationHistory = LinkedList<String>()

    private lateinit var oaiClient: OpenAIClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        findViewById<View>(R.id.main)?.keepScreenOn = true
        oaiClient = OpenAIClient(this)
        val startStopButton = findViewById<Button>(R.id.activate_button)
        startStopButton.isEnabled = false
        updateButtonText()
        startStopButton?.setOnClickListener {
            if (active == false) {
                activate()
            } else {
                deactivate()
            }
        }

        findViewById<TextView>(R.id.textView)?.movementMethod = ScrollingMovementMethod();
        var handler = Handler();
        var runnable = object : Runnable {
            override fun run() {
                var text = ""
                for (s in conversationHistory) {
                    text += s
                }
                findViewById<TextView>(R.id.textView)?.text = text + ongoingConversation
                findViewById<ScrollView>(R.id.scrollView1)?.fullScroll(View.FOCUS_DOWN)
                handler.postDelayed(this, 500)
            }
        }
        handler.post(runnable)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            activate()
        }
    }

    fun updateButtonText() {
        var startStopButton = findViewById<Button>(R.id.activate_button)
        if (!active) {
            startStopButton?.text = getString(R.string.activate)
        } else {
            startStopButton?.text = getString(R.string.deactivate)
        }
    }
    
    fun activate() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.RECORD_AUDIO), 1)
        } else {
            audioCapture = AudioCapture(false, this).apply {
                startRecording()
            }
            active = true
            updateButtonText()
            audioPlay = AudioPlay()
        }
    }
    
    fun deactivate() {
        audioCapture?.stopRecording()
        audioCapture = null
        active = false
        updateButtonText()
        audioPlay?.close()
        audioPlay = null
    }

    override fun onBufferReady(base64Audio: String) {
        //note: this runs on AudioCapture Thread
        findViewById<SwitchCompat>(R.id.switch1)?.isChecked?.let {
            if(!it)
                oaiClient.sendAudioData(base64Audio)
        }
    }

    override fun onEvent(eventJson: JSONObject) {
        when (eventJson.optString("type")) {
            "session.updated" -> {
                runOnUiThread(Runnable() {
                    run {
                        Toast.makeText(this, "OpenAI Session Updated", Toast.LENGTH_SHORT).show()
                        findViewById<Button>(R.id.activate_button)?.isEnabled = true
                    }
                })
            }

            "input_audio_buffer.speech_started" -> {
                audioPlay?.clearAudioQueue()
            }

            "response.audio.delta" -> {
                audioPlay?.appendAudioData(eventJson.optString("delta"))
            }

            "conversation.item.input_audio_transcription.completed" -> {
                conversationHistory.add("\n\nMe: " + eventJson.optString("transcript"))
            }

            "response.audio_transcript.delta" -> {
                val deltaText = eventJson.optString("delta", "")
                Log.i(TAG, "AI Response Delta: $deltaText")
                ongoingConversation += deltaText;
            }

            "response.audio_transcript.done" -> {
                ongoingConversation += "\n-----\n"
                conversationHistory.add(ongoingConversation)
                ongoingConversation = ""
            }
        }
    }

    override fun onOpen() {
        runOnUiThread(Runnable() {
            run {
                //Toast.makeText(this, "Web Sockets Open", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onClosing(reason: String) {
        runOnUiThread(Runnable() {
            run {
                Toast.makeText(this, "Web Sockets Closing", Toast.LENGTH_LONG).show()
            }
        })
    }
}