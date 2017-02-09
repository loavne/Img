package com.botu.img.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.RelativeLayout;

import com.botu.img.MyApp;
import com.botu.img.R;
import com.botu.img.base.IConstants;
import com.botu.img.bean.ImgType;
import com.botu.img.cache.ACache;
import com.botu.img.callback.JsonCallback;
import com.botu.img.ui.adapter.ImageAdapter;
import com.botu.img.ui.view.MetaballView;
import com.botu.img.ui.view.StatusBarUtil;
import com.botu.img.utils.L;
import com.botu.img.utils.SpUtils;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.util.RecyclerViewStateUtils;
import com.github.jdsjlzx.view.LoadingFooter;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 内容
 *
 * @author: swolf
 * @date : 2016-12-26 15:19
 */
public class ImageActivity extends BaseActivity implements OnLoadMoreListener, OnItemClickListener {

    private LRecyclerView mRecyclerView;
    private RelativeLayout mEmpty;
    private List<ImgType> mDatas = new ArrayList<>();
    private ImageAdapter cAdapter;
    private LRecyclerViewAdapter lAdapter;
    private int page = 1;
    public boolean hasData = true;
    private ACache mACache;
    private int mCategoryId;
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
        setToolBar(getIntent().getStringExtra(IConstants.TITLE), true);
        mRecyclerView = (LRecyclerView) findViewById(R.id.lRecyclerView_list);
        mEmpty = (RelativeLayout) findViewById(R.id.rl_empty);

        mLoadingView = (RelativeLayout) findViewById(R.id.rl_loadingView);
        mMetaballView = (MetaballView)findViewById(R.id.loading);
        mMetaballView.setPaintMode(1);
        mEmptyView = (RelativeLayout) findViewById(R.id.rl_empty);
        mLoadingView.setVisibility(View.VISIBLE);

        mCategoryId = getIntent().getIntExtra(IConstants.category_id, 0);

        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        mRecyclerView.setLayoutManager(layoutManager);
        cAdapter = new ImageAdapter(this, R.layout.list_item_news, mDatas);
        lAdapter = new LRecyclerViewAdapter(cAdapter);
        mRecyclerView.setAdapter(lAdapter);
        mRecyclerView.setOnLoadMoreListener(this);//上拉加载
        lAdapter.setOnItemClickListener(this);
        mRecyclerView.setPullRefreshEnabled(false);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                layoutManager.invalidateSpanAssignments(); //防止item位置互换
            }
        });

        mACache = ACache.get(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.botu.img.update");
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatas = (List<ImgType>) mACache.getAsObject(mCategoryId + "");
        if (mDatas != null) {
            cAdapter.clear();
            cAdapter.addData(mDatas);
            //作为请求下一页做准备
            if (cAdapter.getDatas().size() % 20 == 0) {
                page = cAdapter.getDatas().size() / 20; //紧接上一次访问的页面
            } else {
                page = cAdapter.getDatas().size() / 20 + 2;
            }
            L.i("缓存数据" + cAdapter.getDatas().size() + "  page  = " + page);
            lAdapter.notifyDataSetChanged();
            mLoadingView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            L.i("请求数据");
            getContent();
        }
    }

    private void getContent() {
        OkGo.post(IConstants.NEWS_CONTENT)
                .tag(this)
                .params(IConstants.category_id, mCategoryId)
                .params(IConstants.member_id, SpUtils.getInt(this, IConstants.userId, 0))
                .params("p", page)
                .execute(new JsonCallback<List<ImgType>>(new TypeToken<List<ImgType>>() {
                }.getType()) {
                    @Override
                    public void onSuccess(List<ImgType> imgTypes, Call call, Response response) {
                        super.onSuccess(imgTypes, call, response);
                        //缓存数据
                        mACache.put(mCategoryId + "", (Serializable) imgTypes, 2 * ACache.TIME_HOUR);
                        cAdapter.addData(imgTypes);
                        lAdapter.notifyDataSetChanged();
                        mLoadingView.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        L.i("暂时无数据");
                        mLoadingView.setVisibility(View.GONE);
                        mEmpty.setVisibility(View.VISIBLE);
                        hasData = false;
                    }
                });
    }

    @Override
    public void onLoadMore() {
        LoadingFooter.State state = RecyclerViewStateUtils.getFooterViewState(mRecyclerView);
        if (state == LoadingFooter.State.Loading) {
            return;
        }
        if (MyApp.isNetworkConn) {
            if (hasData) { //上一次请求成功才接着这次请求
                page += 1;
                L.i(page + "");
                RecyclerViewStateUtils.setFooterViewState(this, mRecyclerView, 20, LoadingFooter.State.Loading, null);
                OkGo.post(IConstants.NEWS_CONTENT)
                        .tag(this)
                        .params(IConstants.category_id, mCategoryId)
                        .params(IConstants.member_id, SpUtils.getInt(this, IConstants.userId, 0))
                        .params("p", page)
                        .execute(new JsonCallback<List<ImgType>>(new TypeToken<List<ImgType>>() {
                        }.getType()) {
                            @Override
                            public void onSuccess(List<ImgType> imgTypes, Call call, Response response) {
                                super.onSuccess(imgTypes, call, response);
                                RecyclerViewStateUtils.setFooterViewState(mRecyclerView, LoadingFooter.State.Normal);
                                cAdapter.addData(imgTypes);
                                mACache.put(mCategoryId + "", (Serializable) cAdapter.getDatas(), 2 * ACache.TIME_HOUR);
                                lAdapter.notifyDataSetChanged();
                                L.i("加载成功");
                            }

                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                L.i("加载失败");
                                if (e.getMessage().equals("nodata")) {
                                    hasData = false;
                                    RecyclerViewStateUtils.setFooterViewState(mRecyclerView, LoadingFooter.State.TheEnd);
                                } else
                                    RecyclerViewStateUtils.setFooterViewState(ImageActivity.this, mRecyclerView, 20, LoadingFooter.State.NetWorkError, mOnClickListener);
                            }
                        });
            }
        } else {
            //加载失败，点击重新连接
            RecyclerViewStateUtils.setFooterViewState(this, mRecyclerView, 20, LoadingFooter.State.NetWorkError, mOnClickListener);
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (hasData) {
                onLoadMore();
            }
        }
    };

    @Override
    public void onItemClick(View view, int position) {
        //进入画廊
        Intent intent = new Intent(this, ImagePreviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ImagePreviewActivity.IMAGE_INFO, (Serializable) cAdapter.getDatas());
        bundle.putInt(ImagePreviewActivity.CURRENT_ITEM, position);
        bundle.putBoolean("isFav", false);
        intent.putExtras(bundle);
        startActivity(intent);
        this.overridePendingTransition(0, 0);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //接收数据，更改缓存源中的isFav
            L.i("picId----" + intent.getIntExtra("picId", 0) + "   current---" + intent.getIntExtra("current", 0) + "   isfav---" + intent.getIntExtra("isfav", 0));
            int current = intent.getIntExtra("current", 0);
            int isFav = intent.getIntExtra("isfav", 0);
            mACache.remove(mCategoryId + "");
            if (isFav == 0) {
                cAdapter.getDatas().get(current).setIsFav(0);
            } else {
                cAdapter.getDatas().get(current).setIsFav(1);
            }
            mACache.put(mCategoryId + "", (Serializable) cAdapter.getDatas(), 2 * ACache.TIME_HOUR);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
        unregisterReceiver(mReceiver);
    }
}
