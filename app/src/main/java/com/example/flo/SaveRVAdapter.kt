package com.example.flo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flo.databinding.ItemSaveBinding

class SaveRVAdapter(private val saveList: ArrayList<Save>) : RecyclerView.Adapter<SaveRVAdapter.ViewHolder>() {
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
        holder.bind(saveList[position])
        holder.binding.itemSaveMoreIv.setOnClickListener { mItemClickListener.onRemoveAlbum(position) }
    }

    override fun getItemCount(): Int = saveList.size

    inner class ViewHolder(val binding: ItemSaveBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(save: Save){
            binding.itemSaveAlbumIv.setImageResource(save.imgRes!!)
            binding.itemSaveTitleTv.text = save.title
            binding.itemSaveSingerTv.text = save.singer
        }
    }

    fun removeItem(position: Int){
        saveList.removeAt(position)
        notifyDataSetChanged()
    }
}