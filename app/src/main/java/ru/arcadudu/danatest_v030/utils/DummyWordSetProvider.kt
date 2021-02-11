package ru.arcadudu.danatest_v030.utils

import ru.arcadudu.danatest_v030.models.PairSet

fun getDummyWordSet(): PairSet {
    val wordSet = PairSet("fake WordSet", "This is a fake WordSet", false)
    for(i in 0..6){
        wordSet.addPair("table", "стол")
        wordSet.addPair("chair", "стул")
        wordSet.addPair("sofa", "диван")
        wordSet.addPair("shelf", "полка")
        wordSet.addPair("stool", "барный стул")
        wordSet.addPair("pillow", "подушка")
        wordSet.addPair("lamp", "лампа")
    }

    return wordSet
}

var timeRu  = listOf("январь", "февраль", "март", "апрель", "май", "июнь", "июль", "август", "сентябрь", "октябрь",
            "ноябрь", "декабрь", "понедельник", "вторник", "среда", "четверг", "пятница", "суббота", "воскресенье",
            "год", "месяц", "неделя", "день", "час", "минута", "секунда", "полночь", "полдень", "вечер", "утро")

var timeEng = listOf("january", "february", "march", "april", "may", "june", "july", "august", "september",
            "october", "november", "december", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday",
            "year", "month", "week", "day", "hour", "minute", "second", "midnight", "noon", "evening", "morning")

var gbRu = listOf("остров", "острова", "средний", "температура", "выше", "тысяча",
    "Атлантический", "океан", "зона (район)", "километр", "городской (урбанистический)",
    "туман", "поверхность", "гора", "гористый", "озеро", "река", "климат", "осень")

var gbEng = listOf("island", "islands", "average", "temperature", "above", "thousand",
    "Atlantic", "ocean", "area", "kilometer", "urban",
    "fog", "surface", "mountain", "mountainous", "lake", "river", "climate", "autumn")

fun getTimeWordSet():PairSet{
    val wordSet = PairSet("Time WordSet", "This is time wordSet", false)
    if(timeRu.count() == timeEng.count()){
            for(i in timeRu.indices){
                wordSet.addPair(timeEng[i].capitalize(), timeRu[i].capitalize())
            }


    }
    return wordSet
}

//static String[] timeRu = {"январь", "февраль", "март", "апрель", "май", "июнь", "июль", "август", "сентябрь", "октябрь",
//            "ноябрь", "декабрь", "понедельник", "вторник", "среда", "четверг", "пятница", "суббота", "воскресенье",
//            "год", "месяц", "неделя", "день", "час", "минута", "секунда", "полночь", "полдень", "вечер", "утро"};
//
//    static String[] timeEng = {"january", "february", "march", "april", "may", "june", "july", "august", "september",
//            "october", "november", "december", "monday", "tu    esday", "wednesday", "thursday", "friday", "saturday", "sunday",
//            "year", "month", "week", "day", "hour", "minute", "second", "midnight", "noon", "evening", "morning"};