package com.example.jovel.learnux_kotlin.model

data class Video(var id: String,
                 var title: String,
                 var category: String,
                 var description: String,
                 var thumbnail: String,
                 var duration: String) {

    constructor() : this("",
            "",
            "",
            "",
            "",
            ""
    )

}