package com.botu.img.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.botu.img.R;
import com.botu.img.bean.Newsbean;
import com.botu.img.db.DbCore;
import com.botu.img.ui.adapter.NewsAdapter;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import java.io.Serializable;
import java.util.List;

/**
 * @author: swolf
 * @date : 2016-11-24 14:57
 */
public class FavouriteActivity extends BaseActivity {

    private LRecyclerView mRecyclerView;
    private List<Newsbean> mFavouriteDatas;
    private LRecyclerViewAdapter mLRecyclerViewAdapter;
    private NewsAdapter mNewsAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_favourite;
    }

    @Override
    protected void initView() {
        TextView title = (TextView) findViewById(R.id.tv_per_title);
        addStatusBarView(R.color.tab_txt_selected);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_favourite);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        title.setText(getResources().getString(R.string.collect));
        mRecyclerView = (LRecyclerView) findViewById(R.id.lRecyclerView);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        //查询数据库获取数据
        mFavouriteDatas = DbCore.getDaoSession().getNewsbeanDao().loadAll();
        for (int j = 0; j < mFavouriteDatas.size(); j++) {
            Log.i("hlh", mFavouriteDatas.get(j).getId() + "   " + mFavouriteDatas.get(j).getTilte());
        }

        mNewsAdapter = new NewsAdapter(this, R.layout.list_item_news, mFavouriteDatas);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(mNewsAdapter);

        mRecyclerView.setAdapter(mLRecyclerViewAdapter);
        mRecyclerView.setRefreshing(false);
        mRecyclerView.setPullRefreshEnabled(false);

        //点击事件, 进入画廊
        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                Intent intent = new Intent(FavouriteActivity.this, ImagePreviewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(ImagePreviewActivity.IMAGE_INFO, (Serializable) mNewsAdapter.getDatas());
                bundle.putInt(ImagePreviewActivity.CURRENT_ITEM, i);
                intent.putExtras(bundle);
                startActivity(intent);
                FavouriteActivity.this.overridePendingTransition(0, 0);
            }
        });
    }

}
