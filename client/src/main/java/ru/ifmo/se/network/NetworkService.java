package ru.ifmo.se.network;

import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.serializator.Serializator;
import ru.ifmo.se.udp.*;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.*;

public class NetworkService {

    private static final int BUFFER_SIZE = 2048;
    private static final int TIMEOUT_MS = 5000;

    private final DatagramChannel channel;
    private final Selector selector;
    private final SocketAddress serverAddress;

    public NetworkService(String ip, int port, DatagramChannel channel, Selector selector) {
        this.channel = channel;
        this.selector = selector;
        this.serverAddress = new InetSocketAddress(ip, port);
    }

    public Response send(Request request) {
        try {
            long messageId = new Random().nextLong();

            byte[] payload = Serializator.serialize(request);

            List<byte[]> frames = UdpChunker.split(
                    payload,
                    PacketType.REQUEST,
                    messageId
            );

            for (byte[] frame : frames) {
                channel.send(ByteBuffer.wrap(frame), serverAddress);
            }

            byte[] responseBytes = receiveFullResponse(messageId);

            return (Response) Serializator.deserialize(ByteBuffer.wrap(responseBytes));

        } catch (Exception e) {
            throw new NetworkException("Ошибка связи с сервером");
        }
    }

    private byte[] receiveFullResponse(long expectedMessageId) throws Exception {

        Map<Long, ReassemblyBuffer> incoming = new HashMap<>();
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        long deadline = System.currentTimeMillis() + TIMEOUT_MS;

        while (System.currentTimeMillis() < deadline) {

            long timeout = deadline - System.currentTimeMillis();

            int ready = selector.select(timeout);
            if (ready == 0) continue;

            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                if (!key.isReadable()) continue;

                DatagramChannel datagramChannel = (DatagramChannel) key.channel();

                buffer.clear();
                SocketAddress sender = datagramChannel.receive(buffer);
                buffer.flip();

                if (!serverAddress.equals(sender)) continue;

                UdpFrame frame = UdpCodec.decode(buffer);

                if (frame.type() != PacketType.RESPONSE) continue;
                if (frame.messageId() != expectedMessageId) continue;

                ReassemblyBuffer reassembly = incoming.computeIfAbsent(
                        frame.messageId(),
                        id -> new ReassemblyBuffer(frame.chunkCount())
                );

                reassembly.addChunk(frame.chunkIndex(), frame.payload());

                if (reassembly.isComplete()) {
                    return reassembly.assemble();
                }
            }
        }

        throw new NetworkException("Таймаут ожидания ответа от сервера");
    }
}