package ru.arcadudu.danatest_v030.utils

import ru.arcadudu.danatest_v030.models.WordSet

fun getWordSet(): WordSet {
    val wordSet = WordSet("fake WordSet", "This is a fake WordSet", false)
    wordSet.addPair("table", "стол")
    wordSet.addPair("chair", "стул")
    wordSet.addPair("sofa", "диван")
    wordSet.addPair("shelf", "полка")
    wordSet.addPair("stool", "барный стул")
    wordSet.addPair("pillow", "подушка")
    wordSet.addPair("lamp", "лампа")
    return wordSet
}