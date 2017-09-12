package com.spring.boot.xmemcached;

import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.transcoders.Transcoder;

/**
 * @author Liu Hailin
 * @create 2017-09-12 上午11:31
 **/
public interface IManager {

    String get(String key);

    <T> T get(String key,Transcoder<T> transcoder);

    void set(String key,String value);

    void set(String key,int time,String value);

    <T> void set(String key,T value,Transcoder<T> transcoder);

    <T> void set(String key,int time,T value,Transcoder<T> transcoder);

    boolean isMutex(String key,int exp);

    void delete(String key);

    long incr(String key,long delta);

    long incr(String key,long delta,long value);

    long incr(String key,long delta,long value,long timeout);

    long incr(String key,long delta,long value,long timeout,int exp);

    long decr(String key,long delta);

    long decr(String key,long delta,long value);

    long decr(String key,long delta,long value,long timeout);

    long decr(String key,long delta,long value,long timeout,int exp);

}
