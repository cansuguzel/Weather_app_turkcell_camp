package com.kampplus.hava.model

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MoonPhaseTest {

    @Test
    fun `referans yeni aya gore tarih fazi hesaplanir`() {
        val result = moonPhase(LocalDate.of(2000, 1, 7))

        assertEquals((5.0 + 46.0 / 60.0) / 24.0, result.phase * 29.530588853, 0.000001)
        assertEquals(0, result.illuminationPercent)
        assertEquals("Yeni Ay", result.name)
    }

    @Test
    fun `dolunay doneminde aydinlanma yuzdeye yuvarlanir`() {
        val result = moonPhase(LocalDate.of(2000, 1, 21))

        assertTrue(result.illuminationPercent >= 98)
        assertEquals("Dolunay", result.name)
    }

    @Test
    fun `faz her zaman dongu icinde kalir`() {
        val result = moonPhase(LocalDate.of(1900, 1, 1))

        assertTrue(result.phase >= 0.0)
        assertTrue(result.phase < 1.0)
        assertTrue(result.illuminationPercent in 0..100)
        assertTrue(result.ageDays in 0..30)
    }
}
