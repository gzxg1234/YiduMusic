package com.sanron.yidumusic.ui.vo;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.sanron.yidumusic.data.db.model.PlayList;
import com.sanron.yidumusic.data.db.model.PlayListMembers;
import com.sanron.yidumusic.data.db.model.PlayListMembers_Table;

/**
 * Created by sanron on 16-7-23.
 */
public class PlayListVO extends PlayList {

    private int musicCount;

    public int getMusicCount() {
        return musicCount;
    }

    public void setMusicCount(int musicCount) {
        this.musicCount = musicCount;
    }


    public static PlayListVO from(PlayList playList) {
        PlayListVO playListVO = new PlayListVO();
        playListVO.setAddTime(playList.getAddTime());
        playListVO.setName(playList.getName());
        playListVO.setType(playList.getType());
        playListVO.setCode(playList.getCode());
        playListVO.setId(playList.getId());
        playListVO.setIcon(playList.getIcon());
        playListVO.setMusicCount((int) SQLite.selectCountOf()
                .from(PlayListMembers.class)
                .where(PlayListMembers_Table.playList_id.eq(playList.getId()))
                .count());
        return playListVO;
    }
}
