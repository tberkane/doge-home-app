package com.example.dogehomeapp;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * A simple TCP client for sending messages to the smart hub
 */
public final class TcpClient {
    private final String hostName;
    private final int port;

    /**
     * Constructor
     *
     * @param hostName host name
     * @param port     port nb
     */
    public TcpClient(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
    }

    /**
     * Runs the TCP client, sending a message to the smart hub
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sendMessage(String message) {
        try (Socket socket = new Socket(InetAddress.getByName(hostName), port);
             Writer writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), US_ASCII))) {
            writer.write(message);
            writer.write('\n');
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new UncheckedIOException(e);
        }
    }
}