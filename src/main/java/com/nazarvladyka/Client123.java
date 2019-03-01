package com.nazarvladyka;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Random;

public class Client123 {
    static String clientId = "0";
    static int messageId = 0;
    static String receiverId = "1";
    static int phase = 0;
    static String message = "";
    static String key = "";

    public static void main(String[] args) throws IOException {
        SocketAddress socketAddress = new InetSocketAddress("localhost", 8078);
        SocketChannel socketChannel = SocketChannel.open(socketAddress);

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        do {
//            if (phase == 0) {
                Random random = new Random();
                key = random.nextInt(10) + "";
                message = reader.readLine();
//                message = Encrypt.xorMessage(reader.readLine(), key);
                phase++;
//            } else if (phase == 1) {
//
//            }


                String requestString = messageId + " : " + phase + " : " + clientId + " : " + receiverId + " : " + message;

                byte[] bytes = requestString.getBytes();
                ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
                socketChannel.write(byteBuffer);

                byteBuffer.clear();

//                byteBuffer = ByteBuffer.allocate(50);
                socketChannel.read(byteBuffer);
                System.out.println(new String(byteBuffer.array()));
//
                byteBuffer.clear();

//
        } while (true); // here has to be boolean, which will end the cycle
    }
}