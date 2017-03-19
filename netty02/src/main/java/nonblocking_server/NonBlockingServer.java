package nonblocking_server;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

/**
 * Created by hyecheon on 2017. 3. 19..
 */
public class NonBlockingServer {
    private Map<SocketChannel, List<byte[]>> keepDataTrack = new HashMap<>();
    private ByteBuffer buffer = ByteBuffer.allocate(2 * 1024);

    private void startEchoServer() {
        try (final Selector selector = Selector.open();
             final ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            if ((serverSocketChannel.isOpen()) && (selector.isOpen())) {
                serverSocketChannel.configureBlocking(false);
                serverSocketChannel.bind(new InetSocketAddress(8888));

                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
                System.out.println("접속 대기중");
                while (true) {
                    selector.select();
                    final Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                    while (keys.hasNext()) {
                        final SelectionKey key = keys.next();
                        keys.remove();
                        if (!key.isValid()) {
                            continue;
                        }
                        if (key.isAcceptable()) {
                            this.acceptOP(key, selector);
                        } else if (key.isReadable()) {
                            this.readOP(key);
                        } else if (key.isWritable()) {
                            this.writeOP(key);
                        }
                    }
                }
            } else {
                System.out.println("서버 소캣을 생성하지 못했습니다.");
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    private void writeOP(SelectionKey key) {
        final SocketChannel socketChannel = (SocketChannel) key.channel();

        final List<byte[]> channelData = keepDataTrack.get(socketChannel);
        channelData.forEach(data -> {
            try {
                socketChannel.write(ByteBuffer.wrap(data));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        key.interestOps(SelectionKey.OP_READ);
    }

    private void readOP(SelectionKey key) {
        try {
            final SocketChannel socketChannel = (SocketChannel) key.channel();
            buffer.clear();
            int numRead = -1;
            try {
                numRead = socketChannel.read(buffer);
            } catch (IOException e) {
                System.err.println(e);
            }
            if (numRead == -1) {
                this.keepDataTrack.remove(socketChannel);
                System.out.println("클라이언트 연결 종료 : " + socketChannel.getRemoteAddress());
                socketChannel.close();
                key.cancel();
                return;
            }
            byte[] data = new byte[numRead];
            System.arraycopy(buffer.array(), 0, data, 0, numRead);
            System.out.println(new String(data, "UTF-8") + "from" + socketChannel.getRemoteAddress());
            doEchoJob(key, data);
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    private void doEchoJob(SelectionKey key, byte[] data) {
        final SocketChannel socketChannel = (SocketChannel) key.channel();
        final List<byte[]> channelData = keepDataTrack.get(socketChannel);
        channelData.add(data);
        key.interestOps(SelectionKey.OP_WRITE);
    }

    private void acceptOP(SelectionKey key, Selector selector) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        final SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);

        System.out.println("클라이언트 연결됨 : " + socketChannel.getRemoteAddress());

        keepDataTrack.put(socketChannel, new ArrayList<>());
        socketChannel.register(selector, SelectionKey.OP_READ);

    }

    public static void main(String[] args) {
        final NonBlockingServer nonBlockingServer = new NonBlockingServer();
        nonBlockingServer.startEchoServer();
    }
}
