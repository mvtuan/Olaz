package com.example.olaz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import org.linphone.core.Address;
import org.linphone.core.ChatMessage;
import org.linphone.core.ChatRoom;
import org.linphone.core.Core;

public class ChatActivity extends AppCompatActivity {

    private EditText mTextMessage;
    private EditText mTextToAddress;
    private ImageButton mButtonSend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mTextToAddress = findViewById(R.id.text_to_address);
        mTextMessage = findViewById(R.id.text_message);
        mButtonSend = findViewById(R.id.button_send);

        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Core core = LinphoneService.getCore();
                String[] DnsServer = {"192.168.122.40"};
                core.setDnsServers(DnsServer);
                Address address = core.interpretUrl(mTextToAddress.getText().toString());
                ChatRoom chatRoom = core.createChatRoom(address);
                if(chatRoom != null){
                    ChatMessage chatMessage;

                    chatMessage = chatRoom.createEmptyMessage();
                    chatMessage.addTextContent(mTextMessage.getText().toString());
                    if (chatMessage.getContents().length > 0) {
                        chatRoom.sendChatMessage(chatMessage);
                    }
                }
                else{
                    Log.e("ERROR: ", "Cannot create chatroom");
                }
                mTextMessage.setText("");
            }
        });
    }
}
