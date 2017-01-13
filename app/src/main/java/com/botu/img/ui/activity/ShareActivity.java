package com.botu.img.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.RelativeLayout;

import com.botu.img.R;
import com.botu.img.base.IConstants;
import com.botu.img.bean.ImgType;
import com.botu.img.callback.JsonCallback;
import com.botu.img.ui.adapter.ImageAdapter;
import com.botu.img.utils.SpUtils;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
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
 * 分享后的图片集合
 *
 * @author: swolf
 * @date : 2016-11-29 15:33
 */
public class ShareActivity extends BaseActivity implements OnRefreshListener, OnItemClickListener, OnLoadMoreListener {

    private LRecyclerView mRecyclerView;
    private LRecyclerViewAdapter lAdapter;
    List<ImgType> mDatas = new ArrayList<>();
    private ImageAdapter shareAdapter;

    private int page = 1;
    private RelativeLayout mView_empty;

    @Override
    public int getLayoutId() {
        return R.layout.activity_list;
    }

    @Override
    protected void initView() {
        setToolBar(getString(R.string.share), R.color.gradient, true);
        mView_empty = (RelativeLayout) findViewById(R.id.ll_empty);
        mRecyclerView = (LRecyclerView) findViewById(R.id.lRecyclerView_list);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        shareAdapter = new ImageAdapter(this, R.layout.list_item_news, mDatas);
        lAdapter = new LRecyclerViewAdapter(shareAdapter);
        mRecyclerView.setAdapter(lAdapter);
        mRecyclerView.setRefreshing(true);
        mRecyclerView.setPullRefreshEnabled(false);

        getContent();
        //点击事件, 进入画廊
        lAdapter.setOnItemClickListener(this);
        mRecyclerView.setOnRefreshListener(this);
        mRecyclerView.setOnLoadMoreListener(this);
    }

    private void getContent() {
        OkGo.post(IConstants.GET_SHARE)
                .tag (this)
                .params(IConstants.member_id, SpUtils.getInt(this, IConstants.userId, 0))
                .params("p", page)
                .execute(new JsonCallback<List<ImgType>>(new TypeToken<List<ImgType>>() {
                }.getType()) {
                    @Override
                    public void onSuccess(List<ImgType> favBeen, Call call, Response response) {
                        super.onSuccess(favBeen, call, response);
                        shareAdapter.addData(favBeen);
                        lAdapter.notifyDataSetChanged();
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        //错误处理
                        if (e.getMessage().equals("nodata")) {
                            mView_empty.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onAfter(@Nullable List<ImgType> favBeen, @Nullable Exception e) {
                        super.onAfter(favBeen, e);
                        mRecyclerView.refreshComplete();
                    }
                });

    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(ShareActivity.this, ImagePreviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ImagePreviewActivity.IMAGE_INFO, (Serializable) shareAdapter.getDatas());
        bundle.putInt(ImagePreviewActivity.CURRENT_ITEM, position);
        intent.putExtras(bundle);
        startActivity(intent);
        ShareActivity.this.overridePendingTransition(0, 0);

    }

    @Override
    public void onLoadMore() {

    }
}


