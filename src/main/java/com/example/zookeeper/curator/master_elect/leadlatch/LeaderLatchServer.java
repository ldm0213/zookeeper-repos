package com.example.zookeeper.curator.master_elect.leadlatch;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;

import java.io.Closeable;
import java.io.IOException;

public class LeaderLatchServer implements LeaderLatchListener , Closeable {
    /** server name **/
    private final String serverName;

    private LeaderLatch leaderLatch;

    LeaderLatchServer(CuratorFramework client, String path, String serverName) {
        this.serverName = serverName;
        /** 传入客户端、监听路径、监听器 */
        leaderLatch = new LeaderLatch(client, path);
        leaderLatch.addListener(this);
    }

    public void start() throws Exception {
        leaderLatch.start();
        System.out.println(serverName + " started done");
    }

    public void close() throws IOException {
        leaderLatch.close();
    }

    @Override
    public void isLeader() {
        System.out.println(serverName + " has gained leadership");
    }

    @Override
    public void notLeader() {
        System.out.println(serverName + " has lost leadership");
    }
}
