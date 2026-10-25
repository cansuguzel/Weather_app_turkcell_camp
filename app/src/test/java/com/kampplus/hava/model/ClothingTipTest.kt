package com.kampplus.hava.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ClothingTipTest {

    @Test
    fun `sicaklik sinirlari dogru oneriyi verir`() {
        assertTrue(suggest(-1.0, 0, null).contains("Çok kalın mont"))
        listOf(0.0, 9.0).forEach {
            assertTrue(suggest(it, 0, null).contains("Kalın mont"))
        }
        listOf(10.0, 15.0).forEach {
            assertTrue(suggest(it, 0, null).contains("İnce mont"))
        }
        listOf(16.0, 21.0).forEach {
            assertTrue(suggest(it, 0, null).contains("Hırka veya sweatshirt"))
        }
        listOf(22.0, 27.0).forEach {
            assertTrue(suggest(it, 0, null).contains("Tişört"))
        }
        assertTrue(suggest(28.0, 0, null).contains("güneş kremi"))
    }

    @Test
    fun `yagmur kar ve ruzgar tavsiyesi eklenir`() {
        assertTrue(suggest(20.0, 51, null).contains("Şemsiye al"))
        assertTrue(suggest(20.0, 82, null).contains("Şemsiye al"))
        assertTrue(suggest(20.0, 95, null).contains("Şemsiye al"))
        assertTrue(suggest(20.0, 71, null).contains("Bot ve kalın mont"))
        assertTrue(suggest(20.0, 86, null).contains("Bot ve kalın mont"))
        assertTrue(suggest(20.0, 0, 30.1).contains("Rüzgâr geçirmez"))
        assertFalse(suggest(20.0, 0, 30.0).contains("Rüzgâr geçirmez"))
        assertTrue(suggest(20.0, 61, 31.0).endsWith("."))
    }
}
