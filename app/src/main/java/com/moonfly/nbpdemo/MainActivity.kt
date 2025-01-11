package com.moonfly.nbpdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.moonfly.nbpdemo.presentation.base.NBPDemoTheme
import com.moonfly.nbpdemo.presentation.base.RootView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NBPDemoTheme {
                RootView()
            }
        }
    }
}