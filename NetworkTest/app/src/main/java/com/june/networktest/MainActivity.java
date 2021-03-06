package com.june.networktest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView textView, textView2;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.edittext);
        textView = findViewById(R.id.send_text);
        textView2 = findViewById(R.id.server_text);

        Button sendButton = findViewById(R.id.send_btn);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String data = editText.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        send(data);
                    }
                }).start();
            }
        });

        Button serverButton = findViewById(R.id.server_btn);
        serverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        startServer();
                    }
                }).start();
            }
        });
    }

    public void printClientLog(final String data) {
        Log.d("MainActivity", data);
        handler.post(new Runnable() {
            @Override
            public void run() {
                textView.append(data+"\n");
            }
        });
    }

    public void printServerLog(final String data) {
        Log.d("MainActivity", data);

        handler.post(new Runnable() {
            @Override
            public void run() {
                textView2.append(data+"\n");
            }
        });
    }

    public void send(String data) {
        try {
            int portNumber = 5001;
            Socket socket = new Socket("localhost", portNumber);
            printClientLog("?????? ?????????");

            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(data);
            outputStream.flush();
            printClientLog("????????? ?????????");

            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            printClientLog("??????????????? ??????: "+inputStream.readObject());
            socket.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void startServer() {
        try {
            int portNumber = 5001;

            ServerSocket server = new ServerSocket(portNumber);
            printServerLog("?????? ?????????: "+portNumber);

            while (true) {
                Socket socket = server.accept();
                InetAddress clientHost = socket.getLocalAddress();
                int clientPort = socket.getPort();
                printServerLog("??????????????? ?????????: "+clientHost+" : "+clientPort);

                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                Object obj = inputStream.readObject();
                printServerLog("????????? ??????: "+obj);

                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                outputStream.writeObject(obj+" from Server.");
                outputStream.flush();
                printServerLog("????????? ??????");

                socket.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}