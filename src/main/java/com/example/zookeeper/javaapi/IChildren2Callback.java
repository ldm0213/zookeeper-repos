package com.example.zookeeper.javaapi;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.data.Stat;

import java.util.List;

public class IChildren2Callback implements AsyncCallback.Children2Callback {

    @Override
    public void processResult(int rc, String path, Object ctx,
                              List<String> children, Stat stat) {
        System.out.println("Get children  znode result:[ rc=" + rc +
                ";path=" + path + ";ctx=" + ctx + ";children:" + children + ";stat=" + stat);
    }
}
