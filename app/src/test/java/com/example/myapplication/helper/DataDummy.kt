package com.example.myapplication.helper

import com.example.myapplication.response.ListStoryItem

object  DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem>{
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100){
            val story = ListStoryItem(
                id = i.toString(),
                createdAt = "2024-11-16T12:34:56Z",
                description = "This is a description for Story $i",
                name = "Story by $i",
                lon = 100.0,
                lat = 100.0,
                photoUrl = "https://picsum.photos/200/300?random=$i"
            )
            items.add(story)
        }
        return items
    }
}