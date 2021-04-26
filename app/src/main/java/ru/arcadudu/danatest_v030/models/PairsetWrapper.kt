package ru.arcadudu.danatest_v030.models

import java.io.Serializable

class PairsetWrapper(
    var name: String = "pairsetCollection",
    private var collection: MutableList<PairSet>
) : Serializable {

    fun retrieveCollection(): MutableList<PairSet> = collection


}