package com.kampplus.hava.model

import java.time.LocalDate
import java.time.ZoneOffset
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.roundToInt

data class MoonPhaseInfo(
    val phase: Double,
    val illuminationPercent: Int,
    val ageDays: Int,
    val name: String
)

fun moonPhase(date: LocalDate): MoonPhaseInfo {
    val dateInstant = date.atStartOfDay(ZoneOffset.UTC).toInstant()
    val daysSinceReference = (dateInstant.toEpochMilli() - REFERENCE_NEW_MOON_MILLIS) / MILLIS_PER_DAY
    val phase = ((daysSinceReference % SYNODIC_MONTH_DAYS) + SYNODIC_MONTH_DAYS) % SYNODIC_MONTH_DAYS /
        SYNODIC_MONTH_DAYS
    val illumination = (1.0 - cos(2.0 * PI * phase)) / 2.0
    val nameIndex = floor(phase * MOON_NAMES.size + 0.5).toInt() % MOON_NAMES.size

    return MoonPhaseInfo(
        phase = phase,
        illuminationPercent = (illumination * 100.0).roundToInt(),
        ageDays = (phase * 29.53).roundToInt(),
        name = MOON_NAMES[nameIndex]
    )
}

private val MOON_NAMES = listOf(
    "Yeni Ay",
    "Büyüyen Hilal",
    "İlk Dördün",
    "Büyüyen Şişkin Ay",
    "Dolunay",
    "Küçülen Şişkin Ay",
    "Son Dördün",
    "Küçülen Hilal"
)
private const val SYNODIC_MONTH_DAYS = 29.530588853
private const val MILLIS_PER_DAY = 86_400_000.0
private const val REFERENCE_NEW_MOON_MILLIS = 947_182_440_000.0
