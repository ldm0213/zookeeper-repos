package com.example.zookeeper.curator.api;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

public class CuratorZKCreateAndDeleteNode {
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

        // 删除
        fluentClient.delete().
                guaranteed().
                withVersion(0).
                forPath("/zk-test");

        Thread.sleep(Integer.MAX_VALUE);
    }
}
