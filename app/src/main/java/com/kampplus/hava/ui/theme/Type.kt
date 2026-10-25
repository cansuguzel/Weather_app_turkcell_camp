package com.kampplus.hava.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

internal val HavaTypography = Typography().let { base ->
    base.copy(
        displayLarge = base.displayLarge.copy(
            fontSize = 88.sp,
            lineHeight = 96.sp,
            fontWeight = FontWeight.Light
        ),
        displayMedium = base.displayMedium.copy(fontWeight = FontWeight.Light),
        headlineLarge = base.headlineLarge.copy(fontWeight = FontWeight.SemiBold),
        headlineMedium = base.headlineMedium.copy(fontWeight = FontWeight.Medium),
        titleLarge = base.titleLarge.copy(fontWeight = FontWeight.SemiBold),
        titleMedium = base.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        bodyLarge = base.bodyLarge.copy(fontSize = 17.sp, lineHeight = 24.sp),
        bodyMedium = base.bodyMedium.copy(fontSize = 15.sp, lineHeight = 22.sp),
        labelLarge = base.labelLarge.copy(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
        labelMedium = base.labelMedium.copy(fontSize = 13.sp, lineHeight = 18.sp),
        labelSmall = base.labelSmall.copy(fontSize = 13.sp, lineHeight = 18.sp)
    )
}
