package com.botu.img.ui.fragment;


import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.botu.img.R;
import com.botu.img.base.IConstants;
import com.botu.img.bean.ListType;
import com.botu.img.cache.ACache;
import com.botu.img.callback.JsonCallback;
import com.botu.img.ui.activity.ListActivity;
import com.botu.img.ui.adapter.ListAdapter;
import com.botu.img.utils.L;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 主页
 *
 * @author: swolf
 * @date : 2016-11-03 09:58
 */
public class HomeFragment extends BaseFragment implements OnItemClickListener {

    private LRecyclerView mRecyclerView;
    private ListAdapter mAdapter;
    private LRecyclerViewAdapter lAdapter;
    private List<ListType> mList = new ArrayList<>();
    private ACache mACache;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView() {
        mACache = ACache.get(mActivity);
        mRecyclerView = (LRecyclerView) mActivity.findViewById(R.id.lRecyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mActivity, 3));
        mAdapter = new ListAdapter(mActivity, R.layout.list_item_type, mList);
        lAdapter = new LRecyclerViewAdapter(mAdapter);
        //禁止下拉
        mRecyclerView.setPullRefreshEnabled(false);
        mRecyclerView.setAdapter(lAdapter);
        //进入二级分类页面
        lAdapter.setOnItemClickListener(this);
    }

    @Override
    public void initData() {
        //判断是否有缓存
        mList = (List<ListType>) mACache.getAsObject("home_data");
        if (mList == null) {
            //一级分类
            request();
        } else {
            //缓存的数据
            mAdapter.addData(mList);
            lAdapter.notifyDataSetChanged();
        }
    }

    private void request() {
        OkGo.post(IConstants.NEWS_LIST_URL)
                .tag(this)
                .params(IConstants.parent_id, 0)
                .params(IConstants.gif, 0)
                .execute(new JsonCallback<List<ListType>>(new TypeToken<List<ListType>>() {
                }.getType()) {
                    @Override
                    public void onSuccess(List<ListType> types, Call call, Response response) {
                        L.i("一级分类请求成功");
                        //缓存数据，两天
                        mACache.put("home_data", (Serializable) types, 2 * ACache.TIME_DAY);
                        mAdapter.addData(types);
                        lAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        //参数错误、空数据处理、无网络
                        L.i("一级分类请求失败, 原因： " + e.toString());
                    }
                });
    }


    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(mActivity, ListActivity.class);
        intent.putExtra(IConstants.parent_id, mAdapter.getData(position).getId());
        intent.putExtra(IConstants.TITLE, mAdapter.getData(position).getName());
        mActivity.startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
    }
}
