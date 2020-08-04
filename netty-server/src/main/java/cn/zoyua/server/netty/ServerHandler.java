package cn.zoyua.server.netty;

import cn.zoyua.server.service.Hosts2Json;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

@Sharable
public class ServerHandler extends SimpleChannelInboundHandler<String> {
    private static Logger logger = LoggerFactory.getLogger(ServerHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 为新连接发送庆祝
        logger.info("channelActive----Welcome! It is " + new Date() + " now.\r\n");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        logger.info(s);
        String[] arr = s.split("_");
        String response="";
        boolean close = false;
        if("hostList".equals(arr[0])){
            try {
                response = Hosts2Json.changeHost2Str()+"\r\n";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if ("update".equals(arr[0])){
            try {
                Hosts2Json.writeHosts(arr[1]);
                response = "success" + "\r\n";
            } catch (Exception e) {
                response = "fail" + "\r\n";
                e.printStackTrace();
            }
        }else {
            response = "receive other.\r\n";
        }

        ChannelFuture future = channelHandlerContext.writeAndFlush(response);

        if (close) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }
}
