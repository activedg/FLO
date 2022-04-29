package com.example.flo

data class Album(
    var title: String = "",
    var singer: String = "",
    var coverImage: Int? = null,
    var songs: ArrayList<Song>? = null
)
