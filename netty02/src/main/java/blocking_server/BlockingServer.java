package blocking_server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by hyecheon on 2017. 3. 19..
 */

public class BlockingServer {
    public static void main(String[] args) throws IOException {
        final BlockingServer server = new BlockingServer();
        server.run();
    }

    private void run() throws IOException {
        ServerSocket server = new ServerSocket(8888);
        System.out.println("접속 대기중");
        while (true) {
            final Socket socket = server.accept();
            System.out.println("클라이언트 연결됨");
            final OutputStream outputStream = socket.getOutputStream();
            final InputStream inputStream = socket.getInputStream();
            while (true) {
                try {
                    final int request = inputStream.read();
                    outputStream.write(request);
                } catch (IOException e) {
                    break;
                }
            }
        }
    }
}
