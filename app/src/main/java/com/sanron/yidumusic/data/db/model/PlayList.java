package com.sanron.yidumusic.data.db.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.sanron.yidumusic.data.db.YiduDB;

import java.util.List;

/**
 * 播放列表
 * Created by Administrator on 2015/12/21.
 */
@Table(
        name = "play_list",
        database = YiduDB.class
)
public class PlayList extends Base {

    public static final int TYPE_FAVORITE = 1;
    public static final int TYPE_USER = 2;
    public static final int TYPE_ALBUM = 3;
    public static final int TYPE_GEDAN = 4;
    public static final int TYPE_OFFICIAL_GEDAN = 5;

    @Column
    private String name;

    @Column
    private int type;

    @Column
    private long addTime;

    @Column
    private String icon;

    @Column
    private String code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}

