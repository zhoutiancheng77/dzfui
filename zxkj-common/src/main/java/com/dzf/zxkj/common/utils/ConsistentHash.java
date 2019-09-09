package com.dzf.zxkj.common.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.*;


/**
 * 一致性Hash算法
 * 算法详解：http://blog.csdn.net/sparkliang/article/details/5279393
 * 算法实现：https://weblogs.java.net/blog/2007/11/27/consistent-hashing
 *
 * @param <T> 节点类型
 * @author xiaoleilu
 */
public class ConsistentHash<T> {
    /**
     * Hash计算对象，用于自定义hash算法
     */
    private HashFunc hashFunc;

    public HashFunc getHashFunc() {
        return hashFunc;
    }


    /**
     * 复制的节点个数
     */
    private final int numberOfReplicas;
    /**
     * 一致性Hash环
     */
    private final SortedMap<Long, T> circle = new TreeMap<Long, T>();

    /**
     * 构造，使用Java默认的Hash算法
     *
     * @param numberOfReplicas 复制的节点个数，增加每个节点的复制节点有利于负载均衡
     * @param nodes            节点对象
     */
    public ConsistentHash(int numberOfReplicas, Collection<T> nodes) {
        this.numberOfReplicas = numberOfReplicas;
        this.hashFunc = new HashFunc() {


            public long hash(Object key) {
//				String data = key.toString();
//				//默认使用FNV1hash算法
//				final int p = 16777619;
//				int hash = (int) 2166136261L;
//				for (int i = 0; i < data.length(); i++)
//					hash = (hash ^ data.charAt(i)) * p;
//				hash += hash << 13;
//				hash ^= hash >> 7;
//				hash += hash << 3;
//				hash ^= hash >> 17;
//				hash += hash << 5;
//				return hash;

                byte[] data = DigestUtils.md5(key.toString().getBytes());
                return (data[0] | ((long) data[1] << 8) | ((long) data[2] << 16)
                        | ((long) data[3] << 24) | ((long) data[4] << 32)
                        | ((long) data[5] << 40) | ((long) data[6] << 48)
                        | ((long) data[7] << 56));
            }
        };
        //初始化节点
        for (T node : nodes) {
            add(node);
        }
    }

    /**
     * 构造
     *
     * @param hashFunc         hash算法对象
     * @param numberOfReplicas 复制的节点个数，增加每个节点的复制节点有利于负载均衡
     * @param nodes            节点对象
     */
    public ConsistentHash(HashFunc hashFunc, int numberOfReplicas, Collection<T> nodes) {
        this.numberOfReplicas = numberOfReplicas;
        this.hashFunc = hashFunc;
        //初始化节点
        for (T node : nodes) {
            add(node);
        }
    }

    /**
     * 增加节点<br>
     * 每增加一个节点，就会在闭环上增加给定复制节点数<br>
     * 例如复制节点数是2，则每调用此方法一次，增加两个虚拟节点，这两个节点指向同一Node
     * 由于hash算法会调用node的toString方法，故按照toString去重
     *
     * @param node 节点对象
     */
    public void add(T node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            circle.put(hashFunc.hash(node.toString() + i), node);
        }
    }

    /**
     * 移除节点的同时移除相应的虚拟节点
     *
     * @param node 节点对象
     */
    public void remove(T node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            circle.remove(hashFunc.hash(node.toString() + i));
        }
    }

    /**
     * 获得一个最近的顺时针节点
     *
     * @param key 为给定键取Hash，取得顺时针方向上最近的一个虚拟节点对应的实际节点
     * @return 节点对象
     */
    public T get(Object key) {
        if (circle.isEmpty()) {
            return null;
        }
        long hash = hashFunc.hash(key);
        if (!circle.containsKey(hash)) {
            SortedMap<Long, T> tailMap = circle.tailMap(hash);    //返回此映射的部分视图，其键大于等于 hash
            hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
        }
        //正好命中
        return circle.get(hash);
    }

    public T getByData(Object obj) {
        if (circle.isEmpty()) {
            return null;
        }
        Object key = getHashFunc().hash(obj);
        long hash = hashFunc.hash(key);
        if (!circle.containsKey(hash)) {
            SortedMap<Long, T> tailMap = circle.tailMap(hash);    //返回此映射的部分视图，其键大于等于 hash
            hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
        }
        //正好命中
        return circle.get(hash);
    }

    /**
     * Hash算法对象，用于自定义hash算法
     *
     * @author xiaoleilu
     */
    public interface HashFunc {
        public long hash(Object key);
    }

    public static void main(String[] args) {

        String[] servers = new String[]{"Server 1:192.168.1.1",
                "Server 2:192.168.1.2", "Server 3:192.168.1.3",
                "Server 4:192.168.1.4", "Server 5:192.168.1.5"};
        List<String> list = new ArrayList<String>();
        list.add(servers[0]);
        list.add(servers[1]);
        list.add(servers[2]);
        list.add(servers[3]);
        list.add(servers[4]);
        ConsistentHash<String> consHash = new ConsistentHash<String>(100, list);
        //System.out.println("服务器映射信息：");
        consHash.printServerMapOrder();
        //System.out.println("数据映射信息：");
        showDataMap(consHash);
        // 移除server2
        consHash.remove(servers[2]);
        //System.out.println("移除server 3后数据映射信息：");
        showDataMap(consHash);

    }

    public void printServerMapOrder() {
        //System.out.println(circle);
    }

    public static void showDataMap(ConsistentHash<String> consHash) {
        for (int i = 0; i < 50; i++) {
            //System.out.println("Data" + i + " mapped at "
            //		+ consHash.getByData("Data"+i));
        }

    }

}
