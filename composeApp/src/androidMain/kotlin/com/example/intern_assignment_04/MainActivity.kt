package com.example.intern_assignment_04

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

/** Android entrypoint, который поднимает Compose-контент приложения. */
class MainActivity : ComponentActivity() {
    /** Инициализирует окно и рендерит корневой composable. */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            App()
        }
    }
}

/** Preview корневого экрана приложения для Android Studio. */
@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
