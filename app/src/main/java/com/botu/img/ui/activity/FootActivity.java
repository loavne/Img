package com.botu.img.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.botu.img.R;
import com.botu.img.base.IConstants;
import com.botu.img.bean.ImgType;
import com.botu.img.callback.JsonCallback;
import com.botu.img.ui.adapter.ImageAdapter;
import com.botu.img.ui.view.MetaballView;
import com.botu.img.ui.view.StatusBarUtil;
import com.botu.img.utils.SpUtils;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 浏览
 *
 * @author: swolf
 * @date : 2016-12-28 14:57
 */
public class FootActivity extends BaseActivity implements OnItemClickListener, View.OnClickListener {

    private LRecyclerView mRecyclerView;
    private LRecyclerViewAdapter lAdapter;
    List<ImgType> mDatas = new ArrayList<>();
    private ImageAdapter mAdapter;
    private int page = 1;
    private RelativeLayout mView_empty;
    private TextView mDelete;
    private MetaballView mMetaballView;
    private RelativeLayout mLoadingView;


    @Override
    public int getLayoutId() {
        return R.layout.activity_list;
    }

    @Override
    protected void initView() {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary), 0);
        setToolBar(getString(R.string.recent), true);
        mDelete = (TextView) findViewById(R.id.tv_right);
        mDelete.setText(getString(R.string.delete));
        mView_empty = (RelativeLayout) findViewById(R.id.rl_empty);
        mRecyclerView = (LRecyclerView) findViewById(R.id.lRecyclerView_list);
        mLoadingView = (RelativeLayout) findViewById(R.id.rl_loadingView);
        mMetaballView = (MetaballView) findViewById(R.id.loading);
        mMetaballView.setPaintMode(1);
        mLoadingView.setVisibility(View.VISIBLE);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ImageAdapter(this, R.layout.list_item_news, mDatas);
        lAdapter = new LRecyclerViewAdapter(mAdapter);
        mRecyclerView.setAdapter(lAdapter);
        mRecyclerView.setRefreshing(true);
        mRecyclerView.setPullRefreshEnabled(false);


        //点击事件, 进入画廊
        lAdapter.setOnItemClickListener(this);
        mDelete.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getContent();
    }

    private void getContent() {
        OkGo.post(IConstants.GET_FOOT)
                .tag(this)
                .params(IConstants.member_id, SpUtils.getInt(this, IConstants.userId, 0))
                .params("p", page)
                .execute(new JsonCallback<List<ImgType>>(new TypeToken<List<ImgType>>() {
                }.getType()) {
                    @Override
                    public void onSuccess(List<ImgType> favBeen, Call call, Response response) {
                        super.onSuccess(favBeen, call, response);
                        mAdapter.addData(favBeen);
                        lAdapter.notifyDataSetChanged();
                        mLoadingView.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        //错误处理
                        if (e.getMessage().equals("nodata")) {
                            mLoadingView.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.GONE);
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
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(FootActivity.this, ImagePreviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ImagePreviewActivity.IMAGE_INFO, (Serializable) mAdapter.getDatas());
        bundle.putInt(ImagePreviewActivity.CURRENT_ITEM, position);
        intent.putExtras(bundle);
        startActivity(intent);
        FootActivity.this.overridePendingTransition(0, 0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_right:
                OkGo.post(IConstants.DEL_FOOT)
                        .tag(this)
                        .params(IConstants.member_id, SpUtils.getInt(this, IConstants.userId, 0))
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                Toast.makeText(FootActivity.this, "清除成功", Toast.LENGTH_SHORT).show();
                                onStart();
                            }
                        });


                break;
        }
    }
}
