package ru.arcadudu.danatest_v030.models

data class Pair(var key: String, var value: String) {
    val pairId = key + value
}