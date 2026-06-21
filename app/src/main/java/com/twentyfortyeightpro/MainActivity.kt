package com.twentyfortyeightpro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.twentyfortyeightpro.ui.theme.TwentyFortyEightProTheme

/**
 * Single-activity host. Screen-to-screen navigation happens entirely inside
 * Compose via NavHost (see ui/navigation) — this Activity just sets the
 * theme and content root.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TwentyFortyEightProTheme {
                // TODO: swap for AppNavHost() once navigation + screens are wired up.
                PlaceholderRoot()
            }
        }
    }
}

@Composable
private fun PlaceholderRoot() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Text("2048 Pro — scaffold booting 🚧", color = MaterialTheme.colorScheme.onBackground)
    }
}
