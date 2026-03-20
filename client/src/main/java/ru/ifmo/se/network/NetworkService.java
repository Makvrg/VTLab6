package ru.ifmo.se.network;

import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.serializator.Serializator;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

public class NetworkService {
    private static final int MAX_ATTEMPTS = 4;
    private static final int SELECT_TIMEOUT_MS = 5000;
    private static final int RESPONSE_BUFFER_SIZE = 16384;

    private final DatagramChannel channel;
    private final Selector selector;
    private final SocketAddress socketAddress;

    public NetworkService(String ip, int port, DatagramChannel channel, Selector selector) {
        this.channel = channel;
        this.selector = selector;
        this.socketAddress = new InetSocketAddress(ip, port);
    }

    public Response send(Request request) {
        try {
            byte[] payload = Serializator.serialize(request);
            ByteBuffer sendBuffer = ByteBuffer.wrap(payload);
            ByteBuffer receiveBuffer = ByteBuffer.allocate(RESPONSE_BUFFER_SIZE);

            for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
                sendBuffer.rewind();
                channel.send(sendBuffer, socketAddress);

                Response response = receive(receiveBuffer);
                if (response != null) {
                    return response;
                }
            }

            throw new NetworkException("Не удалось получить ответ от сервера после " +
                    MAX_ATTEMPTS + " попыток");
        } catch (NetworkException e) {
            throw e;
        } catch (Exception e) {
            throw new NetworkException("Невозможно получить доступ к серверу");
        }
    }

    private Response receive(ByteBuffer buffer) {
        try {
            int ready = selector.select(SELECT_TIMEOUT_MS);
            if (ready == 0) {
                return null;
            }

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                if (!key.isValid() || !key.isReadable()) {
                    continue;
                }

                DatagramChannel datagramChannel = (DatagramChannel) key.channel();

                buffer.clear();
                SocketAddress sender = datagramChannel.receive(buffer);
                buffer.flip();

                if (!socketAddress.equals(sender)) {
                    continue;
                }
                return (Response) Serializator.deserialize(buffer);
            }
            return null;
        } catch (IOException e) {
            throw new NetworkException("Невозможно получить доступ к серверу");
        } catch (Exception e) {
            throw new NetworkException("От сервера получены некорректные данные");
        }
    }
}
