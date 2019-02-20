/*
 * Copyright (c) 2016-2017, Guangshan (guangshan1992@qq.com) and the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mxixm.fastboot.weixin.util;

import java.util.*;
import java.util.concurrent.*;

/**
 * FastBootWeixin CacheMap
 * 用来存储短暂对象的缓存类，实现Map接口，内部有一个定时器用来清除过期（30秒）的对象。
 * 扩展了两个功能
 *
 * @author Guangshan
 * @date 2017/8/5 21:50
 * @since 0.1.2
 */
public class CacheMap<K, V> extends AbstractMap<K, V> {

    /**
     * 12个小时
     */
    private static final long DEFAULT_TIMEOUT = 24 * 60 * 60 * 1000;

    private static final Map<String, CacheMap> cacheNameMap = new ConcurrentHashMap<>();

    /**
     * 十分钟扫一次缓存
     */
    private static final long DEFAULT_CLEAR_PERIOD = 10 * 60 * 1000;

    /**
     * 守护线程timer
     */
    private static ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1, Executors.defaultThreadFactory());

    static {
        // 清理线程可优化为多个
        executor.scheduleAtFixedRate(
                () -> cacheNameMap.values().forEach(v -> clearTimeoutCache(v)), DEFAULT_CLEAR_PERIOD, DEFAULT_CLEAR_PERIOD, TimeUnit.MILLISECONDS);
    }

    public static <K, V> Builder<K, V> builder() {
        return new Builder<>();
    }

    static class CacheEntry<K, V> implements Entry<K, V> {
        long time;
        K key;
        V value;

        CacheEntry(K key, V value) {
            super();
            this.key = key;
            this.value = value;
            this.time = System.currentTimeMillis();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof CacheEntry)) {
                return false;
            }

            CacheEntry<?, ?> that = (CacheEntry<?, ?>) o;

            if (time != that.time) {
                return false;
            }
            if (getKey() != null ? !getKey().equals(that.getKey()) : that.getKey() != null) {
                return false;
            }
            return getValue() != null ? getValue().equals(that.getValue()) : that.getValue() == null;
        }

        @Override
        public int hashCode() {
            int result = (int) (time ^ (time >>> 32));
            result = 31 * result + (getKey() != null ? getKey().hashCode() : 0);
            result = 31 * result + (getValue() != null ? getValue().hashCode() : 0);
            return result;
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
                map.remove(key);
            }
        }
    }

    /**
     * 清理超过限制的缓存
     *
     * @param map
     */
    public static void clearOverflowCache(CacheMap map) {
        int maxSize = map.maxSize;
        int realMaxSize = maxSize + maxSize / 4;
        if (map.size() > realMaxSize) {
            int needRemove = map.size() - maxSize;
            TreeSet<Entry> sortedSet = map.entrySet();
            Entry e;
            while ((e = sortedSet.pollFirst()) != null && needRemove-- > 0) {
                map.remove(e.getKey());
            }
        }
    }

    private long cacheTimeout;

    private Map<K, CacheEntry> entryMap = new ConcurrentHashMap<>();

    private String cacheName;

    /**
     * 在读取的时候是否刷新时间
     */
    private boolean refreshOnRead;

    /**
     * 0的时候是无限，最大空间，当然允许超过最大空间，但是只能超过四分之一的量，这样做是为了保证性能
     */
    private int maxSize;

    public CacheMap(String cacheName, long timeout, boolean refreshOnRead, int maxSize) {
        this.cacheName = cacheName;
        this.cacheTimeout = timeout;
        this.refreshOnRead = refreshOnRead;
        this.maxSize = maxSize;
        cacheNameMap.put(cacheName, this);
    }

    public CacheMap(String cacheName, long timeout, boolean refreshOnRead) {
        this(cacheName, timeout, refreshOnRead, 0);
    }

    public CacheMap(String cacheName, long timeout) {
        this(cacheName, timeout, false);
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
     * 这里的entrySet实现可能是有点问题的，因为返回的是一个新的实例。
     *
     * @return the result
     */
    @Override
    public TreeSet<Entry<K, V>> entrySet() {
        TreeSet<Entry<K, V>> entrySet = new TreeSet<>(Comparator.comparing(kvEntry -> ((CacheEntry) kvEntry).time));
        Set<Entry<K, CacheEntry>> wrapEntrySet = entryMap.entrySet();
        for (Entry<K, CacheEntry> entry : wrapEntrySet) {
            entrySet.add(entry.getValue());
        }
        return entrySet;
    }

    @Override
    public int size() {
        return entryMap.size();
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
        if (entry == null) {
            return null;
        }
        if (refreshOnRead) {
            entry.time = System.currentTimeMillis();
        }
        return entry == null ? null : entry.value;
    }

    @Override
    public V put(K key, V value) {
        // 如果超量，需要清理
        if (maxSize > 0 && this.size() > maxSize + maxSize / 4) {
            clearOverflowCache(this);
        }
        CacheEntry entry = new CacheEntry(key, value);
        entryMap.put(key, entry);
        return value;
    }

    public static class Builder<K, V> {
        private long cacheTimeout;
        private Map<K, CacheEntry> entryMap;
        private String cacheName;
        private boolean refreshOnRead;
        private int maxSize;

        Builder() {
        }

        public CacheMap.Builder<K, V> cacheTimeout(long cacheTimeout) {
            this.cacheTimeout = cacheTimeout;
            return this;
        }

        public CacheMap.Builder<K, V> cacheName(String cacheName) {
            this.cacheName = cacheName;
            return this;
        }

        public CacheMap.Builder<K, V> refreshOnRead() {
            this.refreshOnRead = true;
            return this;
        }

        public CacheMap.Builder<K, V> maxSize(int maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        public CacheMap<K, V> build() {
            return new CacheMap<>(cacheName, cacheTimeout, refreshOnRead, maxSize);
        }

        @Override
        public String toString() {
            return "com.mxixm.fastboot.weixin.util.CacheMap.CacheMapBuilder(cacheTimeout=" + this.cacheTimeout + ", entryMap=" + this.entryMap + ", cacheName=" + this.cacheName + ", refreshOnRead=" + this.refreshOnRead + ", maxSize=" + this.maxSize + ")";
        }
    }

}


