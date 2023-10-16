package com.example.chatbot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Request;
import okio.Timeout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private RecyclerView chatsRV;
    private EditText userMsgEdt;
    private FloatingActionButton sendMsgFAB;
      private final String BOT_KEY="bot";
    private final String USER_KEY="user";
    private ArrayList<ChatsModal> chatsModalArrayList;
     private ChatRVAdapter chatRVAdapter;
      @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chatsRV = findViewById(R.id.idRVChats);
        userMsgEdt= findViewById(R.id.idEditMessage);
        sendMsgFAB=findViewById(R.id.idFABSend);
        chatsModalArrayList = new ArrayList<>();



          chatRVAdapter = new ChatRVAdapter(chatsModalArrayList,this );
          LinearLayoutManager manager= new LinearLayoutManager(this);
            chatsRV.setLayoutManager(manager);
            chatsRV.setAdapter(chatRVAdapter);


            sendMsgFAB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userMsgEdt.getText().toString().isEmpty()){
                        Toast.makeText(MainActivity.this, "Please enter your message", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    getResponse(userMsgEdt.getText().toString());
                    userMsgEdt.setText("");
                }
            });
    }
    private void getResponse(String message){
        chatsModalArrayList.add(new ChatsModal(message,USER_KEY));
        chatRVAdapter.notifyDataSetChanged();
        String url= "http://api.brainshop.ai/get?bid=177834&key=R823cs2y8ehIkWfX&uid=[uid]&msg="+message;
        String BASE_URL="http://api.brainshop.ai/";
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<MsgModal> call= retrofitAPI.getMessage(url);
        call.enqueue(new Callback<MsgModal>() {
            @Override
            public void onResponse(Call<MsgModal> call, Response<MsgModal> response) {
                if(response.isSuccessful()){
                    MsgModal modal=response.body();
                    chatsModalArrayList.add(new ChatsModal(modal.getCnt(),BOT_KEY));
                    chatRVAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<MsgModal> call, Throwable t) {
                chatsModalArrayList.add(new ChatsModal("Please revert your question",BOT_KEY));
                chatRVAdapter.notifyDataSetChanged();
            }
        });

            }
}