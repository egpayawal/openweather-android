package com.egpayawal.module.data.utils

inline fun <T> reversingOperation(
    operation: () -> T,
    reversal: (Throwable) -> T
): T = try {
    operation()
} catch (e: Throwable) {
    reversal(e)
}
