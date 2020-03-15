package com.example.zookeeper.curator.api;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

public class CuratorZKGetAndUpdateDate {
    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 2);
        // fluent风格创建client
        CuratorFramework fluentClient = CuratorFrameworkFactory
                .builder()
                .connectString("localhost:2181")
                .sessionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .build();
        fluentClient.start();

        // fluent风格创建节点，返回只是节点path
        String result = fluentClient.create().
                withMode(CreateMode.EPHEMERAL).
                forPath("/zk-test", "init".getBytes());
        System.out.println(result);

        // get data
        Stat stat = new Stat();
        String data = new String(fluentClient.
                getData().
                storingStatIn(stat).
                forPath("/zk-test"));
        System.out.println("data:" + data + ";version=" + stat.getVersion());

        // set data
        fluentClient.setData().
                withVersion(stat.getVersion()).
                forPath("/zk-test", "update".getBytes());

        System.out.println(new String(fluentClient.getData().forPath("/zk-test")));

        fluentClient.setData().
                withVersion(stat.getVersion()).
                forPath("/zk-test", "update-again".getBytes());

        Thread.sleep(Integer.MAX_VALUE);
    }
}
