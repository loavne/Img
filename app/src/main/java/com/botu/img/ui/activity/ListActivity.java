package com.botu.img.ui.activity;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.RelativeLayout;

import com.botu.img.R;
import com.botu.img.base.IConstants;
import com.botu.img.bean.ListType;
import com.botu.img.cache.ACache;
import com.botu.img.callback.JsonCallback;
import com.botu.img.ui.adapter.ListAdapter;
import com.botu.img.ui.view.MetaballView;
import com.botu.img.ui.view.StatusBarUtil;
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
 * @author: swolf
 * @date : 2016-12-23 17:27
 */
public class ListActivity extends BaseActivity implements OnItemClickListener {

    private LRecyclerView mRecylerView;
    private List<ListType> mList = new ArrayList<>();
    private ListAdapter mAdapter;
    private LRecyclerViewAdapter lAdapter;

    private static int current = 0;
    private static String currentTitle; // 当前标题
    private ACache mACache;
    private int mParentId;
    private MetaballView mMetaballView;
    private RelativeLayout mEmptyView;
    private RelativeLayout mLoadingView;

    @Override
    public int getLayoutId() {
        return R.layout.activity_list;
    }

    @Override
    protected void initView() {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary), 0);
        //设置标题栏
        setToolBar(getIntent().getStringExtra(IConstants.TITLE), true);
        //缓存对象
        mACache = ACache.get(this);
        //父类Id
        mParentId = getIntent().getIntExtra(IConstants.parent_id, 0);

        mLoadingView = (RelativeLayout) findViewById(R.id.rl_loadingView);
        mMetaballView = (MetaballView)findViewById(R.id.loading);
        mMetaballView.setPaintMode(1);
        mEmptyView = (RelativeLayout) findViewById(R.id.rl_empty);

        mRecylerView = (LRecyclerView) findViewById(R.id.lRecyclerView_list);
        mRecylerView.setLayoutManager(new GridLayoutManager(ListActivity.this, 3));
        //禁止下拉刷新
        mRecylerView.setPullRefreshEnabled(false);
        //适配器
        mAdapter = new ListAdapter(this, R.layout.list_item_type, mList);
        lAdapter = new LRecyclerViewAdapter(mAdapter);
        mRecylerView.setAdapter(lAdapter);

        mLoadingView.setVisibility(View.VISIBLE);

        //数据适配
        mList = (List<ListType>) mACache.getAsObject(mParentId + "");
        if (mList != null) {
            mAdapter.addData(mList);
            mLoadingView.setVisibility(View.GONE);
            mRecylerView.setVisibility(View.VISIBLE);
            lAdapter.notifyDataSetChanged();
        } else {
            request();
        }

        //因为只有两级分类，所以这里点击进入图片列表页。
        lAdapter.setOnItemClickListener(this);

    }

    private void request() {
        OkGo.post(IConstants.NEWS_LIST_URL)

                .tag(this)
                .params(IConstants.parent_id, mParentId)
                .params(IConstants.gif, 0)
                .execute(new JsonCallback<List<ListType>>(new TypeToken<List<ListType>>() {
                }.getType()) {
                    @Override
                    public void onSuccess(List<ListType> types, Call call, Response response) {
                        super.onSuccess(types, call, response);
                        L.i("二级分类请求成功");
                        mAdapter.addData(types);
                        lAdapter.notifyDataSetChanged();
                        mLoadingView.setVisibility(View.GONE);
                        mRecylerView.setVisibility(View.VISIBLE);
                        //缓存数据
                        mACache.put(mParentId + "", (Serializable) mAdapter.getDatas(), 1 * ACache.TIME_DAY);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        L.i("二级分类请求失败, 原因：" + e.toString());
                        mLoadingView.setVisibility(View.GONE);
                        mEmptyView.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public void onItemClick(View view, int position) {
        //点击Item所对应的Id
        current = mAdapter.getData(position).getId();
        currentTitle = mAdapter.getData(position).getName();
        Intent intent = new Intent(ListActivity.this, ImageActivity.class);
        intent.putExtra(IConstants.category_id, current);
        intent.putExtra(IConstants.TITLE, currentTitle);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
    }
}
