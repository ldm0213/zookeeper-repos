package com.example.zookeeper.javaapi;

import org.apache.zookeeper.*;

import java.util.concurrent.CountDownLatch;

public class ZookeeperCreateNodeExample implements Watcher {
    // 利用countDownLatch进行阻塞直到连接建立
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {
        ZooKeeper zooKeeper = new ZooKeeper("localhost:2181",
                5000, new ZookeeperCreateNodeExample());
        System.out.println(zooKeeper.getState());

        connectedSemaphore.await();
        System.out.println("Zookeeper session establshed");
        // 同步创建
        syncCreate(zooKeeper);

        // 异步创建
        asyCreate(zooKeeper);
        Thread.sleep(Integer.MAX_VALUE);
    }

    public static void syncCreate(ZooKeeper zooKeeper) {
        try{
            String path1 = zooKeeper.create("/zk-test-create", "".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            System.out.println("path1:" + path1);

            // 顺序临时节点
            String path2 = zooKeeper.create("/zk-test-create", "".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            System.out.println("path2:" + path2);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void asyCreate(ZooKeeper zooKeeper) {
        zooKeeper.create("/zk-test-create", "".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,
                new IStringCallback(), "I am context");

        // 顺序临时节点
        zooKeeper.create("/zk-test-create", "".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL,
                new IStringCallback(), "I am context");
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("Receive watched event: " + watchedEvent);
        if (Watcher.Event.KeeperState.SyncConnected == watchedEvent.getState()) {
            connectedSemaphore.countDown();
        }
    }
}
