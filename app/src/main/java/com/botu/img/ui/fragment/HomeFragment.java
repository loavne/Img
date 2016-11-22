package com.botu.img.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.botu.img.R;
import com.botu.img.base.IConstants;
import com.botu.img.bean.Newsbean;
import com.botu.img.ui.activity.ImagePreviewActivity;
import com.botu.img.ui.adapter.NewsAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
public class HomeFragment extends BaseFragment {

    public static final String TAG = "hlh";
    private RecyclerView mRecyclerView;
    private List<Newsbean> mListNews = new ArrayList<>();
    private List<Newsbean> mCurrentNews = new ArrayList<>();
    private NewsAdapter mAdapter;

    private int mTotalSize;         //图片总数量
    private int index = 0;   //当前图片
    private int delayMillis = 1000;
    private boolean isLoaded = false;
    private View mNotLoadingView;
    private View mEmptyView;
    private SwipeRefreshLayout mSwipe;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView() {
        mRecyclerView = (RecyclerView) mActivity.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)); //设置线性布局
        mSwipe = (SwipeRefreshLayout) mActivity.findViewById(R.id.swipe);
        mSwipe.setColorSchemeResources(R.color.colorSplash);
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipe.setRefreshing(false);
            }
        });
        mSwipe.setRefreshing(true);
    }

    @Override
    public void initData() {

        initAdapter();

        //请求网络,获取全部数据
        OkGo.get(IConstants.NEWS_URL)
                .tag(this)
                //OkHttp GBK编码是个坑,如果Response中没有写编码格式会自动使用UTF-8，可能导致中文乱码，http://www.qianlipp.com/124.html
                .execute(new AbsCallback<String>() {
                    //子线程中运行
                    @Override
                    public String convertSuccess(Response response) throws Exception {
                        byte[] bytes = response.body().bytes();
                        return new String(bytes, "GB2312");
                    }

                    //主线程中运行
                    @Override
                    public void onSuccess(String result, Call call, Response response) {
                        mSwipe.setRefreshing(false);
                        try {
                            JSONArray array = new JSONArray(result);
                            Newsbean news;
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject jo = (JSONObject) array.get(i);
                                news = new Newsbean();
                                news.setTilte(jo.getString("tilte"));
                                news.setPicurl(jo.getString("picurl"));
                                mListNews.add(news);
                            }
                            mTotalSize = mListNews.size(); //图片总数量
                            //绑定数据
                            for (int i = 0; i < 10; i++) {
                                mCurrentNews.add(mListNews.get(i));
                                Log.i(TAG, index + i +  " " + mCurrentNews.get(i).getTilte());
                            }
                            mAdapter.addData(mCurrentNews);
                            mCurrentNews.clear();
                            index = 10;//一组20个
                            isLoaded = true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        Log.i(TAG, "7");
                        super.onError(call, response, e);
                        mSwipe.setRefreshing(false);
//                        if(mEmptyView == null)
//                            mEmptyView = mActivity.getLayoutInflater().inflate(R.layout.view_empty, (ViewGroup) mRecyclerView.getParent(), false);
//                        mEmptyView.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                mAdapter.removeFooterView(mEmptyView);//清除当前view
//                            }
//                        });
//                        mAdapter.setEmptyView(mEmptyView);
                    }
                });

    }

    private void initAdapter() {
        mAdapter = new NewsAdapter(mActivity, R.layout.list_item_news, null);
        mAdapter.openLoadAnimation();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (index >= mTotalSize) {
                            mAdapter.loadComplete();
                            if (mNotLoadingView == null)
                                mNotLoadingView = mActivity.getLayoutInflater().inflate(R.layout.not_loading, (ViewGroup) mRecyclerView.getParent(), false);
                            mAdapter.addFooterView(mNotLoadingView);
                        } else {
                            Log.i(TAG, "6");
                            if (isLoaded) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        for (int i = 0; i < 10; i++) {
                                            mCurrentNews.add(mListNews.get(index + i));
                                            Log.i(TAG,index + i +  " " + mCurrentNews.get(i).getTilte());
                                        }
                                        mAdapter.addData(mCurrentNews);
                                        mCurrentNews.clear();
                                        index += 10;
                                    }
                                }, delayMillis);
                            }
                        }
                    }
                });
            }
        });

        mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void SimpleOnItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                //进入画廊
                Intent intent = new Intent(mActivity, ImagePreviewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(ImagePreviewActivity.IMAGE_INFO, (Serializable) mCurrentNews);
                bundle.putInt(ImagePreviewActivity.CURRENT_ITEM, i);
                intent.putExtras(bundle);
                mActivity.startActivity(intent);
                ((Activity) mActivity).overridePendingTransition(0, 0);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        OkGo.getInstance().cancelTag(this);
    }
}
