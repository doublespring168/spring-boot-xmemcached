package com.spring.boot.xmemcached;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.auth.AuthInfo;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.impl.KetamaMemcachedSessionLocator;
import net.rubyeye.xmemcached.transcoders.SerializingTranscoder;
import net.rubyeye.xmemcached.utils.AddrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * @author Liu Hailin
 * @create 2017-09-11 下午6:33
 **/
@Configuration
@EnableConfigurationProperties(XmemcacheConfig.class)
@ConditionalOnProperty(prefix = "memcached",name = "enable",havingValue = "true")
@Slf4j
public class XmemcachedAutoConfiguration {

    @Autowired
    private XmemcacheConfig config;

    public MemcachedClient createClient() throws IOException {
        MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil
            .getAddresses(config.getServers()));

        builder.setConnectionPoolSize(config.getPoolSize());
        if(StringUtils.hasText(config.getUserName())&&StringUtils.hasText(config.getPassword())){
            log.info( "[MemcachedClient]---server:{},username:{},password:{}",config.getServers(), config.getUserName(),config.getPassword());
            builder.addAuthInfo(AddrUtil.getOneAddress(config.getServers()), AuthInfo
                .plain(config.getUserName(), config.getPassword()));
        }
        builder.setFailureMode(true);
        builder.setSessionLocator(new KetamaMemcachedSessionLocator());
        builder.setCommandFactory(new BinaryCommandFactory());
        // 使用序列化传输编码
        builder.setTranscoder(new SerializingTranscoder());
        // 进行数据压缩，大于1KB时进行压缩
        builder.getTranscoder().setCompressionThreshold(1024);

        MemcachedClient client = builder.build();
        client.setConnectTimeout(config.getConnectionTimeOut());
        client.setOpTimeout(config.getOpTimeOut());
        client.setPrimitiveAsString(true);
        return client;
    }


    @Bean
    public XMemcacheManager createManager() throws IOException {
        return new XMemcacheManager(createClient());
    }

}
