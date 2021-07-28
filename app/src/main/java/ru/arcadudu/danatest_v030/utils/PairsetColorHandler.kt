package ru.arcadudu.danatest_v030.utils

import ru.arcadudu.danatest_v030.R

class PairsetColorHandler {

    private val pairsetColorConstantList = listOf(
        PAIRSET_COLOR_DEFAULT,
        PAIRSET_COLOR_BLUE,
        PAIRSET_COLOR_GREEN,
        PAIRSET_COLOR_RED,
        PAIRSET_COLOR_VIOLET,
        PAIRSET_COLOR_GREY
    )

    private val pairsetColorIntList = listOf(
        R.color.dt3_pairset_color_default,
        R.color.dt3_pairset_color_blue,
        R.color.dt3_pairset_color_green,
        R.color.dt3_pairset_color_red,
        R.color.dt3_pairset_color_violet,
        R.color.dt3_pairset_color_grey
    )

    fun getColorIntList(): List<Int> = pairsetColorIntList

    fun getColorConstants(): List<String> = pairsetColorConstantList

    fun getListLastIndex(): Int = pairsetColorIntList.lastIndex

    fun getColorIntOnIndex(index: Int): Int {
        return if (index <= pairsetColorIntList.lastIndex && index >= 0) pairsetColorIntList[index]
        else return pairsetColorIntList[0]
    }

    fun getColorConstantOnIndex(index: Int): String {
        return if (index <= pairsetColorConstantList.lastIndex && index >= 0) pairsetColorConstantList[index]
        else return pairsetColorConstantList[0]
    }

    fun getIndexOfColorInt(colorId: Int): Int {
        return if (pairsetColorIntList.contains(colorId)) pairsetColorIntList.indexOf(colorId)
        else 0
    }

    fun getIndexOfColorConstant(colorConstant: String): Int {
        return if (pairsetColorConstantList.contains(colorConstant)) pairsetColorConstantList.indexOf(
            colorConstant
        )
        else 0
    }

    fun getColorIntOnColorConstant(colorConstant: String): Int {
        var index = 0
        if (pairsetColorConstantList.contains(colorConstant)) {
            index = getIndexOfColorConstant(colorConstant)
        }
        return pairsetColorIntList[index]
    }

    fun getColorConstantOnColorInt(colorId: Int): String {
        var index = 0
        if (pairsetColorIntList.contains(colorId))
            index = getIndexOfColorInt(colorId)
        return pairsetColorConstantList[index]
    }


}