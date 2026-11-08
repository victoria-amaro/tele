package com.vic.devocional

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object DailyContent {
    private val zone = ZoneId.of("America/Montevideo")
    fun index(now: Instant, count: Int): Int {
        require(count > 0) { "Se necesita al menos un devocional" }
        return Math.floorMod(now.atZone(zone).toLocalDate().toEpochDay(), count.toLong()).toInt()
    }
    fun date(now: Instant): String = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", Locale.forLanguageTag("es-UY")).withZone(zone).format(now)
    fun time(now: Instant): String = DateTimeFormatter.ofPattern("HH:mm").withZone(zone).format(now)
}
