package com.example.zookeeper.javaapi;

import org.apache.zookeeper.*;
import java.util.concurrent.CountDownLatch;

public class ZookeeperExistsExample implements Watcher {
    // 利用countDownLatch进行阻塞直到连接建立
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static ZooKeeper zooKeeper = null;

    public static void main(String[] args) throws Exception {
        // zookeeper构造
        zooKeeper = new ZooKeeper("localhost:2181",
                5000, new ZookeeperExistsExample());
        System.out.println(zooKeeper.getState());

        connectedSemaphore.await();
        System.out.println("Zookeeper session establshed");

        // 在path上检测是否存在事件
        String path = "/zk-book";
        zooKeeper.exists(path, true);

        // 同步接口获取子节点，先创建节点，并注册NodeChildrenChanged事件监听
        zooKeeper.create(path, "".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        // 更新数据
        zooKeeper.setData(path, "123".getBytes(), -1);

        // 删除节点
        zooKeeper.delete(path, -1);

        Thread.sleep(2000);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("Receive watched event: " + watchedEvent);
        try {
            if (Watcher.Event.KeeperState.SyncConnected == watchedEvent.getState()) {
                if (Watcher.Event.EventType.None == watchedEvent.getType() && null == watchedEvent.getPath()) {
                    connectedSemaphore.countDown();
                } else if (watchedEvent.getType() == Event.EventType.NodeCreated) {
                    System.out.println("Node: " + watchedEvent.getPath() + " Create");
                    zooKeeper.exists(watchedEvent.getPath(), true);
                } else if (watchedEvent.getType() == Event.EventType.NodeDataChanged) {
                    System.out.println("Node: " + watchedEvent.getPath() + " data change");
                    zooKeeper.exists(watchedEvent.getPath(), true);
                } else if (watchedEvent.getType() == Event.EventType.NodeDeleted) {
                    System.out.println("Node: " + watchedEvent.getPath() + " delete");
                    zooKeeper.exists(watchedEvent.getPath(), true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
