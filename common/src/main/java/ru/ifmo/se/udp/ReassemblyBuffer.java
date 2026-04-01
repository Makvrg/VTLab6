package ru.ifmo.se.udp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ReassemblyBuffer {

    private final byte[][] chunks;
    private final boolean[] received;
    private final int totalChunks;
    private int receivedCount = 0;
    private final long createdAt = System.currentTimeMillis();

    public ReassemblyBuffer(int totalChunks) {
        this.totalChunks = totalChunks;
        this.chunks = new byte[totalChunks][];
        this.received = new boolean[totalChunks];
    }

    public void addChunk(int index, byte[] payload) {
        if (index < 0 || index >= totalChunks) return;

        if (!received[index]) {
            chunks[index] = payload;
            received[index] = true;
            receivedCount++;
        }
    }

    public boolean isComplete() {
        return receivedCount == totalChunks;
    }

    public byte[] assemble() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (byte[] chunk : chunks) {
            out.write(chunk);
        }
        return out.toByteArray();
    }

    public long ageMs() {
        return System.currentTimeMillis() - createdAt;
    }
}