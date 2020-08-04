package cn.zoyua.client.netty;


import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Sharable
public class ClientHandler extends SimpleChannelInboundHandler<String> {
    private static volatile String response = "";
    private static Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        response = msg;
        logger.info(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    public static String getResp() {
        return response;
    }

    public static void setResponse(String res) {
        response = res;
    }

}
