package com.vic.devocional

import org.junit.Assert.*
import org.junit.Test
import java.time.Instant

class DailyContentTest {
    @Test fun changesAtMidnightInUruguay() {
        assertEquals(DailyContent.index(Instant.parse("2026-11-08T02:59:59Z"), 10), DailyContent.index(Instant.parse("2026-11-07T15:00:00Z"), 10))
        assertNotEquals(DailyContent.index(Instant.parse("2026-11-08T02:59:59Z"), 10), DailyContent.index(Instant.parse("2026-11-08T03:00:00Z"), 10))
    }
    @Test fun wrapsWithoutRepeatingAtNewYear() {
        val a = DailyContent.index(Instant.parse("2026-12-31T15:00:00Z"), 10)
        val b = DailyContent.index(Instant.parse("2027-01-01T15:00:00Z"), 10)
        assertEquals((a + 1) % 10, b)
    }
    @Test fun dateUsesSpanishRegardlessOfTvLanguage() {
        assertEquals("domingo 8 de noviembre", DailyContent.date(Instant.parse("2026-11-08T15:00:00Z")))
    }
    @Test(expected = IllegalArgumentException::class) fun rejectsEmptyContent() { DailyContent.index(Instant.EPOCH, 0) }
}
