package com.dzf.zxkj.base.framework.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class SyncMapDecorator<K, V> implements Map<K, V> {

    Map<K, V> map = null;

    ReadWriteLock lock;

    public SyncMapDecorator(Map<K, V> map) {
        this.map = map;
        lock = new ReadWriteLock();
    }

    public void clear() {
        lock.writeLock();
        try {
            map.clear();
        } finally {
            lock.writeUnLock();
        }

    }

    public boolean containsKey(Object key) {
        lock.readLock();

        try {
            return map.containsKey(key);
        } finally {
            lock.readUnLock();
        }

    }

    public boolean containsValue(Object value) {

        lock.readLock();
        try {
            return map.containsValue(value);
        } finally {
            lock.readUnLock();
        }

    }

    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    public boolean equals(Object o) {
        return map.equals(o);
    }

    public V get(Object key) {
        lock.readLock();
        try {
            return map.get(key);
        } finally {
            lock.readUnLock();
        }

    }

    public int hashCode() {
        return map.hashCode();
    }

    public boolean isEmpty() {
        lock.readLock();
        try {
            return map.isEmpty();
        } finally {
            lock.readUnLock();
        }
    }

    public Set<K> keySet() {
        lock.readLock();
        try {
            return map.keySet();
        } finally {
            lock.readUnLock();
        }
    }

    public V put(K key, V value) {
        lock.writeLock();
        try {
            return map.put(key, value);
        } finally {
            lock.writeUnLock();
        }

    }

    public void putAll(Map<? extends K, ? extends V> t) {
        lock.writeLock();
        try {
            map.putAll(t);
        } finally {
            lock.writeUnLock();
        }

    }

    public V remove(Object key) {
        lock.writeLock();
        try {
            return map.remove(key);
        } finally {
            lock.writeUnLock();
        }

    }

    public int size() {
        lock.readLock();
        try {
            return map.size();
        } finally {
            lock.readUnLock();
        }
    }

    public Collection<V> values() {
        lock.readLock();
        try {
            return map.values();
        } finally {
            lock.readUnLock();
        }
    }

}
