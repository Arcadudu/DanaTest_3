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
    val timePairSet = Pairset("Время ⌚", pairsetColor = PAIRSET_COLOR_RED)
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

fun getDefaultPairsetGreatBritain(): Pairset {
    val gbPairSet = Pairset("Великобритания \uD83C\uDDEC\uD83C\uDDE7", pairsetColor = PAIRSET_COLOR_BLUE)
    if (gbRu.count() == gbEng.count()) {
        for (index in gbRu.indices) {
            gbPairSet.addPair(
                gbEng[index].capitalize(Locale.ROOT),
                gbRu[index].capitalize(Locale.ROOT)
            )
        }
    }
    return gbPairSet
}

fun getDefaultPairsetHumanFace(): Pairset {
    val humanFacePairset = Pairset("Лицо человека \uD83D\uDC68\uD83C\uDFFB\u200D", pairsetColor = PAIRSET_COLOR_GREY)
    if (faceRu.count() == faceEng.count()) {
        for (index in faceRu.indices) {
            humanFacePairset.addPair(
                faceEng[index].capitalize(Locale.ROOT),
                faceRu[index].capitalize(Locale.ROOT)
            )
        }
    }
    return humanFacePairset
}

fun getDefaultPairsetHumanBody(): Pairset{
    val humanBodyPairset = Pairset("Части тела \uD83D\uDCAA\uD83C\uDFFB", pairsetColor = PAIRSET_COLOR_GREEN)
    if(bodyRu.count() == bodyEng.count()){
        for(index in bodyRu.indices){
            humanBodyPairset.addPair(
                bodyEng[index].capitalize(Locale.ROOT),
                bodyRu[index].capitalize(Locale.ROOT)
            )
        }
    }
    return humanBodyPairset
}

fun getDefaultPairsetHouseBasic(): Pairset{
    val houseBasicPairset = Pairset("Дом \uD83C\uDFE1", pairsetColor = PAIRSET_COLOR_VIOLET)
    if(houseBasicRu.count()== houseBasicEng.count()){
        for (index in houseBasicRu.indices){
            houseBasicPairset.addPair(
                houseBasicEng[index].capitalize(Locale.ROOT),
                houseBasicRu[index].capitalize(Locale.ROOT)
            )
        }
    }
    return houseBasicPairset
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
    "Атлантический", "океан", "область", "километр", "урбанистический",
    "туман", "поверхность", "гора", "гористый", "озеро", "река", "климат", "осень"
)

var gbEng = listOf(
    "island", "islands", "average", "temperature", "above", "thousand",
    "Atlantic", "ocean", "area", "kilometer", "urban",
    "fog", "surface", "mountain", "mountainous", "lake", "river", "climate", "autumn"
)

val faceRu = listOf(
    "лоб", "волосы", "бровь", "брови", "глаз",
    "глаза", "ресницы", "зрачок", "нос", "губы",
    "зуб", "зубы", "язык", "ухо", "уши", "рот"
)

val faceEng = listOf(
    "forehead", "hair", "brow", "brows", "eye",
    "eyes", "eyelashes", "pupil", "nose", "lips",
    "tooth", "teeth", "tongue", "ear", "ears", "mouth"
)


var bodyRu = listOf(
    "голова",
    "шея",
    "плечо",
    "руки",
    "локоть",
    "предплечье",
    "кисть руки",
    "ладонь",
    "пальцы",
    "грудная клетка",
    "желудок",
    "бедро",
    "нога",
    "колено",
    "икра",
    "ступня",
    "ступни",
    "пальцы на ногах",
    "позвоночник",
    "спина",
    "попа"
)

var bodyEng = listOf(
    "head",
    "neck",
    "shoulder",
    "arms",
    "elbow",
    "forearm",
    "hand",
    "palm",
    "fingers",
    "chest",
    "stomach",
    "hip",
    "leg",
    "knee",
    "calf",
    "foot",
    "feet",
    "toes",
    "spine",
    "back",
    "butt"
)

var houseBasicRu = listOf(
    "кухня", "спальня", "столовая", "ванная", "туалет", "гостиная", "зал", "подвал", "чердак",
    "гараж", "балкон", "пол", "стена", "потолок", "дверь", "ворота", "окно", "этаж", "крыша",
    "задний двор", "сад", "лифт"
)

var houseBasicEng = listOf(
    "kitchen",
    "bedroom",
    "dining room",
    "bathroom",
    "toilet",
    "living room",
    "hall",
    "basement",
    "attic",
    "garage",
    "balcony",
    "floor",
    "wall",
    "ceiling",
    "door",
    "gates",
    "window",
    "floor",
    "roof",
    "backyard",
    "garden",
    "elevator"
)






