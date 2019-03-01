package com.nazarvladyka;

public class Encrypt {

    public static String xorMessage(String message, String key) {
        try {
            if (message == null || key == null) return null;

            char[] cKey = key.toCharArray();
            char[] cMessage = message.toCharArray();

            int messageLength = cMessage.length;
            int keyLength = cKey.length;
            char[] newMessage = new char[messageLength];

            for (int i = 0; i < messageLength; i++) {
                newMessage[i] = (char)(cMessage[i] ^ cKey[i % keyLength]);
            }

            return new String(newMessage);
        } catch (Exception e) {
            return null;
        }
    }

    //    public static void main(String[] args) {
//        String txt = "716253";
//        String key1 = "082738";
//        String key2 = "526890";
//
//        System.out.println(txt);
//        String txt1 = xorMessage(txt, key1);
//        System.out.println(txt1);
//        String txt12 = xorMessage(txt1, key2);
//        System.out.println(txt12);
//        String txt2 = xorMessage(txt12, key1);
//        System.out.println(txt2);
//        String txt0 = xorMessage(txt2, key2);
//        System.out.println(txt0);
//    }
}