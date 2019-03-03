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
                newMessage[i] = (char) (cMessage[i] ^ cKey[i % keyLength]);
            }

            return new String(newMessage);
        } catch (Exception e) {
            return null;
        }
    }
}