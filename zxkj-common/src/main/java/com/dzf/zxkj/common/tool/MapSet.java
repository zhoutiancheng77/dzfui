package com.dzf.zxkj.common.tool;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * MapSet维护了一个key对一个Set的映射关系。每次放入一个键值对时， 会将值放在该键对应的set中，如果列表不存
 * 在则创建set
 * 
 * @param <K> 键的类型
 * @param <V> 值的类型
 */
public class MapSet<K, V> implements Serializable {
  private static final long serialVersionUID = 3235650078973528702L;

  /**
   * 存放key和set映射关系的数据集合
   */
  private Map<K, Set<V>> map = new HashMap<K, Set<V>>();

  /**
   * 是否包含当前键
   * 
   * @param key 键
   * @return 当前MapList包含此键时，返回true
   */
  public boolean containsKey(K key) {
    return this.map.containsKey(key);
  }

  /**
   * 获取当前MapList的视图，用来快速访问里面存储的元素
   * 
   * @return 当前MapList的视图
   */
  public Set<Entry<K, Set<V>>> entrySet() {
    return this.map.entrySet();
  }

  /**
   * 根据键获取set
   * 
   * @param key 键
   * @return 键对应的set
   */
  public Set<V> get(K key) {
    return this.map.get(key);
  }

  /**
   * 获取键的集合
   * 
   * @return 键的集合
   */
  public Set<K> keySet() {
    return this.map.keySet();
  }

  /**
   * 加入一个键值对。当前键不存在时，会自动创建set。否则，将值加入到对应的set中
   * 
   * @param key 键
   * @param value 值
   */
  public void put(K key, V value) {
    Set<V> l = this.map.get(key);
    if (l == null) {
      l = new HashSet<V>();
    }
    l.add(value);
    this.map.put(key, l);
  }

  /**
   * 根据键移除值
   * 
   * @param key 键
   * @return 键对应的set
   */
  public Set<V> remove(K key) {
    return this.map.remove(key);
  }

  /**
   * 得到键的数量
   * 
   * @return 当前MapList的大小
   */
  public int size() {
    return this.map.size();
  }

  /**
   * 转化为原始的Map对象
   * 
   * @return JDK基本数据结构形式
   */
  public Map<K, Set<V>> toMap() {
    return this.map;
  }
}
