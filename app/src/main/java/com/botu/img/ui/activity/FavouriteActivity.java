package com.botu.img.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.RelativeLayout;

import com.botu.img.MyApp;
import com.botu.img.R;
import com.botu.img.base.IConstants;
import com.botu.img.bean.ImgType;
import com.botu.img.callback.JsonCallback;
import com.botu.img.ui.adapter.ImageAdapter;
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
 * 收藏
 * @author: swolf
 * @date : 2016-11-24 14:57
 */
public class FavouriteActivity extends BaseActivity implements OnLoadMoreListener, OnItemClickListener, View.OnClickListener {

    private LRecyclerView mRecyclerView;
    private RelativeLayout mView_empty;
    private LRecyclerViewAdapter lAdapter;
    private ImageAdapter favAdapter;
    List<ImgType> mDatas = new ArrayList<>();

    private int page = 1;

    @Override
    public int getLayoutId() {
        return R.layout.activity_list;
    }

    @Override
    protected void initView() {
        setToolBar(getString(R.string.favourite), R.color.gradient, true);
        mView_empty = (RelativeLayout) findViewById(R.id.ll_empty);
        mRecyclerView = (LRecyclerView) findViewById(R.id.lRecyclerView_list);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        favAdapter = new ImageAdapter(this, R.layout.list_item_news, mDatas);
        lAdapter = new LRecyclerViewAdapter(favAdapter);
        mRecyclerView.setAdapter(lAdapter);

        mRecyclerView.setPullRefreshEnabled(false);
        lAdapter.setOnItemClickListener(this);
        mRecyclerView.setOnLoadMoreListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getContent(page, false);
    }

    private void getContent(int p, final boolean onLoad) {
        OkGo.post(IConstants.GET_COLLECT)
                .tag(this)
                .params(IConstants.member_id, SpUtils.getInt(this, IConstants.userId, 0))
                .params("p", p)
                .execute(new JsonCallback<List<ImgType>>(new TypeToken<List<ImgType>>() {
                }.getType()) {
                    @Override
                    public void onSuccess(List<ImgType> favBeen, Call call, Response response) {
                        super.onSuccess(favBeen, call, response);
                        favAdapter.clear();
                        favAdapter.addData(favBeen);
                        lAdapter.notifyDataSetChanged();
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        mView_empty.setVisibility(View.VISIBLE);
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
            page += 1;
            RecyclerViewStateUtils.setFooterViewState(this, mRecyclerView, 20, LoadingFooter.State.Loading, null);
            OkGo.post(IConstants.GET_COLLECT)
                    .tag(this)
                    .params(IConstants.member_id, SpUtils.getInt(this, IConstants.userId, 0))
                    .params("p", page)
                    .execute(new JsonCallback<List<ImgType>>(new TypeToken<List<ImgType>>() {
                    }.getType()) {
                        @Override
                        public void onSuccess(List<ImgType> favBeen, Call call, Response response) {
                            super.onSuccess(favBeen, call, response);
                            favAdapter.addData(favBeen);
                            lAdapter.notifyDataSetChanged();
                        }
                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);
                                //错误处理
                                if (e.getMessage().equals("nodata"))
                                    RecyclerViewStateUtils.setFooterViewState(mRecyclerView, LoadingFooter.State.TheEnd);
                                else
                                    RecyclerViewStateUtils.setFooterViewState(FavouriteActivity.this, mRecyclerView, 20, LoadingFooter.State.NetWorkError, mOnClickListener);
                        }
                    });

        } else {
            //加载失败，点击重新连接
            RecyclerViewStateUtils.setFooterViewState(this, mRecyclerView, 20, LoadingFooter.State.NetWorkError, mOnClickListener);
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //重新数据
            onLoadMore();
        }
    };

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(FavouriteActivity.this, ImagePreviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ImagePreviewActivity.IMAGE_INFO, (Serializable) favAdapter.getDatas());
        bundle.putInt(ImagePreviewActivity.CURRENT_ITEM, position);
        bundle.putBoolean("isFav", true);
        intent.putExtras(bundle);
        startActivity(intent);
        FavouriteActivity.this.overridePendingTransition(0, 0);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_right:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        OkGo.getInstance().cancelTag(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
    }
}



