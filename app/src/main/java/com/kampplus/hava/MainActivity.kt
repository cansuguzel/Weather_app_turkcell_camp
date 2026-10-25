package com.kampplus.hava

import android.graphics.Color as AndroidColor
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.kampplus.hava.ui.HavaNavHost
import com.kampplus.hava.ui.component.SplashScreen
import com.kampplus.hava.ui.theme.HavaTheme
import kotlinx.coroutines.delay

/**
 * Uygulamanın tek Activity'si; kullanıcının girdiği kapı.
 *
 * Activity bir **pencereyi** yönetir. Pencerenin içine ne çizileceğini ise
 * Compose söyler: sahne Activity'dir, dekoru Compose kurar.
 */
class MainActivity : ComponentActivity() {

    /**
     * Ekran oluşturulurken Android bu fonksiyonu **kendisi** çağırır; biz çağırmayız.
     * Yaşam döngüsünün ilk adımıdır.
     *
     * Logcat'i açıp uygulamayı arka plana alıp geri dönerek `onCreate` / `onStart` /
     * `onResume` sırasını gözlemleyebilirsin.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")

        // İçeriği durum ve navigasyon çubuklarının altına kadar yayar.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT)
        )

        // Süslü parantezin içi Compose dünyasıdır.
        setContent {
            HavaTheme {
                HavaApp()
            }
        }
    }

    @Composable
    private fun HavaApp() {
        var splashFinished by rememberSaveable { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            delay(SPLASH_DURATION_MS)
            splashFinished = true
        }
        AnimatedContent(
            targetState = splashFinished,
            transitionSpec = { fadeIn(tween(450)) togetherWith fadeOut(tween(400)) },
            label = "splashTransition"
        ) { isFinished ->
            if (isFinished) {
                HavaNavHost(modifier = Modifier.fillMaxSize())
            } else {
                SplashScreen(modifier = Modifier.fillMaxSize())
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
    }

    private companion object {
        const val SPLASH_DURATION_MS = 1_200L

        /** Logcat'te filtrelemek için: arama kutusuna `Lifecycle` yaz. */
        const val TAG = "Lifecycle"
    }
}
