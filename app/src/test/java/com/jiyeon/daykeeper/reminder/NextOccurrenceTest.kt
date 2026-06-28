package com.jiyeon.daykeeper.reminder

import com.jiyeon.daykeeper.data.local.ScheduleItem
import com.jiyeon.daykeeper.data.local.Weekday
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Unit test cho [NextOccurrence.nextFireMillis] — hàm thuần, chạy trên JVM.
 * Dùng zone cố định để kết quả ổn định bất kể máy chạy test.
 */
class NextOccurrenceTest {

    private val zone: ZoneId = ZoneId.of("Asia/Ho_Chi_Minh")

    // 2026-06-22 là Thứ Hai (khớp comment trong DayTabRow preview).
    private val monday: LocalDate = LocalDate.of(2026, 6, 22)
    private val tuesday: LocalDate = monday.plusDays(1)
    private val wednesday: LocalDate = monday.plusDays(2)
    private val sunday: LocalDate = monday.plusDays(6)

    private fun millisAt(date: LocalDate, minuteOfDay: Int): Long =
        ZonedDateTime.of(date, LocalTime.ofSecondOfDay(minuteOfDay * 60L), zone)
            .toInstant()
            .toEpochMilli()

    private fun item(
        daysOfWeek: Int,
        startMinute: Int = 7 * 60,
        reminderEnabled: Boolean = true,
    ): ScheduleItem = ScheduleItem(
        id = 1,
        title = "Test",
        startMinute = startMinute,
        endMinute = startMinute + 60,
        daysOfWeek = daysOfWeek,
        reminderEnabled = reminderEnabled,
    )

    @Test
    fun `now before today fire returns today fire`() {
        val now = millisAt(monday, 6 * 60)            // Mon 06:00
        val result = NextOccurrence.nextFireMillis(item(Weekday.MON), now, zone)
        assertEquals(millisAt(monday, 7 * 60), result) // Mon 07:00
    }

    @Test
    fun `now after today fire returns next enabled day`() {
        val now = millisAt(monday, 8 * 60)            // Mon 08:00 (sau giờ báo)
        val result = NextOccurrence.nextFireMillis(item(Weekday.MON or Weekday.TUE), now, zone)
        assertEquals(millisAt(tuesday, 7 * 60), result) // Tue 07:00
    }

    @Test
    fun `today not enabled skips to next enabled day`() {
        val now = millisAt(monday, 6 * 60)            // Mon, nhưng chỉ bật Wed
        val result = NextOccurrence.nextFireMillis(item(Weekday.WED), now, zone)
        assertEquals(millisAt(wednesday, 7 * 60), result)
    }

    @Test
    fun `after fire on only enabled day wraps to next week`() {
        val now = millisAt(monday, 8 * 60)            // Mon 08:00, chỉ bật Mon
        val result = NextOccurrence.nextFireMillis(item(Weekday.MON), now, zone)
        assertEquals(millisAt(monday.plusDays(7), 7 * 60), result) // Mon tuần sau
    }

    @Test
    fun `now exactly at fire is not strictly after so skips`() {
        val now = millisAt(monday, 7 * 60)            // đúng Mon 07:00, chỉ bật Mon
        val result = NextOccurrence.nextFireMillis(item(Weekday.MON), now, zone)
        assertEquals(millisAt(monday.plusDays(7), 7 * 60), result) // lần kế = tuần sau
    }

    @Test
    fun `midnight boundary fires at start of enabled day`() {
        val now = millisAt(sunday, 23 * 60 + 59)      // Sun 23:59
        val result = NextOccurrence.nextFireMillis(item(Weekday.MON, startMinute = 0), now, zone)
        assertEquals(millisAt(sunday.plusDays(1), 0), result) // Thứ Hai kế tiếp 00:00
    }

    @Test
    fun `multi day mix returns nearest upcoming`() {
        val now = millisAt(tuesday, 6 * 60)           // Tue 06:00
        val days = Weekday.MON or Weekday.WED or Weekday.FRI
        val result = NextOccurrence.nextFireMillis(item(days), now, zone)
        assertEquals(millisAt(wednesday, 7 * 60), result) // Wed là gần nhất
    }

    @Test
    fun `reminder disabled returns null`() {
        val now = millisAt(monday, 6 * 60)
        val result = NextOccurrence.nextFireMillis(
            item(Weekday.MON, reminderEnabled = false), now, zone,
        )
        assertNull(result)
    }

    @Test
    fun `no days selected returns null`() {
        val now = millisAt(monday, 6 * 60)
        val result = NextOccurrence.nextFireMillis(item(daysOfWeek = 0), now, zone)
        assertNull(result)
    }
}
