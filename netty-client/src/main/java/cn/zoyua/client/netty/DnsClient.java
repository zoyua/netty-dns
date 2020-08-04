package cn.zoyua.client.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class DnsClient {
    private static Channel ch = null;

    public static Channel getCh() throws Exception {
        if (ch == null || !ch.isActive()) {
            synchronized (DnsClient.class) {
                if (ch == null || !ch.isActive()) {
                    EventLoopGroup group = new NioEventLoopGroup();
                    Bootstrap b = new Bootstrap();
                    b.group(group)
                            .channel(NioSocketChannel.class)
                            .handler(new ClientInitializer());
//                    ch = b.connect("127.0.0.1",8888).sync().channel();
                    ch = b.connect("127.0.0.1", 8888).sync().channel();
                }
            }
        }
        return ch;
    }
}
