package ru.arcadudu.danatest_v030.models

import java.io.Serializable

class PairsetWrapper(
    var name: String = "pairsetCollection",
    private var collection: MutableList<Pairset>
) : Serializable {

    fun retrieveCollection(): MutableList<Pairset> = collection


}