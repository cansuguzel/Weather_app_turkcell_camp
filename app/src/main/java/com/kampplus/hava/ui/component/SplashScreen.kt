package com.kampplus.hava.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kampplus.hava.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    var moonVisible by remember { mutableStateOf(false) }
    var titleVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        moonVisible = true
        delay(280)
        titleVisible = true
    }

    NightSkyBackground(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AnimatedVisibility(visible = moonVisible, enter = fadeIn(tween(700))) {
                    MoonOrb(phase = 0.72f, orbSize = 150.dp)
                }
                AnimatedVisibility(
                    visible = titleVisible,
                    modifier = Modifier.padding(top = 20.dp),
                    enter = fadeIn(tween(600))
                ) {
                    Text(
                        text = stringResource(R.string.splash_title),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Text(
                text = stringResource(R.string.app_credit),
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}
