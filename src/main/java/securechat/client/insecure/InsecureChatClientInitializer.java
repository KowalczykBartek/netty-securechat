package securechat.client.insecure;

import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslHandler;
import securechat.client.SecureChatClientHandler;

public class InsecureChatClientInitializer extends ChannelInitializer<SocketChannel> {
    /*
     * Trust to all certs
     */
    private final TrustManager trm = new X509TrustManager() {
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public void checkClientTrusted(X509Certificate[] certs, String authType) {

        }

        public void checkServerTrusted(X509Certificate[] certs, String authType) {
        }
    };


    @Override
    protected void initChannel(final SocketChannel ch) throws Exception {
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, new TrustManager[]{trm}, null);

        final SSLEngine sslEngine = sc.createSSLEngine();
        sslEngine.setUseClientMode(true);

        final ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast("sslHandler", new SslHandler(sslEngine));
        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        pipeline.addLast(new StringDecoder());
        pipeline.addLast(new StringEncoder());

        pipeline.addLast(new SecureChatClientHandler());
    }
}
