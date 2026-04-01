package ru.ifmo.se.udp;

import java.nio.ByteBuffer;

public final class UdpCodec {

    private static final byte VERSION = 1;

    public static byte[] encode(UdpFrame frame) {
        ByteBuffer buffer = ByteBuffer.allocate(
                1 +                       // version
                1 +                       // type
                Long.BYTES +              // messageId
                Integer.BYTES * 3 +       // index + count + payloadLength
                frame.payload().length
        );

        buffer.put(VERSION);
        buffer.put((byte) frame.type().ordinal());
        buffer.putLong(frame.messageId());
        buffer.putInt(frame.chunkIndex());
        buffer.putInt(frame.chunkCount());
        buffer.putInt(frame.payload().length);
        buffer.put(frame.payload());

        return buffer.array();
    }

    public static UdpFrame decode(ByteBuffer buffer) {
        byte version = buffer.get();
        if (version != VERSION) {
            throw new IllegalArgumentException("Unsupported version: " + version);
        }

        PacketType type = PacketType.values()[buffer.get()];
        long messageId = buffer.getLong();
        int chunkIndex = buffer.getInt();
        int chunkCount = buffer.getInt();
        int payloadLength = buffer.getInt();

        byte[] payload = new byte[payloadLength];
        buffer.get(payload);

        return new UdpFrame(type, messageId, chunkIndex, chunkCount, payload);
    }
}