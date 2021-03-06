package com.link.component_menu.data.entity

import cn.bmob.v3.BmobObject
import java.io.Serializable

class Collection : BmobObject() {

    var userId: String? = null
    var id: String? = null
    var title: String? = null
    var tags: String? = null
    var imtro: String? = null
    var ingredients: String? = null
    var burden: String? = null
    var albums: List<String>? = null
    var steps: List<StepsBean>? = null
}