package com.htt.kon.util;

/**
 * 分布式ID生成器: 雪花算法
 * 生成64位, 长度19的唯一id
 *
 * @author Twitter
 * @date 2018/7/27 9:30
 */

public class IdWorker {
    /**
     * 工作机器ID(0~31)
     */
    private long workerId;
    /**
     * 数据中心ID(0~31)
     */
    private long datacenterId;
    /**
     * 毫秒内序列(0~4095)
     */
    private long sequence;

    /**
     * 分布式id 生成器
     *
     * @param workerId     工作机器ID(0~31)
     * @param datacenterId 数据中心ID(0~31)
     * @param sequence     毫秒内序列(0~4095)
     */
    public IdWorker(long workerId, long datacenterId, long sequence) {
        // sanity check for workerId
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        System.out.printf("worker starting. timestamp left shift %d, datacenter id bits %d, worker id bits %d, sequence bits %d, workerid %d\n",
                timestampLeftShift, datacenterIdBits, workerIdBits, sequenceBits, workerId);

        this.workerId = workerId;
        this.datacenterId = datacenterId;
        this.sequence = sequence;
    }

    private long twepoch = 1288834974657L;

    private long workerIdBits = 5L;
    private long datacenterIdBits = 5L;
    private long maxWorkerId = -1L ^ (-1L << workerIdBits);
    private long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
    private long sequenceBits = 12L;

    private long workerIdShift = sequenceBits;
    private long datacenterIdShift = sequenceBits + workerIdBits;
    private long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    private long sequenceMask = -1L ^ (-1L << sequenceBits);

    private long lastTimestamp = -1L;
    private static IdWorker worker = new IdWorker(0L, 0L, 1L);

    public long getWorkerId() {
        return workerId;
    }

    public long getDatacenterId() {
        return datacenterId;
    }

    public long getTimestamp() {
        return System.currentTimeMillis();
    }


    /**
     * 单机环境下简单生成id
     */
    public static synchronized long singleNextId() {
        return worker.nextId();
    }

    /**
     * 单机环境下简单生成id
     */
    public static synchronized String singleNextIdString() {
        return worker.nextId() + "";
    }

    public synchronized long nextId() {
        long timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            System.err.printf("clock is moving backwards.  Rejecting requests until %d.", lastTimestamp);
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds",
                    lastTimestamp - timestamp));
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = timestamp;
        return ((timestamp - twepoch) << timestampLeftShift) |
                (datacenterId << datacenterIdShift) |
                (workerId << workerIdShift) |
                sequence;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }


    /**
     * ---------------测试---------------
     */
    public static void main(String[] args) {
        // IdWorker worker = new IdWorker(1, 1, 1);
        // for (int i = 0; i < 3000; i++) {
        //     System.out.println(worker.nextId());
        // }
        IdWorker worker1 = new IdWorker(10, 1, 1);
        for (int i = 0; i < 30; i++) {
            System.out.println(worker1.nextId());
        }
        long l = IdWorker.singleNextId();
        System.out.println(IdWorker.singleNextId());
        System.out.println(l);
    }

}
