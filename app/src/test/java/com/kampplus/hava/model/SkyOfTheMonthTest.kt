package com.kampplus.hava.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class SkyOfTheMonthTest {

    @Test
    fun `tum aylar kuzey yarimkure notlariyla eslenir`() {
        val expected = mapOf(
            1 to "Orion",
            2 to "Orion",
            3 to "Aslan",
            4 to "Başak",
            5 to "Başak",
            6 to "Çoban",
            7 to "Yaz Üçgeni",
            8 to "Yaz Üçgeni",
            9 to "Kuğu",
            10 to "Pegasus",
            11 to "Boğa",
            12 to "Orion / Boğa"
        )

        expected.forEach { (month, constellation) ->
            val result = skyNote(month)
            assertEquals(constellation, result.first)
            assertTrue(result.second.isNotBlank())
        }
    }

    @Test
    fun `gecersiz ay reddedilir`() {
        assertThrows(IllegalArgumentException::class.java) { skyNote(0) }
        assertThrows(IllegalArgumentException::class.java) { skyNote(13) }
    }
}
