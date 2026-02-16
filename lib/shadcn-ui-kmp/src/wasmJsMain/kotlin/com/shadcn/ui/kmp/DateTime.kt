package com.shadcn.ui.kmp

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

actual fun LocalDateTime.format(format: String): String {
    // Basic implementation for WasmJs - may need a proper date formatting library for full functionality
    // This is a placeholder; a real implementation would use a library or more complex logic
    return "$year-${monthNumber.toString().padStart(2, '0')}-${dayOfMonth.toString().padStart(2, '0')} ${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}:${second.toString().padStart(2, '0')}"
}

actual fun LocalDate.format(format: String): String {
    // Basic implementation for WasmJs
    return "$year-${monthNumber.toString().padStart(2, '0')}-${dayOfMonth.toString().padStart(2, '0')}"
}