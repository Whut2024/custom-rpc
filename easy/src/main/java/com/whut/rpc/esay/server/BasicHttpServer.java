package com.whut.rpc.esay.server;

/**
 * define an interface, developer can implement it to start a web sever
 */
public interface BasicHttpServer {

    void start(int port);
}
