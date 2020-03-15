package com.example.zookeeper.javaapi;

import org.apache.zookeeper.AsyncCallback;

public class IStringCallback implements AsyncCallback.StringCallback {

    @Override
    public void processResult(int rc, String path, Object ctx, String name) {
        System.out.println("Create path result: [code=" + rc + "; path=" +
                path + ";ctx=" + ctx + ";real path name =" + name);
    }
}
