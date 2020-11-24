package ru.arcadudu.danatest_v030.interfaces

import ru.arcadudu.danatest_v030.models.WordSet

interface ClickableItem {
    fun clickToEditor(wordSet: WordSet)
}