package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ui.AderaApp
import com.example.ui.theme.AderaTheme
import com.example.viewmodel.AderaViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: AderaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AderaTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AderaApp(viewModel = viewModel)
                }
            }
        }
    }
}
