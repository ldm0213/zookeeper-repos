package com.example.zookeeper.javaapi;

import org.apache.zookeeper.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ZookeeperGetChildrenExample implements Watcher {
    // 利用countDownLatch进行阻塞直到连接建立
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static ZooKeeper zooKeeper = null;

    public static void main(String[] args) throws Exception {
        // zookeeper构造
        zooKeeper = new ZooKeeper("localhost:2181",
                5000, new ZookeeperGetChildrenExample());
        System.out.println(zooKeeper.getState());

        connectedSemaphore.await();
        System.out.println("Zookeeper session establshed");

        // 同步接口获取子节点，先创建节点，并注册NodeChildrenChanged事件监听
        String path = "/zk-book";
        zooKeeper.create(path, "".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        zooKeeper.create(path + "/c1", "".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        // 同步获取子节点
        List<String> childrens = zooKeeper.getChildren(path, true);
        System.out.println(childrens);

        // 再次创建节点，事件监听会触发，然后可以自定义结果处理，这里重新获取了节点list
        zooKeeper.create(path + "/c2", "".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        Thread.sleep(2000);

        // 异步接口获取
        zooKeeper.getChildren(path, true, new IChildren2Callback(), null);
        Thread.sleep(Integer.MAX_VALUE);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("Receive watched event: " + watchedEvent);
        if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
            if (Event.EventType.None == watchedEvent.getType() && null == watchedEvent.getPath()) {
                connectedSemaphore.countDown();
            } else if (watchedEvent.getType() == Event.EventType.NodeChildrenChanged) {
                try {
                    // NodeChildrenChanged事件是一次性的，需要重新注册,这里先不注册了
                    System.out.println("reget children:" +
                            zooKeeper.getChildren(watchedEvent.getPath(), false));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}
