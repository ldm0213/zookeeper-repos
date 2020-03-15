package com.example.zookeeper.curator.api;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class CuratorZKClient {
    public static void main(String[] args) throws InterruptedException {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 2);
        // fluent风格创建client
        CuratorFramework fluentClient = CuratorFrameworkFactory
                .builder()
                .connectString("localhost:2181")
                .sessionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .build();
        fluentClient.start();
        System.out.println(fluentClient.getState());

        // 非fluent风格创建client
        CuratorFramework client = CuratorFrameworkFactory.
                newClient("localhost:2181", 5000, 5000, retryPolicy);
        client.start();

        System.out.println(client.getState());

        Thread.sleep(Integer.MAX_VALUE);
    }
}
