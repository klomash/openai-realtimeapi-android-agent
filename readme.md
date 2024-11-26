# Android GPT Voice Chat

A real-time voice conversation Android app that interfaces with OpenAI's GPT-4. The app enables natural voice interactions with GPT-4, providing both audio responses and text transcriptions. Users can have fluid conversations with the AI assistant, with the conversation history displayed on screen.

## Features

- Real-time voice capture and playback
- Seamless integration with OpenAI's GPT-4 via WebSocket
- Text transcription of both user input and AI responses
- Conversation history display
- Voice output using OpenAI's "alloy" voice
- Clean, simple user interface

## Setup

1. Clone the repository
2. Open the project in Android Studio
3. Replace `YOUR_KEY` in `OpenAIClient.kt` with your OpenAI API key
4. Build and run the app

## Required Permissions

- `RECORD_AUDIO` - Required for voice capture functionality

## Core Components

### AudioCapture.kt
Handles real-time audio capture from the device microphone. Features include:
- 24kHz sampling rate
- Mono channel recording
- 16-bit PCM encoding
- Base64 conversion of audio data
- Non-blocking audio buffer reading

### AudioPlay.kt
Manages audio playback of AI responses with features including:
- Synchronized audio buffer queue
- Streaming playback support
- Clean resource management
- Buffer clearing capabilities

### OpenAIClient.kt
Manages WebSocket communication with OpenAI's API, including:
- Real-time session management
- Audio data transmission
- Configuration of GPT-4 parameters
- Event handling for various response types

### MainActivity.kt
The main UI controller that:
- Coordinates between all components
- Manages audio permissions
- Displays conversation history
- Handles user interface events
- Manages application lifecycle

## Technical Details

- Language: Kotlin
- Minimum SDK: [Add your minimum SDK version]
- Target SDK: [Add your target SDK version]
- Audio Format: PCM 16-bit
- Sampling Rate: 24kHz
- OpenAI Model: gpt-4o-realtime-preview-2024-10-01

## Dependencies

- OkHttp: For WebSocket communication
- AndroidX Core: For permission handling
- Android Audio APIs: For audio capture and playback

## Usage

1. Launch the app
2. Grant microphone permissions when prompted
3. Press the activate button to start a conversation
4. Speak naturally with the AI
5. View the conversation history on screen
6. Press the deactivate button to end the session

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

MIT License

Copyright (c) 2024 Lomash Kumar

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.