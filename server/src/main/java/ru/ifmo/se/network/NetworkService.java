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
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Level;

@RequiredArgsConstructor
public class NetworkService implements Runnable, ShutdownListener {

    private volatile boolean shutdown = false;
    private static final int BUFFER_SIZE = 2048;

    private final CommandInvoker commandInvoker;

    // Синхронизированная коллекция для буферов сборки сообщений
    private final Map<Long, ReassemblyBuffer> incoming = Collections.synchronizedMap(new HashMap<>());

    // Пул для чтения и сборки фрагментов (CachedThreadPool)
    private final ExecutorService readPool = Executors.newCachedThreadPool();

    // Пул для обработки запросов (ForkJoinPool)
    private final ForkJoinPool processingPool = ForkJoinPool.commonPool();

    @Override
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
                        // Пропускаем ошибочные фреймы
                        continue;
                    }

                    // Передаём обработку фрейма в CachedThreadPool
                    final DatagramChannel channelForTask = readyChannel;
                    final InetSocketAddress addressForTask = clientAddress;
                    readPool.submit(() -> processFrame(frame, addressForTask, channelForTask));
                }
            }
        } catch (BindException e) {
            AppLogger.LOGGER.log(Level.SEVERE, "Порт уже занят");
        } catch (Exception e) {
            AppLogger.LOGGER.log(Level.SEVERE, "Ошибка сети", e);
        } finally {
            shutdownPools();
        }
    }

    /**
     * Обработка одного UDP-фрейма: добавление в буфер сборки,
     * при завершении сообщения – передача в ForkJoinPool.
     */
    private void processFrame(UdpFrame frame, InetSocketAddress clientAddress, DatagramChannel channel) {
        try {
            ReassemblyBuffer reassembly = incoming.computeIfAbsent(
                    frame.messageId(),
                    id -> new ReassemblyBuffer(frame.chunkCount())
            );

            reassembly.addChunk(frame.chunkIndex(), frame.payload());

            if (!reassembly.isComplete()) return;

            // Сообщение полностью собрано – удаляем буфер
            incoming.remove(frame.messageId());

            byte[] rawData = reassembly.assemble();

            // Передаём десериализацию и обработку в ForkJoinPool
            processingPool.submit(() -> handleRequest(rawData, clientAddress, channel, frame.messageId()));
        } catch (Exception e) {
            AppLogger.LOGGER.log(Level.WARNING, "Ошибка при сборке фреймов", e);
        }
    }

    /**
     * Десериализация запроса, вызов команды и запуск потока для отправки ответа.
     */
    private void handleRequest(byte[] rawData, InetSocketAddress clientAddress,
                               DatagramChannel channel, long messageId) {
        try {
            Request request = (Request) Serializator.deserialize(ByteBuffer.wrap(rawData));
            Response response = commandInvoker.invokeCommand(request);

            // Отправка ответа в отдельном новом потоке
            new Thread(() -> sendResponse(response, clientAddress, channel, messageId)).start();
        } catch (Exception e) {
            AppLogger.LOGGER.log(Level.SEVERE, "Ошибка обработки запроса от " + clientAddress, e);
        }
    }

    /**
     * Сериализация ответа, разбивка на фреймы и отправка через DatagramChannel.
     * Доступ к каналу синхронизирован, чтобы избежать конфликтов при параллельной отправке.
     */
    private void sendResponse(Response response, InetSocketAddress clientAddress,
                              DatagramChannel channel, long messageId) {
        try {
            byte[] responseBytes = Serializator.serialize(response);
            List<byte[]> frames = UdpChunker.split(responseBytes, PacketType.RESPONSE, messageId);

            // Синхронизация на канале – гарантия потокобезопасности при отправке
            synchronized (channel) {
                for (byte[] frameData : frames) {
                    channel.send(ByteBuffer.wrap(frameData), clientAddress);
                }
            }
            AppLogger.LOGGER.info("Ответ отправлен клиенту " + clientAddress);
        } catch (Exception e) {
            AppLogger.LOGGER.log(Level.SEVERE, "Ошибка отправки ответа клиенту " + clientAddress, e);
        }
    }

    /**
     * Корректное завершение пулов потоков при остановке сервера.
     */
    private void shutdownPools() {
        readPool.shutdown();
        processingPool.shutdown();
        // Принудительно завершаем потоки, если нужно – можно добавить awaitTermination
    }

    @Override
    public void onShutdown() {
        shutdown = true;
        shutdownPools();
        System.exit(0);
    }
}