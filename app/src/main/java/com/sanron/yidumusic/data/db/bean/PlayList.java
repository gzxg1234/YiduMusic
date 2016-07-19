package com.sanron.yidumusic.data.db.bean;

/**
 * 播放列表
 * Created by Administrator on 2015/12/21.
 */
public class PlayList extends Base {

    public static final int TYPE_USER = 1;
    public static final int TYPE_FAVORITE = 2;
    public static final int TYPE_ALBUM = 3;
    public static final int TYPE_GEDAN = 4;
    public static final int TYPE_OFFICIAL_GEDAN = 5;

    public static final String TABLE = "play_list";
    public static final String COL_NAME = "name";
    public static final String COL_TYPE = "type";
    public static final String COL_ADD_TIME = "add_time";
    public static final String COL_ICON = "icon";
    public static final String COL_CODE = "code";

    private String name;

    private int type;

    private long addTime;

    private String icon;

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
