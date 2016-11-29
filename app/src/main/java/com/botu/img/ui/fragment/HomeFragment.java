package com.botu.img.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;

import com.botu.img.MyApp;
import com.botu.img.R;
import com.botu.img.base.IConstants;
import com.botu.img.bean.Newsbean;
import com.botu.img.dao.NewsbeanDao;
import com.botu.img.db.DbCore;
import com.botu.img.ui.activity.ImagePreviewActivity;
import com.botu.img.ui.adapter.NewsAdapter;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.github.jdsjlzx.util.RecyclerViewStateUtils;
import com.github.jdsjlzx.view.LoadingFooter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
public class HomeFragment extends BaseFragment implements OnLoadMoreListener, OnRefreshListener {

    public static final String TAG = "hlh";
    private LRecyclerView mRecyclerView;
    private List<Newsbean> mListNews = new ArrayList<>();
    private List<Newsbean> mCurrentNews = new ArrayList<>();
    private List<Newsbean> mDatas = new ArrayList<>();
    private NewsAdapter mAdapter;

    private int mTotalSize;         //图片总数量
    private int index = 0;          //当前图片
    private int delayTime = 1000;

    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;

    private View mEmptyView;

    private NewsbeanDao dao = DbCore.getDaoSession().getNewsbeanDao();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView() {
        mRecyclerView = (LRecyclerView) mActivity.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setOrientation(StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE); //防止item位置互换
        mRecyclerView.setLayoutManager(layoutManager); //设置线性布局
        initAdapter();
    }

    private void initAdapter() {
        mAdapter = new NewsAdapter(mActivity, R.layout.list_item_news, mDatas);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(mAdapter);
        mRecyclerView.setAdapter(mLRecyclerViewAdapter);

        mRecyclerView.setOnRefreshListener(this);
        mRecyclerView.setOnLoadMoreListener(this);

        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallBeat);  //下拉刷新样式

        mRecyclerView.setRefreshing(true); //第一次进入界面刷新

        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                //添加进浏览中,先查询数据库中是否有该条数据，有则不添加，无则添加
                Newsbean ns = dao.load((long) i);
                if (ns == null) {
                    dao.insertInTx(mAdapter.getDatas().get(i));
                }

                //进入画廊
                Intent intent = new Intent(mActivity, ImagePreviewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(ImagePreviewActivity.IMAGE_INFO, (ArrayList<? extends Parcelable>) mAdapter.getDatas());
                bundle.putInt(ImagePreviewActivity.CURRENT_ITEM, i);
                intent.putExtras(bundle);
                mActivity.startActivity(intent);
                ((Activity) mActivity).overridePendingTransition(0, 0);
            }
        });
    }

    @Override
    public void initData() {
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
                        //界面进入时刷新效果
                        mRecyclerView.refreshComplete();
                        mLRecyclerViewAdapter.notifyDataSetChanged();

                        try {
                            JSONArray array = new JSONArray(result);
                            Newsbean news;
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject jo = (JSONObject) array.get(i);
                                news = new Newsbean();
                                news.setId((long) i);
                                news.setTilte(jo.getString("tilte"));
                                news.setPicurl(jo.getString("picurl"));
                                mListNews.add(news);
                            }
                            mTotalSize = mListNews.size(); //图片总数量
                            requestData();  //绑定数据
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        //界面进入时刷新效果
                        mRecyclerView.refreshComplete();
                        mLRecyclerViewAdapter.notifyDataSetChanged();
                        //添加空白页，无网重连
                        if (mEmptyView == null)
                            mEmptyView = LayoutInflater.from(mActivity).inflate(R.layout.view_empty, null);
                        mEmptyView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //点击重新刷新数据
                            }
                        });
                        mRecyclerView.setEmptyView(mEmptyView);
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        OkGo.getInstance().cancelTag(this);
    }

    @Override
    public void onLoadMore() {
        LoadingFooter.State state = RecyclerViewStateUtils.getFooterViewState(mRecyclerView);
        if (state == LoadingFooter.State.Loading)
            return;
        if (MyApp.isNetworkConn) {
            if (index < mTotalSize) {
                //加载更多
                RecyclerViewStateUtils.setFooterViewState(mActivity, mRecyclerView, 20, LoadingFooter.State.Loading, null);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        requestData();
                        //设置刷新完毕
                        RecyclerViewStateUtils.setFooterViewState(mRecyclerView, LoadingFooter.State.Normal);
                    }
                }, delayTime);
            } else {
                //the end
                RecyclerViewStateUtils.setFooterViewState(mActivity, mRecyclerView, 20, LoadingFooter.State.TheEnd, null);
            }
        } else {
            //加载失败，点击重新连接
            RecyclerViewStateUtils.setFooterViewState(mActivity, mRecyclerView, 20, LoadingFooter.State.NetWorkError, mOnClickListener);
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //重新数据
            onLoadMore();
        }
    };

    private void requestData() {
        for (int i = 0; i < 20; i++) {
            mCurrentNews.add(mListNews.get(index + i));
        }
        mAdapter.addData(mCurrentNews);
        mCurrentNews.clear();
        index += 20;
    }

    @Override
    public void onRefresh() {
        mRecyclerView.refreshComplete();
        mLRecyclerViewAdapter.notifyDataSetChanged();
    }
}
