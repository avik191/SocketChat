package com.example.hi.socket_chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client1 extends AppCompatActivity {
    static final int SocketServerPORT = 8080;

    LinearLayout loginPanel, chatPanel;

    EditText editTextUserName, editTextAddress;
    Button buttonConnect;
    TextView chatMsg, textPort;

    EditText editTextSay;
    Button buttonSend;
    Button buttonDisconnect;

    String msgLog = "";

    ChatClientThread chatClientThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client1);

        loginPanel = (LinearLayout)findViewById(R.id.loginpanel);
        chatPanel = (LinearLayout)findViewById(R.id.chatpanel);

        editTextUserName = (EditText) findViewById(R.id.username);
        editTextAddress = (EditText) findViewById(R.id.address);
        textPort = (TextView) findViewById(R.id.port);
        textPort.setText("port: " + SocketServerPORT);
        buttonConnect = (Button) findViewById(R.id.connect);
        buttonDisconnect = (Button) findViewById(R.id.disconnect);
        chatMsg = (TextView) findViewById(R.id.chatmsg);



        editTextSay = (EditText)findViewById(R.id.say);
        buttonSend = (Button)findViewById(R.id.send);


    }

    public void send(View v)
    {
        if (editTextSay.getText().toString().equals("")) {
            return;
        }

        if(chatClientThread==null){
            return;
        }

        chatClientThread.sendMsg(editTextSay.getText().toString() + "\n");
    }

    public void connect(View v)
    {
        String textUserName = editTextUserName.getText().toString();
        if (textUserName.equals("")) {
            Toast.makeText(Client1.this, "Enter User Name",
                    Toast.LENGTH_LONG).show();
            return;
        }

        String textAddress = editTextAddress.getText().toString();
        if (textAddress.equals("")) {
            Toast.makeText(Client1.this, "Enter Addresse",
                    Toast.LENGTH_LONG).show();
            return;
        }

        msgLog = "";
        chatMsg.setText(msgLog);
        loginPanel.setVisibility(View.GONE);
        chatPanel.setVisibility(View.VISIBLE);

        chatClientThread = new ChatClientThread(
                textUserName, textAddress, SocketServerPORT);
        chatClientThread.start();
    }

    public void disconnect(View v)
    {
        if(chatClientThread==null){
            return;
        }
        chatClientThread.disconnect();
    }

    private class ChatClientThread extends Thread {

        String name;
        String dstAddress;
        int dstPort;

        String msgToSend = "";
        boolean goOut = false;

        ChatClientThread(String name, String address, int port) {
            this.name = name;
            dstAddress = address;
            dstPort = port;
        }

        @Override
        public void run() {
            Socket socket = null;
            DataOutputStream dataOutputStream = null;
            DataInputStream dataInputStream = null;

            try {
                socket = new Socket(dstAddress, dstPort);
                dataOutputStream = new DataOutputStream(
                        socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream.writeUTF(name);
                dataOutputStream.flush();

                while (!goOut) {
                    if (dataInputStream.available() > 0) {
                        msgLog += dataInputStream.readUTF();

                        Client1.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                chatMsg.setText(msgLog);
                            }
                        });
                    }

                    if(!msgToSend.equals("")){
                        dataOutputStream.writeUTF(msgToSend);
                        dataOutputStream.flush();
                        msgToSend = "";
                    }
                }

            } catch (UnknownHostException e) {
                e.printStackTrace();
                final String eString = e.toString();
                Client1.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(Client1.this, eString, Toast.LENGTH_LONG).show();
                    }

                });
            } catch (IOException e) {
                e.printStackTrace();
                final String eString = e.toString();
                Client1.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(Client1.this, eString, Toast.LENGTH_LONG).show();
                    }

                });
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                Client1.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        loginPanel.setVisibility(View.VISIBLE);
                        chatPanel.setVisibility(View.GONE);
                    }

                });
            }

        }

        private void sendMsg(String msg){
            msgToSend = msg;
        }

        private void disconnect(){
            goOut = true;
        }
    }

}
