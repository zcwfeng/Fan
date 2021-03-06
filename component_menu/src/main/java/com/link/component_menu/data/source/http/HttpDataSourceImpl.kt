package com.link.component_menu.data.source.http

import com.link.component_menu.data.entity.MenuResult
import com.link.librarycomponent.entity.base.BaseEntity
import com.link.librarymodule.constant.Constant
import io.reactivex.Observable


/**
 * @author WJ
 * @date 2019-05-30
 *
 * 描述：网络数据源
 */
class HttpDataSourceImpl constructor(private val service: HttpService) : IHttpDataSource {


    companion object {
        @Volatile
        private var instance: HttpDataSourceImpl? = null

        fun getInstance(service: HttpService) =
                instance ?: synchronized(this) {
                    instance ?: HttpDataSourceImpl(service).also { instance = it }
                }
    }


    override fun getRecommend(menu: String, pn: Int, rn: Int): Observable<BaseEntity<MenuResult>> {
        return service.query(Constant.JUHE_KEY, menu, pn, rn)
    }


}