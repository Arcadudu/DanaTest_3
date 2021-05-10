package ru.arcadudu.danatest_v030.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.arcadudu.danatest_v030.models.Pairset
import java.lang.reflect.Type

class PairsetListSPHandler(private var context: Context) {

    private var sharedPreferences: SharedPreferences
    private var gson: Gson
    private var type: Type


    init {
        sharedPreferences =
            context.getSharedPreferences(APP_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        gson = Gson()
        type = object : TypeToken<ArrayList<Pairset?>?>() {}.type
    }

    fun loadSpPairsetList(): MutableList<Pairset> {
        val json = sharedPreferences.getString(SHARED_PREFERENCES_PAIRSET_LIST, null)
        val firstVisit = sharedPreferences.getBoolean("firstVisit", true)

        return if (firstVisit) {
            sharedPreferences.edit().apply {
                putBoolean("firstVisit", false)
                apply()
            }
            getFirstVisitDefaultPairsetList()
        } else {
            gson.fromJson<Any>(json, type) as MutableList<Pairset>
        }
    }

    fun saveSpPairsetList(targetPairsetList: MutableList<Pairset>) {
        val json = gson.toJson(targetPairsetList)
        sharedPreferences.edit().apply {
            putString(SHARED_PREFERENCES_PAIRSET_LIST, json)
            apply()
        }
    }

    fun deleteSpPairsetList() {
        sharedPreferences =
            context.getSharedPreferences(APP_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            remove(SHARED_PREFERENCES_PAIRSET_LIST)
            apply()
        }
    }


}