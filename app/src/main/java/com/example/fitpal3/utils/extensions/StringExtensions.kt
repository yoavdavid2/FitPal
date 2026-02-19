package com.example.fitpal3.utils.extensions

fun String.toCapital(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}