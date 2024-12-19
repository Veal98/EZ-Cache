package cn.itmtx.ezcache.operator.map;

import cn.itmtx.ezcache.common.bo.CacheWrapper;
import cn.itmtx.ezcache.common.bo.EzCacheConfig;
import cn.itmtx.ezcache.serializer.hessian.HessianSerializer;
import cn.itmtx.ezcache.serializer.ISerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * map 变更通知
 */
public class MapChangeTask implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(MapChangeTask.class);

    private final ConcurrentHashMapCacheOperator concurrentHashMapCacheOperator;

    private final EzCacheConfig ezCacheConfig;

    /**
     * 缓存被修改的个数
     */
    private AtomicInteger mapChanged = new AtomicInteger(0);

    /**
     * task 是否正在执行
     */
    private volatile boolean running = false;

    /**
     * 持久化到磁盘的文件
     */
    private File persistFile;

    /**
     * 只有HessianSerializer才支持SoftReference序列化
     */
    private static final ISerializer<Object> PERSIST_SERIALIZER = new HessianSerializer();

    public MapChangeTask(ConcurrentHashMapCacheOperator concurrentHashMapCacheOperator, EzCacheConfig ezCacheConfig) {
        this.concurrentHashMapCacheOperator = concurrentHashMapCacheOperator;
        this.ezCacheConfig = ezCacheConfig;
    }

    public void start() {
        if (!this.running) {
            // 从磁盘中加载数据
            loadCacheFromDisk();
            this.running = true;
        }
    }

    public void destroy() {
        // 数据持久化到磁盘
        persistCacheToDisk(true);
        this.running = false;
    }

    /**
     * 从磁盘中加载数据
     */
    private void loadCacheFromDisk() {
        if (null == persistFile || !persistFile.exists()) {
            return;
        }

        BufferedInputStream bis = null;
        try {
            FileInputStream fis = new FileInputStream(persistFile);
            bis = new BufferedInputStream(fis);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte buf[] = new byte[1024];
            int len = -1;
            while ((len = bis.read(buf)) != -1) {
                baos.write(buf, 0, len);
            }
            byte retArr[] = baos.toByteArray();
            Object obj = PERSIST_SERIALIZER.deserialize(retArr);
            if (obj instanceof ConcurrentHashMap) {
                // 存入 map
                concurrentHashMapCacheOperator.getMap().putAll((ConcurrentHashMap<String, Object>) obj);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            if (null != bis) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 数据持久化到磁盘
     * @param forcePersist 是否强制持久化
     */
    private void persistCacheToDisk(boolean forcePersist) {
        int changedCnt = mapChanged.intValue();
        // 当 Map 变更数量超过配置的 mapUnPersistCountThreshold 时才做持久化操作即存入磁盘)
        if (!forcePersist && changedCnt <= ezCacheConfig.getMapUnPersistCountThreshold()) {
            return;
        }

        FileOutputStream fos = null;
        try {
            // 序列化整个 map
            byte[] data = PERSIST_SERIALIZER.serialize(concurrentHashMapCacheOperator.getMap());
            if (persistFile == null) {
                persistFile = MapCacheUtils.getSaveFile(concurrentHashMapCacheOperator.getEzCacheConfigBo().getNamespace());
            }
            fos = new FileOutputStream(persistFile);
            fos.write(data);

            // 持久化成功，mapChanged 归零
            mapChanged.set(0);
        } catch (Exception ex) {
            // 持久化失败
            logger.error(ex.getMessage(), ex);
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 清除过期缓存
     */
    private void cleanMap() {
        Iterator<Map.Entry<String, Object>> iterator = concurrentHashMapCacheOperator.getMap().entrySet().iterator();
        int mapChangedCnt = 0;
        int i = 0;
        while (iterator.hasNext()) {
            mapChangedCnt += removeExpiredItem(iterator);
            i ++;
            if (i == 2000) {
                i = 0;
                try {
                    // 当一个线程处理的过期数据达到 2000 条时, 使用 sleep(0) 强制触发操作系统重新进行一次CPU竞争
                    Thread.sleep(0);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        // 变更通知
        if (mapChangedCnt > 0) {
            this.mapChangeMulti(mapChangedCnt);
        }
    }

    private int removeExpiredItem(Iterator<Map.Entry<String, Object>> iterator) {
        int mapChangedCnt = 0;
        Object value = iterator.next().getValue();
        if (value instanceof SoftReference) {
            SoftReference<CacheWrapper<Object>> reference = (SoftReference<CacheWrapper<Object>>) value;
            if (null != reference && null != reference.get()) {
                CacheWrapper<Object> mapValue = reference.get();
                if (mapValue.isExpired()) {
                    iterator.remove();
                    mapChangedCnt++;
                }
            } else {
                iterator.remove();
                mapChangedCnt++;
            }
        }
        return mapChangedCnt;
    }

    /**
     * 变更一条记录
     */
    public void mapChangeSingle() {
        mapChanged.incrementAndGet();
    }

    /**
     * 变更多条记录
     *
     * @param cnt 变更数量
     */
    public void mapChangeMulti(int cnt) {
        mapChanged.addAndGet(cnt);
    }

    @Override
    public void run() {
        while (running) {
            try {
                // 清除过期缓存
                cleanMap();
                // 持久化缓存
                persistCacheToDisk(false);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            try {
                // sleep 1min, 防止 cpu 压力过大
                Thread.sleep(60 * 1000);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }



    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.running = false;
    }
}
