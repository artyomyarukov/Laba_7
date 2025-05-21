package common.utility;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.CRC32;

/**
 * Утилиты для сериализации/десериализации объектов
 */


public class SerializationUtils {
    public static final int BUFFER_SIZE = 65000;
    public static final int MAX_CHUNK_SIZE = 40000;
    public static final SerializationUtils INSTANCE = new SerializationUtils();
    private static final Logger logger = LoggerFactory.getLogger(SerializationUtils.class);

    private SerializationUtils() {
    }

    public static byte[] serialize(Object obj) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
            return baos.toByteArray();
        }
    }

    public static Object deserialize(byte[] data) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            return ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Ошибка десериализации", e);
        }
    }

    public ChunksWithCRC serializeAndSplitToChunks(Object obj) throws IOException {
        byte[] data = serialize(obj);
        long crc = calculateCRC(data);
        List<byte[]> chunks = splitToChunks(data);
        return new ChunksWithCRC(chunks, crc);
    }

    private List<byte[]> splitToChunks(byte[] data) {
        List<byte[]> chunks = new ArrayList<>();
        int start = 0;
        while (start < data.length) {
            int end = Math.min(data.length, start + MAX_CHUNK_SIZE);
            byte[] chunk = Arrays.copyOfRange(data, start, end);
            chunks.add(chunk);
            start = end;
        }
        return chunks;
    }

    public byte[] copy(byte[] data, int length) {
        if (data == null || length <= 0) {
            return new byte[0];
        }

        int actualLength = Math.min(length, data.length);
        byte[] result = new byte[actualLength];
        System.arraycopy(data, 0, result, 0, actualLength);
        return result;
    }

    private long calculateCRC(byte[] data) {
        CRC32 crc = new CRC32();
        crc.update(data, 0, data.length);
        return crc.getValue();
    }

    public Object deserializeFromChunks(List<byte[]> chunks, long crcExpected) throws IOException {
        byte[] data = mergeChunks(chunks);
        long crc = calculateCRC(data);

        if (crc == crcExpected) {
            logger.debug("Чексуммы равны, получено " + crc + " , ожидалось " + crcExpected);
        }
        else {
            throw new IllegalArgumentException("Несоответсвие чексумм, получено " + crc + " , ожидалось " + crcExpected);
        }
        return deserialize(data);
    }

    private byte[] mergeChunks(List<byte[]> chunks) {
        int totalLength = 0;
        for (byte[] chunk : chunks) {
            totalLength += chunk.length;
        }

        byte[] result = new byte[totalLength];
        int offset = 0;
        for (byte[] chunk : chunks) {
            System.arraycopy(chunk, 0, result, offset, chunk.length);
            offset += chunk.length;
        }

        return result;
    }

    @Data
    @AllArgsConstructor
    public static class ChunksWithCRC {
        private final List<byte[]> chunks;
        private final long crc;
    }

    @Data
    @AllArgsConstructor
    public static class ChunksCountWithCRC implements Serializable {
        private final int chunksCount;
        private final long crc;
    }














}
