package davidnba.com.davidnba_ywh.ui.adapter;

import android.content.Context;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zengcanxiang.baseAdapter.absListView.HelperAdapter;
import com.zengcanxiang.baseAdapter.absListView.HelperViewHolder;

import java.util.List;

import davidnba.com.davidnba_ywh.R;
import davidnba.com.davidnba_ywh.http.bean.match.MatchStat;
import davidnba.com.davidnba_ywh.utils.FrescoUtils;

/**
 * Created by 仁昌居士 on 2017/2/20.
 */
public class MatchLMaxPlayerdapter extends HelperAdapter<MatchStat.MatchStatInfo.StatsBean.MaxPlayers> {

    public MatchLMaxPlayerdapter(List<MatchStat.MatchStatInfo.StatsBean.MaxPlayers> mList, Context context, int... layoutIds) {
        super(mList, context, layoutIds);
    }

    @Override
    public void HelpConvert(HelperViewHolder viewHolder, int position, MatchStat.MatchStatInfo.StatsBean.MaxPlayers item) {
        viewHolder.setText(R.id.tvLeftPlayerName, item.leftPlayer.name)
                .setText(R.id.tvLeftPlayerType, item.leftPlayer.position + "  #" + item.rightPlayer.jerseyNum)
                .setText(R.id.tvRightPlayerName, item.rightPlayer.name)
                .setText(R.id.tvRightPlayerType, item.rightPlayer.position + "  #" + item.rightPlayer.jerseyNum)
                .setText(R.id.tvType, item.text);

        SimpleDraweeView ivLeft = viewHolder.getView(R.id.ivLeftPlayerIcon);
        ivLeft.setController(FrescoUtils.getController(item.leftPlayer.icon, ivLeft));
        SimpleDraweeView ivRight = viewHolder.getView(R.id.ivRightPlayerIcon);
        ivRight.setController(FrescoUtils.getController(item.rightPlayer.icon, ivRight));
    }
}
