import io.netty.channel.*;

import java.net.InetAddress;
import java.time.LocalDateTime;

/**
 * Created by hyecheon on 2017. 3. 22..
 */
@ChannelHandler.Sharable
public class TelnetServerHandler extends SimpleChannelInboundHandler<String> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.write(InetAddress.getLocalHost().getHostName() + " 서버에 접속 하셨습니다.\r\n");
        ctx.write("현재 시간은 " + LocalDateTime.now() + "입니다.\r\n");
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String request) throws Exception {
        String response;
        boolean close = false;
        if (request.isEmpty()) {
            response = "명력을 입력해 주세요 . \r\n";
        } else if ("bye".equalsIgnoreCase(request)) {
            response = "안녕히 가세요 ! \r\n";
            close = true;
        } else {
            response = "입력하신 명령은 '" + request + "'입니다 \r\n";
        }
        final ChannelFuture future = ctx.write(response);
        if (close) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
