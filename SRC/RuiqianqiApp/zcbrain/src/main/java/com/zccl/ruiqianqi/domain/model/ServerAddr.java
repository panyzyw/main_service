package com.zccl.ruiqianqi.domain.model;

/**
 * Created by ruiqianqi on 2017/4/12 0012.
 */

public class ServerAddr {
    private String httpRequest;
    private String httpResource;
    private String tcpRequest;
    private String tcpPort;
    private String flagVersion;

    public String getHttpRequest() {
        return httpRequest;
    }

    public void setHttpRequest(String httpRequest) {
        this.httpRequest = httpRequest;
    }

    public String getHttpResource() {
        return httpResource;
    }

    public void setHttpResource(String httpResource) {
        this.httpResource = httpResource;
    }

    public String getTcpRequest() {
        return tcpRequest;
    }

    public void setTcpRequest(String tcpRequest) {
        this.tcpRequest = tcpRequest;
    }

    public String getTcpPort() {
        return tcpPort;
    }

    public void setTcpPort(String tcpPort) {
        this.tcpPort = tcpPort;
    }

    public String getFlagVersion() {
        return flagVersion;
    }

    public void setFlagVersion(String flagVersion) {
        this.flagVersion = flagVersion;
    }
}
