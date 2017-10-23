package jar;

import eu.epitech.jcoinche.BaseCard;
import eu.epitech.jcoinche.CoincheProtocol;
import eu.epitech.jcoinche.CoincheProtocol.Message;
import eu.epitech.jcoinche.Player;
import eu.epitech.jcoinche.Translator;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.Logger;
import org.pmw.tinylog.writers.ConsoleWriter;
import org.pmw.tinylog.writers.FileWriter;

/**
 * Main Class
 *
 */
public class CoincheServer
{
    private int port;

    private CoincheServer(int _port) {
        port = _port;
    }

    private void run() throws Exception {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup workers = new NioEventLoopGroup();

        try
        {
            ServerBootstrap server = new ServerBootstrap();

            try {
                server.group(boss, workers)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                socketChannel.pipeline().addLast(
                                        new ProtobufVarint32FrameDecoder(),
                                        new ProtobufDecoder(Message.getDefaultInstance()),
                                        new ProtobufVarint32LengthFieldPrepender(),
                                        new ProtobufEncoder(),
                                        new CoincheServerHandler()
                                );
                            }
                        })
                        .option(ChannelOption.SO_BACKLOG, 128)
                        .childOption(ChannelOption.SO_KEEPALIVE, true);

                ChannelFuture future = server.bind(port).sync();
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        finally
        {
            boss.shutdownGracefully();
            workers.shutdownGracefully();
        }

    }

    public static void main(String[] args)
    {
        int port = 8080;

        Configurator.defaultConfig()
                .writer(new FileWriter("logServer.txt"))
                .addWriter(new ConsoleWriter())
                .level(Level.ERROR)
                .activate();

        if (args.length > 0)
        {
            port = Integer.parseInt(args[1]);
        }

        try
        {
            Logger.info("Server Launched");
            new CoincheServer(port).run();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}
