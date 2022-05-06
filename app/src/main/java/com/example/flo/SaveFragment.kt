package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo.databinding.FragmentSaveBinding

class SaveFragment: Fragment() {
    lateinit var binding: FragmentSaveBinding
    lateinit var songDB: SongDatabase
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSaveBinding.inflate(inflater, container, false)
        songDB = SongDatabase.getInstance(requireContext())!!

        val saveRVAdapter = SaveRVAdapter()
        binding.saveListRv.adapter = saveRVAdapter
        saveRVAdapter.addSongs(songDB.songDao().getLikeSongs(true) as ArrayList<Song>)
        binding.saveListRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        saveRVAdapter.setMyItemClickListener(object: SaveRVAdapter.MyItemClickListener{
            override fun onRemoveAlbum(songId: Int) {

                songDB.songDao().updateIsLikeById(false, songId)
            }
        })

        return binding.root
    }
}