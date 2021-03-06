package com.link.component_menu.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.link.component_menu.R
import com.link.component_menu.ui.adapter.MenuAdapter
import com.link.component_menu.ui.adapter.MenuFooterAdapter
import com.link.component_menu.ui.adapter.MenuHeaderAdapter
import com.link.component_menu.data.entity.MenuDetail
import com.link.general_picture.ImageLoader
import com.link.general_picture.glide.GlideStrategy
import com.link.librarycomponent.ServiceFactory
import com.link.librarycomponent.router.RouterConstant
import com.link.librarycomponent.router.StartRouter
import com.link.librarymodule.base.mvvm.view.BaseMvvmFragment
import com.link.librarymodule.utils.ToastUtils
import com.link.librarymodule.widgets.recyclerview.ItemDecoration
import kotlinx.android.synthetic.main.menu_fragment_menu.*

/**
 * @author WJ
 * @date 2019-07-22
 *
 * 描述：
 */
class MenuFragment(override var layoutId: Int = R.layout.menu_fragment_menu) : BaseMvvmFragment<MenuViewModel>() {
    override fun getViewModel(): MenuViewModel {
        return MenuViewModelFactory.getInstance().create(MenuViewModel::class.java)
    }

    companion object {
        @JvmStatic
        fun newInstance(menuDetail: MenuDetail) =
                MenuFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(MenuDetail::class.java.canonicalName, menuDetail)
                    }
                }
    }


    private lateinit var mAdapter: MenuAdapter
    private lateinit var mHeader1Adapter: MenuHeaderAdapter
    private lateinit var mHeader2Adapter: MenuHeaderAdapter
    private lateinit var mFooterAdapter: MenuFooterAdapter


    override fun initView() {
        super.initView()
        toolbar.setNavigationOnClickListener {
            mActivity!!.onBackPressed()
        }
        mAdapter = MenuAdapter(R.layout.menu_item, null)
        rvList.addItemDecoration(ItemDecoration(0, 10, 0, 10))
        rvList.adapter = mAdapter
        refresh.isEnabled = false
        initHeaderView()
        initFootView()
    }

    private lateinit var headerView: View

    private fun initHeaderView() {
        mHeader1Adapter = MenuHeaderAdapter(android.R.layout.simple_list_item_activated_1, null)
        mHeader2Adapter = MenuHeaderAdapter(android.R.layout.simple_list_item_activated_1, null)
        headerView = LayoutInflater.from(context).inflate(R.layout.menu_header, null)

        val ingredients = headerView.findViewById<RecyclerView>(R.id.ingredients)
        ingredients.adapter = mHeader1Adapter

        val burden = headerView.findViewById<RecyclerView>(R.id.burden)
        burden.adapter = mHeader2Adapter

        headerView.findViewById<TextView>(R.id.btn_collection).setOnClickListener {
            if (ServiceFactory.getInstance().loginService!!.isLogin()){
                mViewModel.setCollection()
            }else{
                ToastUtils.showLong("请登录您的账号")
                StartRouter.navigation(RouterConstant.LOGIN)
            }
        }

        mAdapter.addHeaderView(headerView)

    }

    private lateinit var footerView: View

    private fun initFootView() {
        mFooterAdapter = MenuFooterAdapter(R.layout.menu_footer_item, null)
        footerView = LayoutInflater.from(context).inflate(R.layout.menu_footer, null)

        val rv_list = footerView.findViewById<RecyclerView>(R.id.rv_list)
        rv_list.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        rv_list.adapter = mFooterAdapter
        mAdapter.addFooterView(footerView)

        mFooterAdapter.setOnItemClickListener{ _, _, position ->

            ARouter.getInstance()
                    .build(RouterConstant.MENU)
                    .withString("MenuDetail", Gson().toJson(mFooterAdapter.getItem(position)))
                    .navigation()
        }
    }

    override fun initViewObservable() {
        super.initViewObservable()
        mViewModel.ingredients.observe(this, Observer {
            mHeader1Adapter.setNewData(it)
            showContent()
        })

        mViewModel.burden.observe(this, Observer {
            mHeader2Adapter.setNewData(it)
        })

        mViewModel.menuDetail.observe(this, Observer {
            headerView.findViewById<TextView>(R.id.name).text = it.title
            headerView.findViewById<TextView>(R.id.content).text = it.imtro
            mViewModel.ingredients.value = it.ingredients.split(";")
            mViewModel.burden.value = it.burden.split(";")
            mAdapter.setNewData(it.steps)
            ImageLoader.getInstance().with(context!!).load(it.albums[0]).build(GlideStrategy()).into(cover)
            toolbar.title = it.title
            val tag = it.tags.split(";")[0]
            mViewModel.getRecommend(tag)
            mViewModel.isCollection(it.id)
        })

        mViewModel.recommendList.observe(this, Observer {
            mFooterAdapter.setNewData(it)
        })

        mViewModel.isCollection.observe(this, Observer {
            val btnCollection = headerView.findViewById<TextView>(R.id.btn_collection)
            if (it) {
                btnCollection.text = "已收藏"
                btnCollection.setBackgroundResource(R.drawable.shape_stroke_corner_5dp_grey_dark_full)
            } else {
                btnCollection.text = "收藏"
                btnCollection.setBackgroundResource(R.drawable.shape_stroke_corner_5dp_main_full)
            }
        })


    }

    override fun loadData() {
        super.loadData()
        if (arguments != null) {
            mViewModel.menuDetail.value = arguments!!.getParcelable(MenuDetail::class.java.canonicalName)
        }

    }


}
