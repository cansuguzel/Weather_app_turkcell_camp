package com.kampplus.hava.model

fun suggest(tempC: Double, weatherCode: Int, windKmh: Double?): String {
    val tips = mutableListOf(
        when {
            tempC < 0.0 -> "Çok kalın mont, atkı ve bere giy"
            tempC < 10.0 -> "Kalın mont giy"
            tempC < 16.0 -> "İnce mont giy"
            tempC < 22.0 -> "Hırka veya sweatshirt giy"
            tempC < 28.0 -> "Tişört giy"
            else -> "Hafif, açık renkli giysi giy ve güneş kremi sür"
        }
    )

    when {
        weatherCode in 51..67 || weatherCode in 80..82 || weatherCode in 95..99 ->
            tips += "Şemsiye al"
        weatherCode in 71..77 || weatherCode in 85..86 ->
            tips += "Bot ve kalın mont giy"
    }
    if (windKmh != null && windKmh > 30.0) {
        tips += "Rüzgâr geçirmez bir şey giy"
    }

    return tips.joinToString(", ").replaceFirstChar(Char::uppercase) + "."
}
