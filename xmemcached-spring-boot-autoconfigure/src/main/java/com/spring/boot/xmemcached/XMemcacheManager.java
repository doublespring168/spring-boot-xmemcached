package com.spring.boot.xmemcached;

import javax.annotation.PreDestroy;

import com.spring.boot.xmemcached.XMemcacheManager.Manager;
import lombok.extern.slf4j.Slf4j;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.transcoders.Transcoder;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Liu Hailin
 * @create 2017-09-12 上午10:32
 **/
@ConditionalOnBean(XmemcachedAutoConfiguration.class)
@Configuration
@Slf4j
public class XMemcacheManager implements FactoryBean<Manager> {

    @Autowired
    private MemcachedClient client;

    @PreDestroy
    private void closeClient() {
        if (client != null) {
            try {
                if (!client.isShutdown()) {
                    client.shutdown();
                    log.debug("Shutdown MemcachedManager...");
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public Manager getObject() throws Exception {
        return new Manager();
    }

    @Override
    public Class<?> getObjectType() {
        return Manager.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    enum ExpiredTimeEnum {
        MINUTE(60),
        HOUR(3600),
        DAY(3600 * 24),
        WEEK(3600 * 24 * 7),
        MONTH(3600 * 24 * 30 * 7),
        FOREVER(0);

        private int time;

        ExpiredTimeEnum(int time) {
            this.time = time;
        }

        public int getTime() {
            return time;
        }
    }

    /**
     * 冲突延时 1秒
     */
    public static final int MUTEX_EXP = 1;

    /**
     * 冲突键
     */
    public static final String MUTEX_KEY_PREFIX = "MUTEX_";

    class Manager implements IManager {

        @Override
        public String get(String key) {
            String rs = null;
            try {
                rs = client.get(key);
            } catch (Exception e) {
                log.error("[Memcache]===ERROR,method=get(String key),key={},e={}", key, e.getMessage(), e);
            }
            return rs;
        }

        @Override
        public <T> T get(String key, Transcoder<T> transcoder) {
            try {
                return client.get(key, transcoder);
            } catch (Exception e) {
                log.error("[Memcache]===ERROR,method=get(String key, Transcoder<T> transcoder),key={},e={}", key,
                    e.getMessage(), e);
            }
            return null;
        }

        @Override
        public void set(String key, String value) {
            try {
                client.set(key, ExpiredTimeEnum.FOREVER.getTime(), value);
            } catch (Exception e) {
                log.error("[Memcache]===ERROR,method=set(String key, String value),key={},vaule={},e={}", key, value,
                    e.getMessage(), e);
            }
        }

        @Override
        public void set(String key, int time, String value) {
            try {
                client.set(key, time, value);
            } catch (Exception e) {
                log.error("[Memcache]===ERROR,method=set(String key, int time, String vaule),key={},vaule={},e={}", key,
                    value, e.getMessage(), e);
            }
        }

        @Override
        public <T> void set(String key, T value, Transcoder<T> transcoder) {
            try {
                client.set(key, ExpiredTimeEnum.FOREVER.getTime(), value, transcoder);
            } catch (Exception e) {
                log.error(
                    "[Memcache]===ERROR,method=set(String key, T value,Transcoder<T> transcoder),key={},vaule={},e={}",
                    key, value, e.getMessage(), e);
            }
        }

        @Override
        public <T> void set(String key, int time, T value, Transcoder<T> transcoder) {
            try {
                client.set(key, time, value, transcoder);
            } catch (Exception e) {
                log.error(
                    "[Memcache]===ERROR,method=set(String key, int time, T value,Transcoder<T> transcoder),key={},"
                        + "vaule={},e={}",
                    key, value, e.getMessage(), e);
            }
        }

        @Override
        public boolean isMutex(String key, int exp) {
            boolean status = true;
            try {
                if(client.add(MUTEX_KEY_PREFIX+key,exp,1)){
                    status = false;
                }
            } catch (Exception e) {
                log.error(
                    "[Memcache]===ERROR,method=isMutex(String key, int exp),key={},"
                        + "e={}",
                    key, e.getMessage(), e);
            }
            return status;
        }

        @Override
        public void delete(String key) {
            try {
                client.deleteWithNoReply(key);
            } catch (Exception e) {
                log.error(
                    "[Memcache]===ERROR,method=delete(String key),key={},"
                        + "e={}",
                    key, e.getMessage(), e);
            }
        }

        @Override
        public long incr(String key, long delta) {
            try {
                return client.incr(key,delta);
            } catch (Exception e) {
                log.error(
                    "[Memcache]===ERROR,method=incr(String key, long delta),key={},"
                        + "e={}",
                    key, e.getMessage(), e);
            }
            return 0;
        }

        @Override
        public long incr(String key, long delta, long value) {
            try {
                return client.incr(key,delta,value);
            } catch (Exception e) {
                log.error(
                    "[Memcache]===ERROR,method=incr(String key, long delta, long value),key={},"
                        + "e={}",
                    key, e.getMessage(), e);
            }
            return 0;
        }

        @Override
        public long incr(String key, long delta, long value, long timeout) {
            try {
                return client.incr(key,delta,value,timeout);
            } catch (Exception e) {
                log.error(
                    "[Memcache]===ERROR,method=incr(String key, long delta, long value, long timeout),key={},"
                        + "e={}",
                    key, e.getMessage(), e);
            }
            return 0;
        }

        @Override
        public long incr(String key, long delta, long value, long timeout, int exp) {
            try {
                return client.incr(key,delta,value,timeout,exp);
            } catch (Exception e) {
                log.error(
                    "[Memcache]===ERROR,method=incr(String key, long delta, long value, long timeout, int exp),key={},"
                        + "e={}",
                    key, e.getMessage(), e);
            }
            return 0;
        }


        @Override
        public long decr(String key, long delta) {
            try {
                return client.decr(key,delta);
            } catch (Exception e) {
                log.error(
                    "[Memcache]===ERROR,method=decr(String key, long delta),key={},"
                        + "e={}",
                    key, e.getMessage(), e);
            }
            return 0;
        }

        @Override
        public long decr(String key, long delta, long value) {
            try {
                return client.decr(key,delta,value);
            } catch (Exception e) {
                log.error(
                    "[Memcache]===ERROR,method=decr(String key, long delta, long value),key={},"
                        + "e={}",
                    key, e.getMessage(), e);
            }
            return 0;
        }

        @Override
        public long decr(String key, long delta, long value, long timeout) {
            try {
                return client.decr(key,delta,value,timeout);
            } catch (Exception e) {
                log.error(
                    "[Memcache]===ERROR,method=decr(String key, long delta, long value, long timeout),key={},"
                        + "e={}",
                    key, e.getMessage(), e);
            }
            return 0;
        }

        @Override
        public long decr(String key, long delta, long value, long timeout, int exp) {
            try {
                return client.decr(key,delta,value,timeout,exp);
            } catch (Exception e) {
                log.error(
                    "[Memcache]===ERROR,method=decr(String key, long delta, long value, long timeout, int exp),key={},"
                        + "e={}",
                    key, e.getMessage(), e);
            }
            return 0;
        }
    }
}
