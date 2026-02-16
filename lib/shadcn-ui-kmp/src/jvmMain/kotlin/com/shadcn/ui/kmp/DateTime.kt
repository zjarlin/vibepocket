package com.shadcn.ui.kmp

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import java.time.format.DateTimeFormatter

actual fun LocalDateTime.format(format: String): String {
    val formatter = DateTimeFormatter.ofPattern(format)
    return toJavaLocalDateTime().format(formatter)
}

actual fun LocalDate.format(format: String): String {
    val formatter = DateTimeFormatter.ofPattern(format)
    return toJavaLocalDate().format(formatter)
}

// Helper functions to convert kotlinx.datetime to java.time
private fun LocalDateTime.toJavaLocalDateTime(): java.time.LocalDateTime {
    return java.time.LocalDateTime.of(year, monthNumber, dayOfMonth, hour, minute, second, nanosecond)
}

private fun LocalDate.toJavaLocalDate(): java.time.LocalDate {
    return java.time.LocalDate.of(year, monthNumber, dayOfMonth)
}