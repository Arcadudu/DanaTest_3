package ru.arcadudu.danatest_v030.models

import java.io.Serializable

data class Pair(var key: String, var value: String) :Serializable {
    val pairId = key + value
}