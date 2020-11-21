package ru.arcadudu.danatest_v030.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.adapters.WordSetAdapter
import ru.arcadudu.danatest_v030.models.WordSet

private lateinit var recyclerView: RecyclerView
private lateinit var myAdapter: WordSetAdapter
private lateinit var itemList: MutableList<WordSet>

class WordSetFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_word_set, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        packList()
        recyclerView = view.findViewById(R.id.wordSet_recycler)

        recyclerView.apply {
            myAdapter = WordSetAdapter()
            myAdapter.submitList(itemList)
            adapter = myAdapter
            layoutManager =LinearLayoutManager(activity)

        }


    }

    private fun packList() {
        itemList = mutableListOf()
        for (i in 1..20) itemList.add(WordSet(name = "WordSet $i", description = "Description $i"))
    }
}