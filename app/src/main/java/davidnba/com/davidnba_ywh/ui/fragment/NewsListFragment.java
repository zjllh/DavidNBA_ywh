package davidnba.com.davidnba_ywh.ui.fragment;

import android.content.Intent;

import davidnba.com.davidnba_ywh.R;
import davidnba.com.davidnba_ywh.app.Constant;
import davidnba.com.davidnba_ywh.base.BaseLazyFragment;
import davidnba.com.davidnba_ywh.base.BaseWebActivity;
import davidnba.com.davidnba_ywh.http.api.RequestCallback;
import davidnba.com.davidnba_ywh.http.api.tencent.TecentService;
import davidnba.com.davidnba_ywh.http.bean.news.NewsIndex;
import davidnba.com.davidnba_ywh.http.bean.news.NewsItem;
import davidnba.com.davidnba_ywh.support.OnListItemClickListener;
import davidnba.com.davidnba_ywh.support.SpaceItemDecoration;
import davidnba.com.davidnba_ywh.support.SupportRecyclerView;
import davidnba.com.davidnba_ywh.ui.adapter.NewsAdapter;
import davidnba.com.davidnba_ywh.ui.view.activity.NewsDetailActivity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.yuyh.library.utils.DimenUtils;
import com.yuyh.library.utils.log.LogUtils;
import com.yuyh.library.utils.toast.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 仁昌居士 on 2017/2/13.
 */

public class NewsListFragment extends BaseLazyFragment {
    public static final String INTENT_INT_INDEX = "intent_int_index";
    Constant.NewsType newsType = Constant.NewsType.BANNER;
    private NewsAdapter adapter;

    @BindView(R.id.refresh)
    MaterialRefreshLayout materialRefreshLayout;
    @BindView(R.id.recyclerview)
    SupportRecyclerView recyclerView;
    @BindView(R.id.emptyView)
    View emptyView;

    private List<NewsItem.NewsItemBean> list = new ArrayList<>();
    private List<String> indexs = new ArrayList<>();
    private int start = 0;//查询数据起始位置
    private int num = 10;


    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_normal_recyclerview);
        ButterKnife.bind(this, getContentView());
        showLoadingDialog();
        newsType = (Constant.NewsType) getArguments().getSerializable(INTENT_INT_INDEX);
        Log.d("rcjs + newtype" ,"rcjs + newtype" + newsType);
        initView();
        requestIndex(false);
    }

    private void requestIndex(final boolean isRefresh) {
        Log.d("rcjs + newtype" ,"rcjs + newtypeRE" + newsType);
        TecentService.getNewsIndex(newsType,isRefresh,new RequestCallback<NewsIndex>(){
            @Override
            public void onSuccess(NewsIndex newsIndex) {
                indexs.clear();
                start = 0;
                for (NewsIndex.IndexBean bean : newsIndex.data) {
                    indexs.add(bean.id);
                }
                String arcIds = parseIds();
                requestNews(arcIds, isRefresh, false);
            }

            @Override
            public void onFailure(String message) {
                complete();
                LogUtils.i(message);
            }
        });
    }

    private void initView() {
        adapter = new NewsAdapter(list, mActivity, R.layout.item_list_news_normal, R.layout.item_list_news_video);
        adapter.setOnItemClickListener(new OnListItemClickListener<NewsItem.NewsItemBean>() {
            @Override
            public void onItemClick(View view, int position, NewsItem.NewsItemBean data) {
                Intent intent;
                Log.d("rcjs + newtype" ,"rcjs + newtypezz" + newsType);
                switch (newsType) {
                    case VIDEO:
                    case DEPTH:
                    case HIGHLIGHT:
                        Log.d("rcjs+video","+++++");
                        Log.d("rcjs+video","+++++"+data.title);
                        Log.d("rcjs+video",data.url +"+++++");
                        Log.d("rcjs+video",data.url +"+++++"+data.title);
                        intent = new Intent(mActivity, BaseWebActivity.class);
                        intent.putExtra(BaseWebActivity.BUNDLE_KEY_URL, data.url);
                        intent.putExtra(BaseWebActivity.BUNDLE_KEY_TITLE, data.title);
                        startActivity(intent);
                        break;
                    case BANNER:
                    case NEWS:
                    default:
                        Log.d("rcjs+url",data.url +"+++++"+data.index);
                        intent = new Intent(mActivity, NewsDetailActivity.class);
                        intent.putExtra(NewsDetailActivity.TITLE, data.title);
                        intent.putExtra(NewsDetailActivity.ARTICLE_ID, data.index);
                        startActivity(intent);
                        break;

                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new SpaceItemDecoration(DimenUtils.dpToPxInt(5)));
        materialRefreshLayout.setMaterialRefreshListener(new RefreshListener());

    }


    private void requestNews(String arcIds, final boolean isRefresh, final boolean isLoadMore) {
        TecentService.getNewsItem(newsType, arcIds, isRefresh, new RequestCallback<NewsItem>() {
            @Override
            public void onSuccess(NewsItem newsItem) {
                if (isRefresh)
                    list.clear();
                list.addAll(newsItem.data);
                complete();
            }

            @Override
            public void onFailure(String message) {
                complete();
            }
        });

    }

    private String parseIds() {
        int size = indexs.size();
        String articleIds = "";
        for (int i = start, j = 0; i < size && j < num; i++, j++, start++) {
            articleIds += indexs.get(i) + ",";
        }
        if (!TextUtils.isEmpty(articleIds))
            articleIds = articleIds.substring(0, articleIds.length() - 1);
        LogUtils.i("articleIds = " + articleIds);
        return articleIds;
    }


    private class RefreshListener extends MaterialRefreshListener {
        @Override
        public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {
            requestIndex(true);
        }

        @Override
        public void onRefreshLoadMore(final MaterialRefreshLayout materialRefreshLayout) {
            LogUtils.i("load more: start=" + start);
            String arcIds = parseIds();
            if (!TextUtils.isEmpty(arcIds)) {
                requestNews(arcIds, false, true);
            } else {
                ToastUtils.showToast("已经到底啦");
                complete();
            }
        }
    }

    private void complete() {
        recyclerView.setEmptyView(emptyView);
        adapter.notifyDataSetChanged();
        materialRefreshLayout.finishRefresh();
        materialRefreshLayout.finishRefreshLoadMore();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideLoadingDialog();
            }
        }, 1000);
    }

    @Override
    protected void onPauseLazy() {
        super.onPauseLazy();
    }

    @Override
    public void onDestroyViewLazy() {
        super.onDestroyViewLazy();
    }
}
