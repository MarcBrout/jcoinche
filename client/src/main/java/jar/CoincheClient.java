package jar;

import eu.epitech.jcoinche.BaseCard;
import eu.epitech.jcoinche.CoincheProtocol;
import eu.epitech.jcoinche.CoincheProtocol.Message;
import eu.epitech.jcoinche.Translator;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.writers.ConsoleWriter;
import org.pmw.tinylog.writers.FileWriter;

import java.io.IOException;


/**
 * Hello world!
 *
 */
public class CoincheClient
{
    public static void main( String[] args )
    {
        if (args.length == 2)
        {
            Configurator.defaultConfig()
                    .writer(new FileWriter("logClient.txt"))
                    .addWriter(new ConsoleWriter())
                    .level(Level.ERROR)
                    .activate();

            String host = args[0];
            int port = Integer.parseInt(args[1]);
            EventLoopGroup workers = new NioEventLoopGroup();

            try {
                try {
                Bootstrap server = new Bootstrap()
                        .group(workers)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel channel) throws Exception {
                                channel.pipeline().addLast(
                                        new ProtobufVarint32FrameDecoder(),
                                        new ProtobufDecoder(Message.getDefaultInstance()),
                                        new ProtobufVarint32LengthFieldPrepender(),
                                        new ProtobufEncoder(),
                                        new CoincheClientHandler());
                            }
                        });

                    ChannelFuture future = server.connect(host, port).sync();

                    future.channel().closeFuture().sync();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    workers.shutdownGracefully();
                }
            } finally {
                workers.shutdownGracefully();
            }
        }
        else {
            System.out.print("Usage : name.jar [IP_ADRESS] [IP_PORT]");
        }
    }
}
