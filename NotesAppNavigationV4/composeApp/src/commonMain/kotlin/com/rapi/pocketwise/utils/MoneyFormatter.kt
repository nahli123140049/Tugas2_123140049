package com.rapi.pocketwise.utils

fun formatRupiah(value: Int): String {
    return "Rp" + value.toString()
        .reversed()
        .chunked(3)
        .joinToString(".")
        .reversed()
}