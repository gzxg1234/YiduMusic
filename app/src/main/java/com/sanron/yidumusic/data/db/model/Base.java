package com.sanron.yidumusic.data.db.model;

import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;

/**
 * Created by sanron on 16-7-18.
 */
public class Base extends BaseModel implements Serializable{

    @PrimaryKey(autoincrement = true)
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
