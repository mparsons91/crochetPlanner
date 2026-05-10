package com.matthewparsons.hookline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.matthewparsons.hookline.ui.HooklineApp
import com.matthewparsons.hookline.ui.theme.HooklineTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HooklineTheme {
                HooklineApp()
            }
        }
    }
}
