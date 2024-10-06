package com.javarush.cache.redis_enum;

import redis.clients.jedis.util.SafeEncoder;
import redis.clients.jedis.commands.ProtocolCommand;

public enum RedisModuleCommand implements ProtocolCommand {
    RESERVE("TOPK.RESERVE"),
    ADD("TOPK.ADD"),
    INCRBY("TOPK.INCRBY"),
    QUERY("TOPK.QUERY"),
    COUNT("TOPK.COUNT"),
    LIST("TOPK.LIST"),
    INFO("TOPK.INFO");

    private final byte[] raw;

    RedisModuleCommand(String alt) {
        raw = SafeEncoder.encode(alt);
    }

    @Override
    public byte[] getRaw() {
        return raw;
    }
}
