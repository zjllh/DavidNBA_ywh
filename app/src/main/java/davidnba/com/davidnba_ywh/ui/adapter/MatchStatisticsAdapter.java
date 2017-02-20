package davidnba.com.davidnba_ywh.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.zengcanxiang.baseAdapter.absListView.HelperAdapter;
import com.zengcanxiang.baseAdapter.absListView.HelperViewHolder;

import java.util.List;

import davidnba.com.davidnba_ywh.R;
import davidnba.com.davidnba_ywh.http.bean.match.MatchStat;

/**
 * Created by 仁昌居士 on 2017/2/20.
 */

public class MatchStatisticsAdapter extends HelperAdapter<MatchStat.MatchStatInfo.StatsBean.TeamStats> {
    private Context mContext;
    private int colorPrimary;

    public MatchStatisticsAdapter(List<MatchStat.MatchStatInfo.StatsBean.TeamStats> mList, Context context, int... layoutIds) {
        super(mList, context, layoutIds);
        mContext = context;
        colorPrimary = mContext.getResources().getColor(R.color.colorPrimary);
    }

    @Override
    public void HelpConvert(HelperViewHolder viewHolder, final int position, final MatchStat.MatchStatInfo.StatsBean.TeamStats item) {
        viewHolder.setText(R.id.tvLeftVal, item.leftVal + "")
                .setText(R.id.tvRightVal, item.rightVal + "")
                .setText(R.id.tvStatisticsName, item.text);

        final LinearLayout llLeftProgress = viewHolder.getView(R.id.llLeftProgress);
        final LinearLayout llRightProgress = viewHolder.getView(R.id.llRightProgress);
        int sum = item.leftVal + item.rightVal;
        final float left = sum <= 0 ? 0 : (float) item.leftVal / (float) sum;
        final float right = sum <= 0 ? 0 : (float) item.rightVal / (float) sum;

        /*view变化监听器
        ViewTreeObserver是不能被应用程序实例化的,因为它是由视图提供的,通过view.getViewTreeObserver()获取。*/
        ViewTreeObserver vto = llRightProgress.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //务必移除监听，会多次调用
                if (Build.VERSION.SDK_INT < 16) {
                    llRightProgress.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    llRightProgress.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                View leftLine = new View(mContext);
                leftLine.setBackgroundColor(colorPrimary);
                int leftWidth = llLeftProgress.getWidth();
                int newWidth = (int) (leftWidth * left);
                LinearLayout.LayoutParams leftParams = new LinearLayout.LayoutParams(newWidth, ViewGroup.LayoutParams.MATCH_PARENT);
                leftLine.setLayoutParams(leftParams);
                llLeftProgress.setGravity(Gravity.RIGHT);
                llLeftProgress.addView(leftLine);

                View rightLine = new View(mContext);
                rightLine.setBackgroundColor(colorPrimary);
                int rightWidth = llRightProgress.getWidth();
                LinearLayout.LayoutParams rightParams = new LinearLayout.LayoutParams((int) (rightWidth * right), ViewGroup.LayoutParams.MATCH_PARENT);
                rightLine.setLayoutParams(rightParams);
                llRightProgress.setGravity(Gravity.LEFT);
                llRightProgress.addView(rightLine);

                if (position == 0) { // 没明白为什么，第一条数据的param总是不生效，移除重新添加就可以...
                    llLeftProgress.removeAllViews();
                    llLeftProgress.addView(leftLine);
                    llRightProgress.removeAllViews();
                    llRightProgress.addView(rightLine);
                }

                if (item.leftVal > item.rightVal) {
                    rightLine.setBackgroundColor(Color.GRAY);
                } else if (item.leftVal < item.rightVal) {
                    leftLine.setBackgroundColor(Color.GRAY);
                }

            }
        });
    }


}
