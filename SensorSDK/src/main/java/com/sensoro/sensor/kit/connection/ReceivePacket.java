package com.sensoro.sensor.kit.connection;

/**
 * Created by Sensoro on 15/7/28.
 */
public class ReceivePacket {
    public static final int PACKET_RECEIVING = 1;
    public static final int PACKET_RECEIVED_FINISH = 2;
    public static final int PACKET_RECEIVED_ERROR = 3;

    private static final int PACKET_HEADER_LENGTH = 3;
    private static final int PACKET_MAX_LENGTH = 20;

    private byte[] totalPacketBytes;        // all bytes
    private boolean isReceivingPacket = false;  // is receiving.
    private int totalLength = 0;    // total bytes length
    private int receivedLength = 0; // length already received.

    public int appendPacketBytes(byte[] packetBytes) {
        if (packetBytes == null) {
            return PACKET_RECEIVED_ERROR;
        }
        if (!isReceivingPacket) {
            totalLength = (packetBytes[1] + (packetBytes[2] << 8)) & 0xff;   // little end
//            totalLength = packetBytes[1] + (packetBytes[2] << 8);   // little end
            if (totalLength <= (PACKET_MAX_LENGTH - PACKET_HEADER_LENGTH)) {
                totalPacketBytes = new byte[totalLength + PACKET_HEADER_LENGTH];
                System.arraycopy(packetBytes, 0, totalPacketBytes, 0, packetBytes.length);
                // only one package.begin to parse.
                clearReceivedPacketFlags();
                return PACKET_RECEIVED_FINISH;
            } else {
                // two or more packages，continue to receive.
                totalPacketBytes = new byte[totalLength + PACKET_HEADER_LENGTH];
                System.arraycopy(packetBytes, 0, totalPacketBytes, 0, packetBytes.length);
                receivedLength = packetBytes.length;

                isReceivingPacket = true;
                return PACKET_RECEIVING;
            }
        } else {
            int currentPacketLength = packetBytes.length;
            System.arraycopy(packetBytes, 0, totalPacketBytes, receivedLength, currentPacketLength);
            receivedLength = receivedLength + currentPacketLength;
            if (receivedLength == totalPacketBytes.length) {
                // receive completely,begin to parse.
                clearReceivedPacketFlags();
                return PACKET_RECEIVED_FINISH;
            } else if (receivedLength > (totalPacketBytes.length)) {
                // receiving length error.
                clearReceivedPacketFlags();
                return PACKET_RECEIVED_ERROR;
            } else {
                // receiving is not completed.
                return PACKET_RECEIVING;
            }
        }
    }

    public byte[] getReceivedPacketBytes() {
        return totalPacketBytes;
    }

    private void clearReceivedPacketFlags() {
        totalLength = 0;
        receivedLength = 0;
        isReceivingPacket = false;
    }
}
