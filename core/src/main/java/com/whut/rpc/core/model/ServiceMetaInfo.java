package com.whut.rpc.core.model;

import cn.hutool.core.util.StrUtil;
import com.whut.rpc.core.config.RpcApplication;
import lombok.Data;

import static com.whut.rpc.core.constant.RpcConstant.*;

/**
 * service registry meta information
 *
 * @author whut2024
 * @since 2024-07-26
 */
@Data
public class ServiceMetaInfo {


    private String name;


    private String version = DEFAULT_SERVICE_VERSION;


    private String host;


    private Integer port;


    private String group = "default";


    /**
     * get the key of a service
     */
    public String getServiceKey() {
        return String.format("%s:%s%s", name, version, group);
    }


    /**
     * get the key of a service's node
     */
    public String getNodeKey() {
        return String.format("%s:%s:%s:%s", RpcApplication.getConfig().getName(), getServiceKey(), host, port);
    }


    /**
     * get a service's full url
     */
    public String getFullServiceAddress() {
        if (!StrUtil.contains(host, "http")) {
            return String.format("http://%s:%s", host, port);
        }

        return String.format("%s:%s", host, port);
    }
}
