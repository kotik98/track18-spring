package ru.track.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;
import ru.track.io.vendor.ReferenceTaskImplementation;

import java.io.*;
import java.net.InetSocketAddress;

import static java.lang.Math.abs;

public final class TaskImplementation implements FileEncoder {

    /**
     * @param finPath  where to read binary data from
     * @param foutPath where to write encoded data. if null, please create and use temporary file.
     * @return file to read encoded data from
     * @throws IOException is case of input/output errors
     */

    @NotNull
    public File encodeFile(@NotNull String finPath, @Nullable String foutPath) throws IOException {
        //finPath = "C:\\Users\\zahar\\git\\track18-spring\\L2-objects\\image_256.png";
        FileInputStream inputStream = new FileInputStream(finPath);
        StringBuilder result = new StringBuilder();
        int bufferSize = 16002;
        byte[] buffer = new byte[bufferSize];
        int a, b, c, d;
        while (inputStream.available() > 0) {
            if (inputStream.available() < bufferSize) {
                bufferSize = inputStream.available();
            }
            inputStream.read(buffer, 0, bufferSize );
            if (bufferSize == 1) {
                a = (buffer[0] & 0b11111111) >> 2;
                b = (buffer[0] & 0b00000011) << 4;
                result.append(toBase64[a]);
                result.append(toBase64[b]);
                result.append("=");
                result.append("=");
                break;
            }
            if (bufferSize == 2) {
                a = (buffer[0] & 0b11111111) >> 2;
                b = ((buffer[0] & 0b00000011) << 4) + ((buffer[1] & 0b11110000) >> 4);
                c = ((buffer[1] & 0b00001111) << 2);
                result.append(toBase64[a]);
                result.append(toBase64[b]);
                result.append(toBase64[c]);
                result.append("=");
                break;
            }
            for (int i = 2; i < bufferSize ; i += 3) {
                a = (buffer[i - 2] & 0b11111111) >> 2;
                b = ((buffer[i - 2] & 0b00000011) << 4) + ((buffer[i -1] & 0b11110000) >> 4);
                c = ((buffer[i - 1] & 0b00001111) << 2) + ((buffer[i] & 0b11000000) >> 6);
                d = (buffer[i] & 0b00111111);
                result.append(toBase64[a]);
                result.append(toBase64[b]);
                result.append(toBase64[c]);
                result.append(toBase64[d]);
                if (bufferSize - i == 2) {
                    a = (buffer[i + 1] & 0b11111111) >> 2;
                    b = (buffer[i + 1] & 0b00000011) << 4;
                    result.append(toBase64[a]);
                    result.append(toBase64[b]);
                    result.append("=");
                    result.append("=");
                    break;
                }
                if (bufferSize - i == 3) {
                    a = (buffer[i + 1] & 0b11111111) >> 2;
                    b = ((buffer[i + 1] & 0b00000011) << 4) + ((buffer[i + 2] & 0b11110000) >> 4);
                    c = ((buffer[i + 2] & 0b00001111) << 2);
                    result.append(toBase64[a]);
                    result.append(toBase64[b]);
                    result.append(toBase64[c]);
                    result.append("=");
                }
            }
        }
        inputStream.close();
        if (foutPath != null) {
            File out = new File(foutPath);
            FileWriter writer = new FileWriter(foutPath);
            writer.write(result.toString());
            out.deleteOnExit();
            return out;
        }else {
            File out = File.createTempFile("123", null, null);
            OutputStream outputStream =  new BufferedOutputStream(new FileOutputStream(out));
            outputStream.write(result.toString().getBytes());
            outputStream.close();
            out.deleteOnExit();
            return out;
        }
    }

    private static final char[] toBase64 = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    };

    public static void main(String[] args) throws Exception {
        final FileEncoder encoder = new TaskImplementation();
        // NOTE: open http://localhost:9000/ in your web browser
        (new Bootstrapper(args, encoder))
                .bootstrap("", new InetSocketAddress("127.0.0.1", 9000));
    }

}
