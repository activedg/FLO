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
    private var saveData: ArrayList<Save> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSaveBinding.inflate(inflater, container, false)

        saveData.apply {
            add(Save("Butter", "방탄소년단(BTS)", R.drawable.img_album_exp))
            add(Save("Lilac", "아이유(IU)", R.drawable.img_album_exp2))
            add(Save("Next Level", "에스파(Aespa)", R.drawable.img_album_exp3))
            add(Save("Boy with Luv", "방탄소년단 (BTS)", R.drawable.img_album_exp4))
            add(Save("BBoom BBoom", "모모랜드 (MOMOLAND)", R.drawable.img_album_exp5))
            add(Save("Weekend", "태연 (Tae Yeon)", R.drawable.img_album_exp6))
            add(Save("Next Level", "에스파(Aespa)", R.drawable.img_album_exp3))
            add(Save("Next Level", "에스파(Aespa)", R.drawable.img_album_exp3))
            add(Save("Boy with Luv", "방탄소년단 (BTS)", R.drawable.img_album_exp4))
            add(Save("Boy with Luv", "방탄소년단 (BTS)", R.drawable.img_album_exp4))
            add(Save("Lilac", "아이유(IU)", R.drawable.img_album_exp2))
            add(Save("Lilac", "아이유(IU)", R.drawable.img_album_exp2))
            add(Save("Lilac", "아이유(IU)", R.drawable.img_album_exp2))
            add(Save("Lilac", "아이유(IU)", R.drawable.img_album_exp2))
        }


        val saveRVAdapter = SaveRVAdapter(saveData)
        binding.saveListRv.adapter = saveRVAdapter
        binding.saveListRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        saveRVAdapter.setMyItemClickListener(object: SaveRVAdapter.MyItemClickListener{
            override fun onRemoveAlbum(position: Int) {
                saveRVAdapter.removeItem(position)
            }
        })

        return binding.root
    }
}