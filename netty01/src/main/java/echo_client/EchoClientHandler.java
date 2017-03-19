package echo_client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.java.Log;

import java.nio.charset.Charset;

/**
 * Created by hyecheon on 2017. 3. 19..
 */
@Log
public class EchoClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String sendMessage = "Hello, Netty!";

        final ByteBuf messageByteBuf = Unpooled.buffer();
        messageByteBuf.writeBytes(sendMessage.getBytes());

        log.info("전송한 문자열 [ " + sendMessage + "]");
        ctx.writeAndFlush(messageByteBuf);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        final String readMessage = ((ByteBuf) msg).toString(Charset.defaultCharset());
        log.info("수신한 문자열[" + readMessage + "]");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
