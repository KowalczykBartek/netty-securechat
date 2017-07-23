package securechat.server;

import static securechat.SSLContextUtil.createAndInitSSLContext;

import javax.net.ssl.SSLContext;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class SecureChatServer {
    public static void main(String... args) throws Exception {
        System.setProperty("javax.net.debug", "all");

        SSLContext sslContext = createAndInitSSLContext("server.jks");

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            final ServerBootstrap b = new ServerBootstrap();

            b.group(bossGroup, workerGroup)//
                    .channel(NioServerSocketChannel.class) //
                    .handler(new LoggingHandler(LogLevel.INFO)) //
                    .childHandler(new SecureChatServerInitializer(sslContext));

            b.bind(8080).sync().channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
