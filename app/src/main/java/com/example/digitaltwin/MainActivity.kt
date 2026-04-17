package com.example.digitaltwin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DigitalTwinApp(
                appContainer = (application as DigitalTwinApplication).appContainer,
            )
        }
    }
}

