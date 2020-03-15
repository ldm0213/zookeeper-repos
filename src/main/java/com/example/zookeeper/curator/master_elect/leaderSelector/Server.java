package com.example.zookeeper.curator.master_elect.leaderSelector;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;

import java.io.Closeable;

public class Server extends LeaderSelectorListenerAdapter implements Closeable {
    /** server name **/
    private final String serverName;

    /** 监听器 */
    private final LeaderSelector leaderSelector;

    /** takeLeadership方法中设置线程阻塞多长时间，单位ms */
    private final int SLEEP_MILLISECOND = 100000;

    public Server(CuratorFramework client, String path, String serverName) {
        this.serverName = serverName;
        /** 传入客户端、监听路径、监听器 */
        leaderSelector = new LeaderSelector(client, path, this);
        leaderSelector.autoRequeue();
    }

    public void start() throws InterruptedException {
        leaderSelector.start();
        System.out.println(getServerName() + "开始运行了！");
        Thread.sleep(1000);
    }

    @Override
    public void close() {
        leaderSelector.close();
        System.out.println(getServerName() + "释放资源了！");
    }

    @Override
    public void takeLeadership(CuratorFramework client) {
        try {
            System.out.println(getServerName() + "是Master, 执行到takeLeadership()方法了！");
            Thread.sleep(SLEEP_MILLISECOND);
        } catch (InterruptedException e) {
            System.err.println(getServerName() + " was interrupted!");
            Thread.currentThread().interrupt();
        }
    }

    public String getServerName() {
        return serverName;
    }

    public LeaderSelector getLeaderSelector() {
        return leaderSelector;
    }
}
