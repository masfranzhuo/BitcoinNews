package com.hodiau.bitcoinnews

/**
 * Created by OS on 12/13/2017.
 */
class News {
    var source:String? = null
    var author:String? = null
    var title:String? = null
    var description:String? = null
    var URL:String? = null
    var imageURL:String? = null
    var publishedAt:String? = null
    var viewType:String? = null
    constructor(source:String, author:String, title:String, description:String, URL:String, imageURL:String, publishedAt:String, viewType:String) {
        this.source = source
        this.author = author
        this.title = title
        this.description = description
        this.URL = URL
        this.imageURL = imageURL
        this.publishedAt = publishedAt
        this.viewType = viewType
    }
}