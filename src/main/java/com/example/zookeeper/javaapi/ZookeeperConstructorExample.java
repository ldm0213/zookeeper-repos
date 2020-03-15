package com.example.zookeeper.javaapi;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

public class ZookeeperConstructorExample implements Watcher {

    // 利用countDownLatch进行阻塞直到连接建立
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {
        ZooKeeper zooKeeper = new ZooKeeper("localhost:2181",
                5000, new ZookeeperConstructorExample());
        System.out.println(zooKeeper.getState());

        connectedSemaphore.await();
        System.out.println("Zookeeper session establshed");

        // 利用sessionId和sessionPasswd恢复连接
        long sessionId = zooKeeper.getSessionId();
        byte[] sessionPasswd = zooKeeper.getSessionPasswd();
        zooKeeper = new ZooKeeper("localhost:2181",
                5000, new ZookeeperConstructorExample(),
                1L, "test".getBytes());

        zooKeeper = new ZooKeeper("localhost:2181",
                5000, new ZookeeperConstructorExample(),
                sessionId, sessionPasswd);

        Thread.sleep(Integer.MAX_VALUE);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("Receive watched event: " + watchedEvent);
        if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
            connectedSemaphore.countDown();
        }
    }
}
