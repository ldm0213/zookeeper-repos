package com.example.zookeeper.javaapi;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

public class ZookeeperGetSetDataExample implements Watcher {
    // 利用countDownLatch进行阻塞直到连接建立
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static ZooKeeper zooKeeper = null;
    private static Stat stat = new Stat();

    public static void main(String[] args) throws Exception {
        zooKeeper = new ZooKeeper("localhost:2181",
                5000, new ZookeeperGetSetDataExample());
        System.out.println(zooKeeper.getState());
        connectedSemaphore.await();
        System.out.println("Zookeeper session establshed");

        // 创建节点
        String path = zooKeeper.create("/zk-book", "".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println("path:" + path);

        // 设置数据
        zooKeeper.setData("/zk-book", "123".getBytes(), -1);

        // 获取数据
        System.out.println(new String(
                zooKeeper.getData(path, new ZookeeperGetSetDataExample(), stat))
        );

        // 更新数据--更新成功，获取数据回调接收到消息
        zooKeeper.setData("/zk-book", "345".getBytes(), 1);

        // 更新数据--老版本更新失败
        zooKeeper.setData("/zk-book", "345".getBytes(), 1);

        Thread.sleep(Integer.MAX_VALUE);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("Receive watched event: " + watchedEvent);
        if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
            if (Event.EventType.None == watchedEvent.getType()
                    && null == watchedEvent.getPath()) {
                connectedSemaphore.countDown();
            } else if (watchedEvent.getType() == Event.EventType.NodeDataChanged) {
                try {
                    System.out.println(new String(
                            zooKeeper.getData(watchedEvent.getPath(), true, stat))
                    );
                    System.out.println(stat.getCzxid() + "," +
                            stat.getMzxid() + "," + stat.getVersion());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
