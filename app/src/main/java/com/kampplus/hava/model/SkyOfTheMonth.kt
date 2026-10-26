package com.kampplus.hava.model

data class NightSkyGuide(
    val constellation: String,
    val note: String,
    val stars: List<String>
)

fun skyNote(month: Int): Pair<String, String> = when (month) {
    1, 2 -> "Orion" to "Avcı takımyıldızı Betelgeuse ve Rigel yıldızlarıyla tanınır."
    3 -> "Aslan" to "Aslan takımyıldızının en parlak yıldızı Regulus'tur."
    4, 5 -> "Başak" to "Başak takımyıldızı parlak Spica yıldızına ev sahipliği yapar."
    6 -> "Çoban" to "Çoban takımyıldızı, parlak Arkturus yıldızıyla tanınır."
    7, 8 -> "Yaz Üçgeni" to "Vega, Deneb ve Altair gökyüzünde büyük bir üçgen oluşturur."
    9 -> "Kuğu" to "Kuğu takımyıldızının en parlak yıldızı Deneb'dir."
    10 -> "Pegasus" to "Pegasus takımyıldızı Büyük Kare deseniyle tanınır."
    11 -> "Boğa" to "Boğa takımyıldızında Aldebaran ve Ülker yıldız kümesi bulunur."
    12 -> "Orion / Boğa" to "Orion'da Betelgeuse ve Rigel, Boğa'da Aldebaran öne çıkar."
    else -> throw IllegalArgumentException("month must be between 1 and 12")
}

fun nightSkyGuide(month: Int, latitude: Double): NightSkyGuide {
    require(month in 1..12) { "month must be between 1 and 12" }
    val seasonalMonth = if (latitude < 0.0) (month + 5) % 12 + 1 else month
    val (constellation, note) = skyNote(seasonalMonth)
    val stars = when (seasonalMonth) {
        1, 2 -> listOf("Betelgeuse", "Rigel")
        3 -> listOf("Regulus")
        4, 5 -> listOf("Spica")
        6 -> listOf("Arkturus")
        7, 8 -> listOf("Vega", "Deneb", "Altair")
        9 -> listOf("Deneb")
        10 -> listOf("Markab", "Alpheratz")
        11 -> listOf("Aldebaran", "Ülker")
        else -> listOf("Betelgeuse", "Rigel", "Aldebaran")
    }
    return NightSkyGuide(constellation, note, stars)
}
