package com.sanron.yidumusic.data.db.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Index;
import com.raizlabs.android.dbflow.annotation.Table;
import com.sanron.yidumusic.data.db.YiduDB;

/**
 * Created by sanron on 16-7-19.
 */
@Table(
        name = "cache",
        database = YiduDB.class
)
public class Cache extends Base {

    @Index
    @Column
     String key;
    @Column
     String value;
    @Column
     long addTime;
    @Column
     long maxAge;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public long getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(long maxAge) {
        this.maxAge = maxAge;
    }
}
