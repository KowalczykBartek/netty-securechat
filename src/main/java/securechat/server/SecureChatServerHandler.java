package securechat.server;

import java.net.InetAddress;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.GlobalEventExecutor;

public class SecureChatServerHandler extends SimpleChannelInboundHandler<String> {
    static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        ctx.pipeline().get(SslHandler.class)//
                .handshakeFuture() //
                .addListener(future -> {

                    ctx.writeAndFlush("Welcome to " + InetAddress.getLocalHost().getHostName() + " secure chat service!\n");

                    ctx.writeAndFlush("Your session is protected by " +
                            ctx.pipeline()//
                                    .get(SslHandler.class)//
                                    .engine().getSession().getCipherSuite() + " cipher suite.\n");

                    channels.add(ctx.channel());
                });
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final String msg) throws Exception {

        channels.forEach(c -> {
            if (c != ctx.channel()) {
                c.writeAndFlush("[" + ctx.channel().remoteAddress() + "] " + msg + '\n');
            } else {
                c.writeAndFlush("[you] " + msg + '\n');
            }
        });


        if ("bye".equals(msg.toLowerCase())) {
            ctx.close();
        }
    }
}
