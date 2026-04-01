package ru.ifmo.se.network;

import lombok.RequiredArgsConstructor;
import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.event.ShutdownListener;
import ru.ifmo.se.io.input.CommandInvoker;
import ru.ifmo.se.logger.AppLogger;
import ru.ifmo.se.serializator.Serializator;
import ru.ifmo.se.udp.*;

import java.net.BindException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

@RequiredArgsConstructor
public class NetworkService implements Runnable, ShutdownListener {

    private volatile boolean shutdown = false;
    private static final int BUFFER_SIZE = 2048;

    private final CommandInvoker commandInvoker;

    private final Map<Long, ReassemblyBuffer> incoming = new HashMap<>();

    public void run() {
        try (DatagramChannel channel = DatagramChannel.open();
             Selector selector = Selector.open()) {

            channel.configureBlocking(false);
            channel.bind(new InetSocketAddress("0.0.0.0", 56789));
            channel.register(selector, SelectionKey.OP_READ);

            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

            AppLogger.LOGGER.info("UDP сервер запущен");

            while (!shutdown) {
                selector.select();

                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    if (!key.isReadable()) continue;

                    DatagramChannel readyChannel = (DatagramChannel) key.channel();

                    buffer.clear();
                    InetSocketAddress clientAddress =
                            (InetSocketAddress) readyChannel.receive(buffer);

                    if (clientAddress == null) continue;

                    buffer.flip();

                    UdpFrame frame;
                    try {
                        frame = UdpCodec.decode(buffer);
                    } catch (Exception e) {
                        continue;
                    }

                    ReassemblyBuffer reassembly = incoming.computeIfAbsent(
                            frame.messageId(),
                            id -> new ReassemblyBuffer(frame.chunkCount())
                    );

                    reassembly.addChunk(frame.chunkIndex(), frame.payload());

                    if (!reassembly.isComplete()) continue;

                    incoming.remove(frame.messageId());

                    Request request;
                    try {
                        byte[] raw = reassembly.assemble();
                        request = (Request) Serializator.deserialize(ByteBuffer.wrap(raw));
                    } catch (Exception e) {
                        AppLogger.LOGGER.log(Level.SEVERE, "Ошибка десериализации запроса", e);
                        continue;
                    }

                    Response response = commandInvoker.invokeCommand(request);

                    byte[] responseBytes = Serializator.serialize(response);

                    List<byte[]> frames = UdpChunker.split(
                            responseBytes,
                            PacketType.RESPONSE,
                            frame.messageId()
                    );

                    for (byte[] f : frames) {
                        readyChannel.send(ByteBuffer.wrap(f), clientAddress);
                    }

                    AppLogger.LOGGER.info("Ответ отправлен клиенту " + clientAddress);
                }
            }
        } catch (BindException e) {
            AppLogger.LOGGER.log(Level.SEVERE,"Порт уже занят");
        } catch (Exception e) {
            AppLogger.LOGGER.log(Level.SEVERE,"Ошибка сети", e);
        }
    }

    @Override
    public void onShutdown() {
        shutdown = true;
        System.exit(0);
    }
}