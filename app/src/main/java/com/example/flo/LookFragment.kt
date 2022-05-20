package com.example.flo

import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentLookBinding

class LookFragment : Fragment(), LookView {

    private lateinit var binding: FragmentLookBinding
    private lateinit var songRVAdapter: SongRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLookBinding.inflate(inflater, container, false)

        return binding.root
    }

    private fun getSongs(){
        val songService = SongService()
        songService.setLookView(this)

        songService.getSongs()
    }

    private fun initRecyclerView(result: FloChartResult){
        songRVAdapter = SongRVAdapter(requireContext(), result)
        binding.lookFloChartRv.adapter = songRVAdapter
    }

    override fun onStart() {
        super.onStart()
        getSongs()
    }

    override fun onGetSongLoading() {
        binding.lookLoadingPb.visibility = View.VISIBLE
    }

    override fun onGetSongSuccess(code: Int, result: FloChartResult) {
        binding.lookLoadingPb.visibility = View.GONE
        initRecyclerView(result)
    }

    override fun onGetSongFailure(code: Int, message: String) {
        binding.lookLoadingPb.visibility = View.GONE
        Log.d("LOOK-FRAG/SONG-RESPONSE", message)
    }
}