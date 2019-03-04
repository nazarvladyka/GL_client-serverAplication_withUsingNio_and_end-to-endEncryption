package com.nazarvladyka;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

public class Server {
    static HashMap<String, String> clients = new HashMap<>();
    static ArrayList<ArrayList> requests = new ArrayList<>();
    static ArrayList<String> request;

    static int clientId = 0;
    static String senderId = "";
    static String receiverId = "";
    static String messageId = "";
    static String phase = "";
    volatile static String message = "";

    static String receiverAddress = "";

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

                    clients.put(clientId + "", acceptSocketChanel.getRemoteAddress().toString());

                    log("USER" + clientId + " connected, "+ "[remote address : " +  acceptSocketChanel.getRemoteAddress() + "]");
                    clientId++;
                    acceptSocketChanel.register(selector, SelectionKey.OP_READ);
                } else if (selectionKey.isReadable()) {
                    SocketChannel readSocketChannel = (SocketChannel) selectionKey.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(50);

                    readSocketChannel.read(byteBuffer);

                    String outString = new String(byteBuffer.array());
                    request = new ArrayList<>(Arrays.asList(outString.split(" : ")));
                    requests.add(request);

                    senderId = getKeyByValue(clients, readSocketChannel.getRemoteAddress().toString());
                    receiverId = request.get(2);
                    message = request.get(3);
                    phase = request.get(1);
                    messageId = request.get(0);

                    request.add(senderId);

                    log("INCOMING: " + "USER" + senderId + " sent to USER" + receiverId + " MESSAGE : " + "\'" + message + "\'" +
                            "| phase = " + phase + "| messageId = " + messageId);

                    byteBuffer.clear();

                    readSocketChannel.register(selector, SelectionKey.OP_WRITE);
                } else if (selectionKey.isWritable()) {
                    SocketChannel writeSocketChannel = (SocketChannel) selectionKey.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(50);

                    for (ArrayList request : requests) {
                        receiverAddress = clients.get(request.get(2));
                        if(writeSocketChannel.getRemoteAddress().toString().equals(receiverAddress)) {
                            senderId = request.get(4).toString();
                            receiverId = request.get(2).toString();
                            message = request.get(3).toString();
                            phase = request.get(1).toString();
                            messageId = request.get(0).toString();

                            byte[] bytes = message.getBytes();
                            writeSocketChannel.write(ByteBuffer.wrap(bytes));

                            log("OUTGOING: " + "USER" + senderId + " sent to USER" + receiverId + " MESSAGE : " + "\'" + message + "\'" +
                                    "| phase = " + phase + "| messageId = " + messageId);
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

    private static String getKeyByValue(HashMap<String, String> map, String key) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue().equals(key)) {
                return entry.getKey();
            }
        }
        return "";
    }
}