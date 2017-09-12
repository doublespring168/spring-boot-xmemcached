package com.spring.boot;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Liu Hailin
 * @create 2017-09-11 下午6:39
 **/
@ConditionalOnExpression("'${memcached.servers}'.length() > 0")
@Configuration
@ConfigurationProperties(
    prefix = "memcached"
)
@Data
public class XmemcacheConfig {

    private String servers;

    private int poolSize;

    private String userName;

    private String password;

    private long connectionTimeOut;

    private long opTimeOut;
}
