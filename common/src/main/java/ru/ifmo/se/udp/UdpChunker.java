package ru.ifmo.se.udp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class UdpChunker {

    private static final int MAX_DATAGRAM_SIZE = 1200;

    public static List<byte[]> split(byte[] data, PacketType type, long messageId) {

        int headerSize = 1 + 1 + Long.BYTES + Integer.BYTES * 3;
        int payloadSize = MAX_DATAGRAM_SIZE - headerSize;

        int chunkCount = (data.length + payloadSize - 1) / payloadSize;

        List<byte[]> result = new ArrayList<>(chunkCount);

        for (int i = 0; i < chunkCount; i++) {
            int from = i * payloadSize;
            int to = Math.min(from + payloadSize, data.length);

            byte[] payload = Arrays.copyOfRange(data, from, to);

            UdpFrame frame = new UdpFrame(
                    type,
                    messageId,
                    i,
                    chunkCount,
                    payload
            );

            result.add(UdpCodec.encode(frame));
        }

        return result;
    }
}