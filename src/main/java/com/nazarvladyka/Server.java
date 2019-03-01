package com.nazarvladyka;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

import org.apache.log4j.Logger;


public class Server {
//    static HashMap<String, String> mapId = new HashMap<>();
//    static Logger log = Logger.getLogger(Server.class.getName());

    static HashMap<String, String> channels = new HashMap<>();
    static ArrayList<ArrayList> requests = new ArrayList<>();
    static ArrayList<String> request;

    static int channelId = 0;


    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        Selector selector = Selector.open();
        SocketAddress socketAddress = new InetSocketAddress("localhost", 8078);

        serverSocketChannel.bind(socketAddress);
        serverSocketChannel.configureBlocking(false);

        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while(true) {
            selector.select();

            Set<SelectionKey> selectionKeySet = selector.selectedKeys();
            Iterator<SelectionKey> selectionKeyIterator = selectionKeySet.iterator();

            while(selectionKeyIterator.hasNext()) {
                SelectionKey selectionKey = selectionKeyIterator.next();

                if(selectionKey.isAcceptable()) {
                    SocketChannel acceptSocketChanel = serverSocketChannel.accept();
                    acceptSocketChanel.configureBlocking(false);

                    channels.put(channelId + "", acceptSocketChanel.getRemoteAddress().toString());
//                    System.out.println("channelId - " + channelId + "acceptSocketChanel.getRemoteAddress().toString() - " + acceptSocketChanel.getRemoteAddress().toString());

//                    if(channelId == 0) {
//                        System.out.println("Connection accepted " + acceptSocketChanel.getRemoteAddress());
//                        acceptSocketChanel.register(selector, SelectionKey.OP_READ);
//                    } else if(channelId == 1) {
//                        System.out.println("We will write here " + acceptSocketChanel.getRemoteAddress());
//                        acceptSocketChanel.register(selector, SelectionKey.OP_WRITE);
//                    }

                    System.out.println("Connection accepted " + acceptSocketChanel.getRemoteAddress());
                    acceptSocketChanel.register(selector, SelectionKey.OP_READ);
                    channelId++;

                } else if (selectionKey.isReadable()) {
                    SocketChannel readSocketChannel = (SocketChannel) selectionKey.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(50);

                    readSocketChannel.read(byteBuffer);

                    //this to parse info from input /////////split string
                    String outString = new String(byteBuffer.array());
                    request = new ArrayList<>(Arrays.asList(outString.split(" : ")));
                    requests.add(request);

                    System.out.println(requests);
                    System.out.println("isReadable: " + readSocketChannel.getRemoteAddress());
                    System.out.println("___________________");

                    byteBuffer.clear();

                    readSocketChannel.register(selector, SelectionKey.OP_WRITE);
                } else if (selectionKey.isWritable()) {
                    SocketChannel writeSocketChannel = (SocketChannel) selectionKey.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(50);

                    for (ArrayList request : requests) {
                        if(writeSocketChannel.getRemoteAddress().toString().equals(channels.get(request.get(3).toString()))) {
                            String msg = request.get(4).toString();

                            byte[] bytes = msg.getBytes();
                            writeSocketChannel.write(ByteBuffer.wrap(bytes));
                            System.out.println("String msg = request.get(4).toString() - " + request.get(4).toString());
                            System.out.println("Sent from " + channels.get(request.get(2).toString()));
                            System.out.println(request);
                            System.out.println("Sent to " + writeSocketChannel.getRemoteAddress());
                            requests.remove(request);
                        }
                    }

                    byteBuffer.clear();
                    byteBuffer.flip();
//                    System.out.println("isWritable: " + writeSocketChannel.getRemoteAddress());

                    String str = new String(byteBuffer.array());
                    System.out.println(str);
                    byteBuffer.clear();
                    writeSocketChannel.register(selector, SelectionKey.OP_READ);
                }
                selectionKeyIterator.remove();
            }
        }
    }
}