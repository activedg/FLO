package com.example.flo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flo.databinding.ItemSaveBinding

class SaveRVAdapter() : RecyclerView.Adapter<SaveRVAdapter.ViewHolder>() {
    private val songs = ArrayList<Song>()
    lateinit var binding: ItemSaveBinding

    interface MyItemClickListener{
        fun onRemoveAlbum(position: Int)
    }
    private lateinit var mItemClickListener: MyItemClickListener

    fun setMyItemClickListener(itemClickListener: MyItemClickListener){
        mItemClickListener = itemClickListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaveRVAdapter.ViewHolder {
        binding = ItemSaveBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SaveRVAdapter.ViewHolder, position: Int) {
        holder.bind(songs[position])
        holder.binding.itemSaveMoreIv.setOnClickListener {
            removeItem(position)
            mItemClickListener.onRemoveAlbum(songs[position].id)
        }
    }

    override fun getItemCount(): Int = songs.size

    fun addSongs(songs: ArrayList<Song>){
        this.songs.clear()
        this.songs.addAll(songs)

        notifyDataSetChanged()
    }


    fun removeItem(position: Int){
        songs.removeAt(position)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemSaveBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(song: Song){
            binding.itemSaveAlbumIv.setImageResource(song.coverImage!!)
            binding.itemSaveTitleTv.text = song.title
            binding.itemSaveSingerTv.text = song.singer
        }
    }
}