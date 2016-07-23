package com.sanron.yidumusic.data.db;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.sanron.yidumusic.data.db.model.Cache;
import com.sanron.yidumusic.data.db.model.Cache_Table;
import com.sanron.yidumusic.util.MD5Util;

public class HttpCache {

    public String get(String url) {
        String key = generateKey(url);
        String value = null;
        Cache cache = SQLite.select()
                .from(Cache.class)
                .where(Cache_Table.key.eq(key))
                .querySingle();
        if (cache != null) {
            long curTime = System.currentTimeMillis();
            if ((cache.getAddTime() + cache.getMaxAge()) > curTime) {
                value = cache.getValue();
            } else {
                cache.delete();
            }
        }
        return value;
    }

    public static String generateKey(String url) {
        return MD5Util.toMd5(url.getBytes(), true);
    }

    public void remove(String url) {
        String key = generateKey(url);
        Cache cache = SQLite.select()
                .from(Cache.class)
                .where(Cache_Table.key.eq(key))
                .querySingle();
        if (cache != null) {
            cache.delete();
        }
    }

    public void put(String url, String value, long maxAge) {
        String key = generateKey(url);
        Cache cache = SQLite.select()
                .from(Cache.class)
                .where(Cache_Table.key.eq(key))
                .querySingle();
        if (cache == null) {
            cache = new Cache();
            cache.setKey(key);
            cache.setValue(value);
            cache.setAddTime(System.currentTimeMillis());
            cache.setMaxAge(maxAge);
            cache.save();
        } else {
            cache.setValue(value);
            cache.setAddTime(System.currentTimeMillis());
            cache.setMaxAge(maxAge);
            cache.save();
        }
    }

    public void clearCache() {
        SQLite.delete(Cache.class)
                .execute();
    }
}
