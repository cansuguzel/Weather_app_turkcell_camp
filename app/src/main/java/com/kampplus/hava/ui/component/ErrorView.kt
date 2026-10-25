package com.kampplus.hava.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kampplus.hava.R
import com.kampplus.hava.ui.theme.AccentYellow
import com.kampplus.hava.ui.theme.HavaTheme

@Composable
fun ErrorView(message: String, modifier: Modifier = Modifier, onRetry: (() -> Unit)? = null) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                androidx.compose.material3.Surface(
                    modifier = Modifier.size(108.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceContainerHigh
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        CloudUnavailableMark()
                    }
                }
                androidx.compose.material3.Surface(
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    color = AccentYellow
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "!",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.error_title),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
            onRetry?.let { retry ->
                Button(
                    onClick = retry,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentYellow,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Text(
                        text = stringResource(R.string.action_retry),
                        modifier = Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun CloudUnavailableMark() {
    val tint = MaterialTheme.colorScheme.error
    Canvas(Modifier.size(48.dp)) {
        val cloud = Path().apply {
            moveTo(size.width * 0.22f, size.height * 0.68f)
            cubicTo(
                size.width * 0.08f,
                size.height * 0.68f,
                size.width * 0.08f,
                size.height * 0.45f,
                size.width * 0.24f,
                size.height * 0.42f
            )
            cubicTo(
                size.width * 0.28f,
                size.height * 0.20f,
                size.width * 0.62f,
                size.height * 0.16f,
                size.width * 0.73f,
                size.height * 0.40f
            )
            cubicTo(
                size.width * 0.96f,
                size.height * 0.42f,
                size.width * 0.96f,
                size.height * 0.68f,
                size.width * 0.78f,
                size.height * 0.68f
            )
            lineTo(size.width * 0.22f, size.height * 0.68f)
        }
        drawPath(
            path = cloud,
            color = tint,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
        drawLine(
            color = tint,
            start = Offset(size.width * 0.18f, size.height * 0.18f),
            end = Offset(size.width * 0.83f, size.height * 0.83f),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorViewPreview() {
    HavaTheme {
        ErrorView(message = "Hava durumuna şu anda ulaşılamıyor. İnternet bağlantınızı kontrol edin.", onRetry = {})
    }
}
