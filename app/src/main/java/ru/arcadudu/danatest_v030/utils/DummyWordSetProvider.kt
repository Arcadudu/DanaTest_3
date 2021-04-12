package ru.arcadudu.danatest_v030.utils

import ru.arcadudu.danatest_v030.models.PairSet

fun getDummyPairSet(): PairSet {
    val dummyPairSet = PairSet("fake WordSet", "")

    dummyPairSet.addPair("table", "стол")
    dummyPairSet.addPair("chair", "стул")
    dummyPairSet.addPair("sofa", "диван")
    dummyPairSet.addPair("shelf", "полка")
    dummyPairSet.addPair("stool", "барный стул")
    dummyPairSet.addPair("pillow", "подушка")
    dummyPairSet.addPair("lamp", "лампа")


    return dummyPairSet
}

var timeRu = listOf(
    "январь",
    "февраль",
    "март",
    "апрель",
    "май",
    "июнь",
    "июль",
    "август",
    "сентябрь",
    "октябрь",
    "ноябрь",
    "декабрь",
    "понедельник",
    "вторник",
    "среда",
    "четверг",
    "пятница",
    "суббота",
    "воскресенье",
    "год",
    "месяц",
    "неделя",
    "день",
    "час",
    "минута",
    "секунда",
    "полночь",
    "полдень",
    "вечер",
    "утро"
)

var timeEng = listOf(
    "january",
    "february",
    "march",
    "april",
    "may",
    "june",
    "july",
    "august",
    "september",
    "october",
    "november",
    "december",
    "monday",
    "tuesday",
    "wednesday",
    "thursday",
    "friday",
    "saturday",
    "sunday",
    "year",
    "month",
    "week",
    "day",
    "hour",
    "minute",
    "second",
    "midnight",
    "noon",
    "evening",
    "morning"
)

var gbRu = listOf(
    "остров", "острова", "средний", "температура", "выше", "тысяча",
    "Атлантический", "океан", "зона (район)", "километр", "городской (урбанистический)",
    "туман", "поверхность", "гора", "гористый", "озеро", "река", "климат", "осень"
)

var gbEng = listOf(
    "island", "islands", "average", "temperature", "above", "thousand",
    "Atlantic", "ocean", "area", "kilometer", "urban",
    "fog", "surface", "mountain", "mountainous", "lake", "river", "climate", "autumn"
)

fun getTimePairSet(): PairSet {
    val timePairSet = PairSet("Time WordSet", "This is time wordSet")
    if (timeRu.count() == timeEng.count()) {
        for (i in timeRu.indices) {
            timePairSet.addPair(timeEng[i].capitalize(), timeRu[i].capitalize())
        }


    }
    return timePairSet
}

//static String[] timeRu = {"январь", "февраль", "март", "апрель", "май", "июнь", "июль", "август", "сентябрь", "октябрь",
//            "ноябрь", "декабрь", "понедельник", "вторник", "среда", "четверг", "пятница", "суббота", "воскресенье",
//            "год", "месяц", "неделя", "день", "час", "минута", "секунда", "полночь", "полдень", "вечер", "утро"};
//
//    static String[] timeEng = {"january", "february", "march", "april", "may", "june", "july", "august", "september",
//            "october", "november", "december", "monday", "tu    esday", "wednesday", "thursday", "friday", "saturday", "sunday",
//            "year", "month", "week", "day", "hour", "minute", "second", "midnight", "noon", "evening", "morning"};