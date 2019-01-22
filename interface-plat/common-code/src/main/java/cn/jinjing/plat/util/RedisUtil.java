package cn.jinjing.plat.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.redisson.Redisson;
import org.redisson.api.RMap;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/**
 redis_addrs=192.168.10.102:7000,192.168.10.102:7001,192.168.10.102:7002,192.168.10.103:7000,192.168.10.103:7001,192.168.10.103:7002
 redis_max_active=50
 redis_max_idle=20
 redis_min_idle=8
 redis_max_wait=5000
 redis_connection_timeout=5000
 redis_so_timeout=5000
 redis_max_attempts=1
 acquire_time_out=3000
 expire_time=2000
 */

public class RedisUtil {

    private static Log log = LogFactory.getLog(RedisUtil.class);

    private static RedisUtil instance;

    private JedisCluster jedisCluster = null;

    private RedissonClient redissonClient = null;

    private static ReentrantLock lock = new ReentrantLock();

    private static String REDIS_ADDRS = ConfigUtil.getProperties("redis_addrs");
    //可用连接实例的最大数目，默认值为8；
    //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
    private static int MAX_ACTIVE = Integer.parseInt(ConfigUtil.getProperties("redis_max_active"));
    //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
    private static int MAX_IDLE = Integer.parseInt(ConfigUtil.getProperties("redis_max_idle"));
    private static int MIN_IDLE = Integer.parseInt(ConfigUtil.getProperties("redis_min_idle"));
    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    private static int MAX_WAIT = Integer.parseInt(ConfigUtil.getProperties("redis_max_wait"));
    //连接超时时间
    private static int CONNECTION_TIMEOUT = Integer.parseInt(ConfigUtil.getProperties("redis_connection_timeout"));
    //返回值的超时时间
    private static int SO_TIMEOUT = Integer.parseInt(ConfigUtil.getProperties("redis_so_timeout"));
    //出现异常最大重试次数
    private static int MAX_ATTEMPTS = Integer.parseInt(ConfigUtil.getProperties("redis_max_attempts"));

    private RedisUtil() {
    }

    public static RedisUtil getInstance() {
        if (instance == null) {
            lock.lock();
            try {
                if (instance == null) {
                    instance = new RedisUtil();
                }
            } catch (Exception e){
                log.error(ExceptionUtil.printStackTraceToString(e));
            }
            finally {
                lock.unlock();
            }
        }
        return instance;
    }

    /**
     * 初始化JedisPool
     */
    private void initJedisPool() {
        String[] serverArray = REDIS_ADDRS.split(",");
        Set<HostAndPort> nodes = new HashSet<>();

        for (String ipPort : serverArray) {
            String[] ipPortPair = ipPort.split(":");
            nodes.add(new HostAndPort(ipPortPair[0].trim(), Integer.valueOf(ipPortPair[1].trim())));
        }

        //这里超时时间不要太短，他会有超时重试机制
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(MAX_ACTIVE);
        config.setMaxIdle(MAX_IDLE);
        config.setMinIdle(MIN_IDLE);
        config.setMaxWaitMillis(MAX_WAIT);
        config.setTestOnBorrow(false);
        config.setTestOnReturn(false);
        jedisCluster = new JedisCluster(nodes, CONNECTION_TIMEOUT, SO_TIMEOUT, MAX_ATTEMPTS, config);
    }

    /**
     * 通用方法：从JedisPool中获取Jedis
     *
     * @return
     */
    public JedisCluster getJedisCluster() {
        if (jedisCluster == null || jedisCluster.getClusterNodes().size() <= 0) {
            lock.lock();    //防止吃初始化时多线程竞争问题
            try {
                if (jedisCluster == null || jedisCluster.getClusterNodes().size() <= 0){
                    initJedisPool();
                    log.info(">>>>>>>>>>>>> JedisCluster 初始化成功！");
                }
            } catch (Exception e){
                jedisCluster = null;
                log.error(ExceptionUtil.printStackTraceToString(e));
            }finally {
                lock.unlock();
            }
        }
        return jedisCluster;
    }

}
