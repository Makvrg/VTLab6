package ru.ifmo.se.udp;

public record UdpFrame(
        PacketType type,
        long messageId,
        int chunkIndex,
        int chunkCount,
        byte[] payload
) {}