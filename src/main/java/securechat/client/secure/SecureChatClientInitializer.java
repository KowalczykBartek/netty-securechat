package securechat.client.secure;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslHandler;
import securechat.client.SecureChatClientHandler;

public class SecureChatClientInitializer extends ChannelInitializer<SocketChannel>
{
	private final SSLContext sslContext;

	public SecureChatClientInitializer(final SSLContext sslContext)
	{
		this.sslContext = sslContext;
	}

	@Override
	protected void initChannel(final SocketChannel ch) throws Exception
	{
		final SSLEngine sslEngine = sslContext.createSSLEngine("localhost", 8080);

		final ChannelPipeline pipeline = ch.pipeline();

		sslEngine.setUseClientMode(true);
		pipeline.addLast("sslHandler", new SslHandler(sslEngine));
		pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
		pipeline.addLast(new StringDecoder());
		pipeline.addLast(new StringEncoder());

		pipeline.addLast(new SecureChatClientHandler());
	}
}
