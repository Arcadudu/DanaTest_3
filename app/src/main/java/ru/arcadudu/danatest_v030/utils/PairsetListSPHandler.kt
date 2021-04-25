package ru.arcadudu.danatest_v030.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.arcadudu.danatest_v030.models.PairSet
import java.lang.reflect.Type

class PairsetListSPHandler(private var context: Context) {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var gson: Gson

    fun loadSpPairsetList(): MutableList<PairSet> {
        sharedPreferences =
            context.getSharedPreferences(APP_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        gson = Gson()
        val json = sharedPreferences.getString(SHARED_PREFERENCES_PAIRSET_LIST, null)
        val type :Type = object :TypeToken<ArrayList<PairSet?>?>() {}.type
        val firstVisit = sharedPreferences.getBoolean("firstVisit", true)

        return if(firstVisit){
            val psEditor = sharedPreferences.edit()
            psEditor.putBoolean("firstVisit", false)
            psEditor.apply()
            getFirstVisitDefaultPairsetList()
        }else{
            gson.fromJson<Any>(json, type) as MutableList<PairSet>
        }
    }

    fun saveSpPairsetList(targetPairsetList: MutableList<PairSet>) {
        sharedPreferences =
            context.getSharedPreferences(APP_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        val spEditor = sharedPreferences.edit()
        gson = Gson()
        val json = gson.toJson(targetPairsetList)
        spEditor.putString(SHARED_PREFERENCES_PAIRSET_LIST, json)
        spEditor.apply()
    }

    fun deleteSpPairsetList() {
        sharedPreferences = context.getSharedPreferences(APP_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        val stEditor = sharedPreferences.edit()
        stEditor.remove(SHARED_PREFERENCES_PAIRSET_LIST)
        stEditor.apply()
    }
}