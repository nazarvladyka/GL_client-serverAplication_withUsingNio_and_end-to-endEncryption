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
    volatile static String message = "";
    static String key = "";

    public static void main(String[] args) throws IOException, InterruptedException {
        SocketAddress socketAddress = new InetSocketAddress("localhost", 8078);
        SocketChannel socketChannel = SocketChannel.open(socketAddress);

        Runnable runnable = () -> {
            do {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                try {
                    message = reader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } while (true);
        };
        Thread thread = new Thread(runnable);
        thread.start();

        Runnable runnable1 = () -> {

            do {
                ByteBuffer byteBuffer = ByteBuffer.allocate(50);
                try {
                    socketChannel.read(byteBuffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(new String(byteBuffer.array()));
            } while (true);
        };
        Thread thread1 = new Thread(runnable1);
        thread1.start();

        do {
//            System.out.println("d0...");
            Random random = new Random();
            key = random.nextInt(10) + "";
//                message = reader.readLine();
//                message = Encrypt.xorMessage(reader.readLine(), key);
//                phase++;

            String requestString = messageId + " : " + phase + " : " + clientId + " : " + receiverId + " : " + message;

            ByteBuffer byteBuffer = ByteBuffer.allocate(50);

            if (!message.equals("")) {
                byte[] bytes = requestString.getBytes();
                byteBuffer = ByteBuffer.wrap(bytes);

                socketChannel.write(byteBuffer);

                byteBuffer.clear();
                message = "";
            }
//            byteBuffer = ByteBuffer.allocate(50);
//            socketChannel.read(byteBuffer);
//            System.out.println(socketChannel.read(byteBuffer));
//            System.out.println(new String(byteBuffer.array()));

            byteBuffer.clear();
        } while (true); // here has to be boolean, which will end the cycle
    }
}