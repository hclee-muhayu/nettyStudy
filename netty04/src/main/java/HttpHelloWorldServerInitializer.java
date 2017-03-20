import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;

/**
 * Created by hyecheon on 2017. 3. 20..
 */
public class HttpHelloWorldServerInitializer extends ChannelInitializer<io.netty.channel.socket.SocketChannel> {
    private final SslContext sslContext;

    public HttpHelloWorldServerInitializer(SslContext sslContext) {
        this.sslContext = sslContext;
    }

    protected void initChannel(io.netty.channel.socket.SocketChannel ch) throws Exception {
        final ChannelPipeline pipeline = ch.pipeline();
        if (sslContext != null) {
            pipeline.addLast(sslContext.newHandler(ch.alloc()));
        }
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpHelloWorldServerHandler());
    }
}
