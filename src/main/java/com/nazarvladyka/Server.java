package com.nazarvladyka;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

public class Server {
    static HashMap<String, String> channels = new HashMap<>();
    static ArrayList<ArrayList> requests = new ArrayList<>();
    static ArrayList<String> request;

    static int channelId = 0;


    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        Selector selector = Selector.open();
        SocketAddress socketAddress = new InetSocketAddress("localhost", 8090);

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

                    log("USER" + channelId + " connected, "+ "[remote address : " +  acceptSocketChanel.getRemoteAddress() + "]");
                    channelId++;
                    acceptSocketChanel.register(selector, SelectionKey.OP_READ);
                } else if (selectionKey.isReadable()) {
                    SocketChannel readSocketChannel = (SocketChannel) selectionKey.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(50);

                    readSocketChannel.read(byteBuffer);

                    String outString = new String(byteBuffer.array());
                    request = new ArrayList<>(Arrays.asList(outString.split(" : ")));
                    requests.add(request);

                    log("INCOMING: " + "USER" + request.get(2) + " sent to USER" + request.get(3) + " MESSAGE : " + "\'" + request.get(4) + "\'" +
                            "| phase = " + request.get(1) + "| messageId = " + request.get(0));

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
                            log("OUTGOING: " + "USER" + request.get(2) + " sent to USER" + request.get(3) + " MESSAGE : " + "\'" + request.get(4) + "\'" +
                                    "| phase = " + request.get(1) + "| messageId = " + request.get(0));
                            requests.remove(request);
                        }
                    }
                    byteBuffer.clear();
                    byteBuffer.flip();
                    writeSocketChannel.register(selector, SelectionKey.OP_READ);
                }
                selectionKeyIterator.remove();
            }
        }
    }

    private static void log(String msg) {
        System.out.println(msg);
    }
}