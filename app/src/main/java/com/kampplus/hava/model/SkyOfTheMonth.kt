package com.kampplus.hava.model

fun skyNote(month: Int): Pair<String, String> = when (month) {
    1, 2 -> "Orion" to "Avcı takımyıldızı akşam saatlerinde güney yönünde görülebilir."
    3 -> "Aslan" to "Aslan takımyıldızını akşam göğünün güneyinde arayın."
    4, 5 -> "Başak" to "Başak takımyıldızı ilkbahar akşamlarının belirgin desenlerindendir."
    6 -> "Çoban" to "Çoban takımyıldızı, parlak Arkturus yıldızıyla tanınır."
    7, 8 -> "Yaz Üçgeni" to "Vega, Deneb ve Altair yaz akşamlarında büyük bir üçgen oluşturur."
    9 -> "Kuğu" to "Kuğu takımyıldızı Samanyolu boyunca uzanır."
    10 -> "Pegasus" to "Pegasus'un Büyük Kare deseni sonbahar akşamlarında yükselir."
    11 -> "Boğa" to "Boğa takımyıldızında Ülker yıldız kümesini bulabilirsiniz."
    12 -> "Orion / Boğa" to "Orion ve Boğa takımyıldızları kış akşamlarında öne çıkar."
    else -> throw IllegalArgumentException("month must be between 1 and 12")
}
