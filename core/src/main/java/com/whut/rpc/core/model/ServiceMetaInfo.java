package com.whut.rpc.core.model;

import cn.hutool.core.util.StrUtil;
import com.whut.rpc.core.config.RpcApplication;
import lombok.Data;

import java.util.Comparator;

import static com.whut.rpc.core.constant.RpcConstant.*;

/**
 * service registry meta information
 *
 * @author whut2024
 * @since 2024-07-26
 */
@Data
public class ServiceMetaInfo implements Comparable<ServiceMetaInfo> {


    private String name;


    private String version = DEFAULT_SERVICE_VERSION;


    private String host;


    private Integer port;


    private String group = "default";


    private Integer usedNumber;


    /**
     * get the key of a service
     */
    public String getServiceKey() {
        return String.format("%s/%s/%s/%s", RpcApplication.getConfig().getName(), name, version, group);
    }


    /**
     * get the key of a service's node
     */
    public String getNodeKey() {
        return String.format("%s/%s/%s", getServiceKey(), host, port);
    }

    /**
     * transform node-key to service-key
     */
    public static String getServiceKey(String nodeKey) {
        final String[] strings = nodeKey.split("/");
        final StringBuilder builder = new StringBuilder();
        for (int i = 2; i < 6; i++) {
            builder.append(strings[i]);
            if (i != 5) builder.append("/");
        }

        return builder.toString();
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




    @Override
    public int compareTo(ServiceMetaInfo o) {
        return this.usedNumber - o.usedNumber;
    }
}
