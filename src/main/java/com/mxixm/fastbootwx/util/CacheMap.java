package com.mxixm.fastbootwx.util;

import java.util.*;

/**
 * 用来存储短暂对象的缓存类，实现Map接口，内部有一个定时器用来清除过期（30秒）的对象。
 * 为避免创建过多线程，没有特殊要求请使用getDefault()方法来获取本类的实例。
 *
 */
public class CacheMap<K, V> extends AbstractMap<K, V> {

    // 12个小时
    private static final long DEFAULT_TIMEOUT = 24 * 60 * 60 * 1000;

    private static final Map<String, CacheMap> cacheNameMap = new HashMap<>();

    // 一个小时扫一次缓存
    private static final long DEFAULT_CLEAR_PERIOD = 60 * 60 * 1000;

    private static TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            cacheNameMap.values().forEach(v -> clearTimeoutCache(v));
        }
    };

    // 守护线程timer
    private static Timer timer = new Timer(true);

    static {
        timer.schedule(timerTask, DEFAULT_CLEAR_PERIOD, DEFAULT_CLEAR_PERIOD);
    }

    static class CacheEntry<K, V> implements Entry<K, V> {
        long time;
        V value;
        K key;

        CacheEntry(K key, V value) {
            super();
            this.value = value;
            this.key = key;
            this.time = System.currentTimeMillis();
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            return this.value = value;
        }
    }


    public static void clearTimeoutCache(CacheMap map) {
        long now = System.currentTimeMillis();
        Object[] keys = map.keySet().toArray();
        for (Object key : keys) {
            CacheEntry entry = (CacheEntry) map.getEntryMap().get(key);
            if (now - entry.time >= map.getCacheTimeout()) {
                synchronized (map) {
                    map.remove(key);
                }
            }
        }
    }

    private long cacheTimeout;

    private String cacheName;

    private Map<K, CacheEntry> entryMap = new HashMap<>();

    public CacheMap(String cacheName, long timeout) {
        this.cacheName = cacheName;
        this.cacheTimeout = timeout;
        cacheNameMap.put(cacheName, this);
    }

    public CacheMap(String cacheName) {
        this(cacheName, DEFAULT_TIMEOUT);
    }

    public Map<K, CacheEntry> getEntryMap() {
        return entryMap;
    }

    public long getCacheTimeout() {
        return cacheTimeout;
    }

    /**
     * remove其实是通过这个来实现的，但是这里功能明显导致删除会失败，结果会内存溢出，所以我重写了remove方法
     * @return
     */
    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> entrySet = new HashSet<>();
        Set<Entry<K, CacheEntry>> wrapEntrySet = entryMap.entrySet();
        for (Entry<K, CacheEntry> entry : wrapEntrySet) {
            entrySet.add(entry.getValue());
        }
        return entrySet;
    }

    @Override
    public V remove(Object key) {
        Entry<K, V> entry = this.entryMap.remove(key);
        if (entry == null) {
            return null;
        }
        return entry.getValue();
    }

    @Override
    public V get(Object key) {
        CacheEntry<K, V> entry = entryMap.get(key);
        return entry == null ? null : entry.value;
    }

    @Override
    public V put(K key, V value) {
        CacheEntry entry = new CacheEntry(key, value);
        synchronized (entryMap) {
            entryMap.put(key, entry);
        }
        return value;
    }

}


