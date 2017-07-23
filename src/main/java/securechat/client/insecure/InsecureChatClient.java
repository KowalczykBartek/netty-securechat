package securechat.client.insecure;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class InsecureChatClient
{


	public static void main(String... args) throws Exception
	{
		System.setProperty("javax.net.debug", "all");

		final EventLoopGroup group = new NioEventLoopGroup();

		try
		{
			final Bootstrap b = new Bootstrap();
			b.group(group)//
					.channel(NioSocketChannel.class)//
					.handler(new InsecureChatClientInitializer());

			final Channel ch = b.connect("127.0.0.1", 8080).sync().channel();

			ChannelFuture lastWriteFuture = null;

			final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

			for (; ; )
			{
				final String line = in.readLine();

				if (line == null)
				{
					break;
				}

				lastWriteFuture = ch.writeAndFlush(line + "\r\n");

				if ("bye".equals(line.toLowerCase()))
				{
					ch.closeFuture().sync();
					break;
				}
			}

			if (lastWriteFuture != null)
			{
				lastWriteFuture.sync();
			}
		}
		catch (Exception e)
		{
			System.err.println(e);
		}
		finally
		{
			group.shutdownGracefully();
		}
	}
}
