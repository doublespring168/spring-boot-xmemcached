package com.spring.boot;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.auth.AuthInfo;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.impl.KetamaMemcachedSessionLocator;
import net.rubyeye.xmemcached.utils.AddrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * @author Liu Hailin
 * @create 2017-09-11 下午6:33
 **/
@Configuration
@ConditionalOnBean(XmemcacheConfig.class)
@EnableConfigurationProperties(XmemcacheConfig.class)
@Slf4j
public class XmemcachedAutoConfiguration {

    @Autowired
    private XmemcacheConfig config;

    @Bean
    public MemcachedClient createClient() throws IOException {
        MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil
            .getAddresses(config.getServers()));
        builder.setSessionLocator(new KetamaMemcachedSessionLocator());
        builder.setConnectionPoolSize(config.getPoolSize());
        if(StringUtils.hasText(config.getUserName())&&StringUtils.hasText(config.getPassword())){
            builder.addAuthInfo(AddrUtil.getOneAddress(config.getServers()), AuthInfo
                .typical(config.getUserName(), config.getPassword()));
        }
        builder.setFailureMode(true);
        builder.setCommandFactory(new BinaryCommandFactory());
        MemcachedClient client = builder.build();
        client.setConnectTimeout(config.getConnectionTimeOut());
        client.setOpTimeout(config.getOpTimeOut());
        client.setPrimitiveAsString(true);
        return client;
    }

}
