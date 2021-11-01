package com.team.mychat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.team.mychat.R;
import com.team.mychat.activities.ChatActivity;
import com.team.mychat.activities.MainActivity;
import com.team.mychat.activities.UserActivity;
import com.team.mychat.adapter.RecentConversionsAdapter;
import com.team.mychat.listener.ConversionListener;
import com.team.mychat.models.ChatMessage;
import com.team.mychat.models.User;
import com.team.mychat.utilities.Constants;
import com.team.mychat.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment implements ConversionListener {

    private View view;
    private FloatingActionButton btnAdd;
    private List<ChatMessage> conversations;
    private RecentConversionsAdapter conversionsAdapter;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private PreferenceManager preferenceManager;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home,container,false);
        preferenceManager = new PreferenceManager(getActivity());
        initItem();
        setListener();
        listenConversations();
        return view;
    }

    private void initItem(){
        btnAdd = view.findViewById(R.id.fabNewChat);
        conversations = new ArrayList<>();
        conversionsAdapter = new RecentConversionsAdapter(conversations, this);
        recyclerView = view.findViewById(R.id.conversationsRecyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView.setAdapter(conversionsAdapter);
        db = FirebaseFirestore.getInstance();
    }

    private void setListener(){
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UserActivity.class);
            startActivity(intent);
        });
    }

    private final EventListener<QuerySnapshot> eventListener = ((value, error) -> {
       if(error !=null){
           return;
       }
       if (value !=null){
           for (DocumentChange documentChange : value.getDocumentChanges()){
               if(documentChange.getType() == DocumentChange.Type.ADDED){
                   String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                   String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                   ChatMessage chatMessage = new ChatMessage();
                   chatMessage.senderId = senderId;
                   chatMessage.receiverId = receiverId;
                   if(preferenceManager.getString(Constants.KEY_USER_ID).equals(senderId)){
                       chatMessage.conversionImage = documentChange.getDocument().getString(Constants.KEY_RECEIVER_IMAGE);
                       chatMessage.conversionName = documentChange.getDocument().getString(Constants.KEY_RECEIVER_NAME);
                       chatMessage.conversionId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                   } else {
                       chatMessage.conversionImage = documentChange.getDocument().getString(Constants.KEY_SENDER_IMAGE);
                       chatMessage.conversionName = documentChange.getDocument().getString(Constants.KEY_SENDER_NAME);
                       chatMessage.conversionId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                   }
                   chatMessage.message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSSAGE);
                   chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                   conversations.add(chatMessage);
               } else if (documentChange.getType() == DocumentChange.Type.MODIFIED){
                   for (int i=0;i< conversations.size();i++){
                       String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                       String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                       if(conversations.get(i).senderId.equals(senderId) && conversations.get(i).receiverId.equals(receiverId)){
                           conversations.get(i).message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSSAGE);
                           conversations.get(i).dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                           break;
                       }
                   }
               }
           }
           Collections.sort(conversations,(obj1,obj2)->obj2.dateObject.compareTo(obj1.dateObject));
           conversionsAdapter.notifyDataSetChanged();
           recyclerView.smoothScrollToPosition(0);
           recyclerView.setVisibility(View.VISIBLE);
           progressBar.setVisibility(view.GONE);
       }
    });

    private void listenConversations(){
        db.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        db.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_RECEIVER_ID,preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    @Override
    public void onConversionCLicked(User user) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER,user);
        startActivity(intent);
    }
}
