package com.example.flo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flo.databinding.ItemAlbumBinding

class AlbumRVAdapter(private val albumList: ArrayList<Album>) : RecyclerView.Adapter<AlbumRVAdapter.ViewHolder>() {

    interface MyItemClickListener{
        fun onItemClick(album: Album)
        fun onRemoveAlbum(position: Int)
    }

    private lateinit var mItemCLickListener: MyItemClickListener
    // 외부에서 전달 받은 리스너 어댑터 내에서 사용하기
    fun setMyItemClickListener(itemClickListener: MyItemClickListener){
        mItemCLickListener = itemClickListener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumRVAdapter.ViewHolder {
        val binding: ItemAlbumBinding = ItemAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlbumRVAdapter.ViewHolder, position: Int) {
        holder.bind(albumList[position])
        holder.itemView.setOnClickListener { mItemCLickListener.onItemClick(albumList[position]) }
        // holder.binding.itemAlbumTitleTv.setOnClickListener{ mItemCLickListener.onRemoveAlbum(position)}
    }

    override fun getItemCount(): Int = albumList.size

    fun addItem(album: Album){
        albumList.add(album)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int){
        albumList.removeAt(position)
        notifyDataSetChanged()
    }
    inner class ViewHolder(val binding: ItemAlbumBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(album: Album){
            binding.itemAlbumTitleTv.text = album.title
            binding.itemAlbumSingerTv.text = album.singer
            binding.itemAlbumCoverIv.setImageResource(album.coverImage!!)
        }
    }
}