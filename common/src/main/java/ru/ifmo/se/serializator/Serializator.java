package ru.ifmo.se.serializator;

import java.io.*;
import java.nio.ByteBuffer;

public class Serializator {

    private Serializator() {}

    public static byte[] serialize(Object obj) throws SerializationException {
        if (obj == null) {
            throw new SerializationException("Переданный для сериализации объект - null");
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {

            oos.writeObject(obj);
            oos.flush();
            return baos.toByteArray();

        } catch (IOException e) {
            throw new SerializationException(
                    "IO исключение при сериализации: " + e.getMessage());
        }
    }

    public static Object deserialize(ByteBuffer buffer) throws SerializationException {
        if (buffer == null) {
            throw new SerializationException("Переданный для десериализации объект - null");
        }

        try {
            byte[] data = extractBytes(buffer);

            try (ObjectInputStream ois =
                         new ObjectInputStream(new ByteArrayInputStream(data))) {

                return ois.readObject();
            }

        } catch (IOException | ClassNotFoundException e) {
            throw new SerializationException(
                    "IO исключение при десериализации: " + e.getMessage());
        }
    }

    private static byte[] extractBytes(ByteBuffer buffer) {
        ByteBuffer readBuffer = buffer;

        if (buffer.position() != 0 && buffer.limit() == buffer.capacity()) {
            readBuffer = buffer.duplicate();
            readBuffer.flip();
        }

        byte[] data = new byte[readBuffer.remaining()];
        readBuffer.get(data);

        return data;
    }
}
