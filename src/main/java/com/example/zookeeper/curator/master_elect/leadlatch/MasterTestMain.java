package com.example.zookeeper.curator.master_elect.leadlatch;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.utils.CloseableUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MasterTestMain {

    /** 重试策略:重试间隔时间为1000ms; 最多重试3次; */
    private static RetryPolicy retryPolicy = new RetryNTimes(3, 1000);

    public static void main(String[] args) throws Exception {
        List<CuratorFramework> clients = new ArrayList<CuratorFramework>(16);

        List<LeaderLatchServer> workServers = new ArrayList<LeaderLatchServer>(16);

        String name = "server-";

        try {
            // 模拟是个server，进行争抢master
            for (int i = 0; i < 4; i++) {
                CuratorFramework client = CuratorFrameworkFactory.builder().
                        connectString("localhost:2181").
                        sessionTimeoutMs(5000).
                        connectionTimeoutMs(5000).
                        retryPolicy(retryPolicy).
                        build();
                clients.add(client);
                LeaderLatchServer server = new LeaderLatchServer(client, "/zk-master", name + i);
                workServers.add(server);
                client.start();
                server.start();
            }
        } finally {
            System.out.println("开始关闭！");
            LeaderLatchServer server;
            // 关闭server会进行master切换
            for (int i = 0; i < workServers.size(); i++) {
                server = workServers.get(i);
                CloseableUtils.closeQuietly(server);
                // 为方便观察，这里阻塞几秒
                TimeUnit.SECONDS.sleep(3);
            }
            for (CuratorFramework client : clients) {
                CloseableUtils.closeQuietly(client);
            }
        }
    }
}
