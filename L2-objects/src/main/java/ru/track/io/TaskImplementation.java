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
        File input = new File(finPath);
        StringBuilder result = new StringBuilder();
        int bufferSize = 2001;
        byte[] buffer = new byte[3];
        int a, b, c, d;
        File out;
        if (foutPath != null) {
            out = new File(foutPath);
        }else {
            out = File.createTempFile("123", null, null);
        }
        out.deleteOnExit();
        try ( BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(out), bufferSize);
              BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(input), bufferSize * 4 / 3)
        ){
            int buff = bufferedInputStream.read(buffer, 0, 3);
            while (buff != -1) {
                if (buff == 1) {
                    a = (buffer[0] & 0b11111111) >> 2;
                    b = (buffer[0] & 0b00000011) << 4;
                    bufferedOutputStream.write(toBase64[a]);
                    bufferedOutputStream.write(toBase64[b]);
                    bufferedOutputStream.write('=');
                    bufferedOutputStream.write('=');
                }
                if (buff == 2) {
                    a = (buffer[0] & 0b11111111) >> 2;
                    b = ((buffer[0] & 0b00000011) << 4) + ((buffer[1] & 0b11110000) >> 4);
                    c = ((buffer[1] & 0b00001111) << 2);
                    bufferedOutputStream.write(toBase64[a]);
                    bufferedOutputStream.write(toBase64[b]);
                    bufferedOutputStream.write(toBase64[c]);
                    bufferedOutputStream.write('=');
                }
                if (buff == 3) {
                    a = (buffer[0] & 0b11111111) >> 2;
                    b = ((buffer[0] & 0b00000011) << 4) + ((buffer[1] & 0b11110000) >> 4);
                    c = ((buffer[1] & 0b00001111) << 2) + ((buffer[2] & 0b11000000) >> 6);
                    d = (buffer[2] & 0b00111111);
                    bufferedOutputStream.write(toBase64[a]);
                    bufferedOutputStream.write(toBase64[b]);
                    bufferedOutputStream.write(toBase64[c]);
                    bufferedOutputStream.write(toBase64[d]);
                }
                for (int i = 0; i < buffer.length; i++) {
                    buffer[i] = 0;
                }
                buff = bufferedInputStream.read(buffer, 0, 3);
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return out;
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
