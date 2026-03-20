package ru.ifmo.se.network;

import lombok.RequiredArgsConstructor;
import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.event.ShutdownListener;
import ru.ifmo.se.io.input.CommandInvoker;
import ru.ifmo.se.logger.AppLogger;
import ru.ifmo.se.serializator.Serializator;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.logging.Level;

@RequiredArgsConstructor
public class NetworkService implements Runnable, ShutdownListener {

    private boolean shutdown = false;
    private static final int REQUEST_BUFFER_SIZE = 16384;
    private int commandCounter = 0;

    private final CommandInvoker commandInvoker;

    public void run() {
        try (DatagramChannel channel = DatagramChannel.open();
             Selector selector = Selector.open()) {

            channel.configureBlocking(false);
            channel.bind(new InetSocketAddress("0.0.0.0", 56789));
            channel.register(selector, SelectionKey.OP_READ);

            ByteBuffer buffer = ByteBuffer.allocate(REQUEST_BUFFER_SIZE);

            AppLogger.LOGGER.info("Сервер запущен в однопоточном режиме");

            while (!shutdown) {
                if (commandCounter == 3) {
                    saveCollection();
                    commandCounter = 0;
                }
                selector.select();

                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    if (!key.isReadable()) {
                        continue;
                    }

                    DatagramChannel readyChannel = (DatagramChannel) key.channel();

                    buffer.clear();
                    InetSocketAddress clientAddress =
                            (InetSocketAddress) readyChannel.receive(buffer);

                    if (clientAddress == null) {
                        continue;
                    }

                    buffer.flip();

                    Request request;
                    try {
                        request = (Request) Serializator.deserialize(buffer);
                    } catch (Exception e) {
                        AppLogger.LOGGER.log(
                                Level.SEVERE,
                                String.format(
                                        "Ошибка десериализации от %s",
                                        clientAddress
                                )
                        );
                        continue;
                    }

                    AppLogger.LOGGER.info(
                            String.format("Получен запрос от %s с командой: %s",
                                    clientAddress, request.getCommandName()));

                    Response response;
                    try {
                        response = commandInvoker.invokeCommand(request);
                    } catch (Exception e) {
                        AppLogger.LOGGER.log(
                                Level.SEVERE,
                                String.format(
                                        "Ошибка выполнения команды %s",
                                        request.getCommandName()
                                ), e
                        );
                        continue;
                    }
                    if (response == null) {
                        continue;
                    }
                    byte[] data;
                    try {
                        data = Serializator.serialize(response);
                    } catch (Exception e) {
                    AppLogger.LOGGER.log(
                            Level.SEVERE,
                            String.format(
                                    "Ошибка сериализации от %s",
                                    clientAddress
                            )
                    );
                    continue;
                    }
                    ByteBuffer responseBuffer = ByteBuffer.wrap(data);

                    readyChannel.send(responseBuffer, clientAddress);
                    commandCounter++;

                    AppLogger.LOGGER.info(
                            String.format(
                                    "Ответ отправлен клиенту %s",
                                    clientAddress
                            )
                    );
                }
            }
        } catch (BindException e) {
            AppLogger.LOGGER.log(Level.SEVERE,"Порт уже занят");
            System.exit(1);
        } catch (IOException e) {
            AppLogger.LOGGER.log(Level.SEVERE,"Ошибка сети", e);
        }
    }

    private void saveCollection() {
        commandInvoker.invokeLocalCommand(commandInvoker.getSaveCommandName());
    }

    @Override
    public void onShutdown() {
        shutdown = true;
    }
}
