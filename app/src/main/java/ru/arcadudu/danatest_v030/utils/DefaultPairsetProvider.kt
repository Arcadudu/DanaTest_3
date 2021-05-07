package ru.arcadudu.danatest_v030.utils

import ru.arcadudu.danatest_v030.models.Pairset
import java.util.*

fun getDefaultPairsetFurniture(): Pairset {
    val defaultPairsetFurniture = Pairset("Мебель \uD83E\uDE91")
    defaultPairsetFurniture.addPair("table", "стол")
    defaultPairsetFurniture.addPair("chair", "стул")
    defaultPairsetFurniture.addPair("sofa", "диван")
    defaultPairsetFurniture.addPair("shelf", "полка")
    defaultPairsetFurniture.addPair("stool", "барный стул")
    defaultPairsetFurniture.addPair("pillow", "подушка")
    defaultPairsetFurniture.addPair("lamp", "лампа")
    return defaultPairsetFurniture
}


fun getDefaultPairsetTime(): Pairset {
    val timePairSet = Pairset("Время ⌚")
    if (timeRu.count() == timeEng.count()) {
        for (index in timeRu.indices) {
            timePairSet.addPair(
                timeEng[index].capitalize(Locale.ROOT),
                timeRu[index].capitalize(Locale.ROOT)
            )
        }
    }
    return timePairSet
}

fun getDefaultPairsetGreatBritain():Pairset{
    val gbPairSet = Pairset("Великобритания \uD83C\uDDEC\uD83C\uDDE7")
    if(gbRu.count()== gbEng.count()){
        for (index in gbRu.indices){
            gbPairSet.addPair(
                gbEng[index].capitalize(Locale.ROOT),
                gbRu[index].capitalize(Locale.ROOT)
            )
        }
    }
    return gbPairSet
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


