package com.bekurir.biz.fragment;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.bekurir.biz.R;
import com.bekurir.biz.activity.ChatActivity;
import com.bekurir.biz.constants.BaseApp;
import com.bekurir.biz.constants.Constant;
import com.bekurir.biz.constants.Functions;
import com.bekurir.biz.item.MessageItem;
import com.bekurir.biz.models.MessageModels;
import com.bekurir.biz.models.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class MessageFragment extends Fragment {


    private View getView;
    private Context context;

    private RecyclerView inboxList;

    private ArrayList<MessageModels> inboxArraylist;
    private ShimmerFrameLayout shimmer;
    private DatabaseReference rootRef;

    private MessageItem inboxItem;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getView = inflater.inflate(R.layout.fragment_recycle, container, false);
        context = getContext();

        rootRef = FirebaseDatabase.getInstance().getReference();

        inboxList = getView.findViewById(R.id.inboxlist);
        shimmer = getView.findViewById(R.id.shimmerwallet);
        inboxArraylist =new ArrayList<>();
        inboxList = getView.findViewById(R.id.inboxlist);
        LinearLayoutManager layout = new LinearLayoutManager(context);
        inboxList.setLayoutManager(layout);
        inboxList.setHasFixedSize(false);
        inboxItem = new MessageItem(context, inboxArraylist, new MessageItem.OnItemClickListener() {
            @Override
            public void onItemClick(MessageModels item) {
                User loginuser = BaseApp.getInstance(context).getLoginUser();
                if (checkReadStoragepermission()) {
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra("senderid", loginuser.getId_merchant());
                    intent.putExtra("receiverid", item.getId());
                    intent.putExtra("name", item.getName());
                    intent.putExtra("tokendriver", loginuser.getToken_merchant());
                    intent.putExtra("tokenku", item.getTokenuser());
                    intent.putExtra("pic", item.getPicture());
                    requireActivity().startActivity(intent);
                }


            }
        });

        inboxList.setAdapter(inboxItem);



        getView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.hideSoftKeyboard(requireActivity());
            }
        });
        shimmershow();
        return getView;
    }

    private ValueEventListener valueEventListener;

    private Query inboxQuery;
    private void shimmershow() {
        inboxList.setVisibility(View.GONE);
        shimmer.setVisibility(View.VISIBLE);
        shimmer.startShimmerAnimation();
    }

    private void shimmertutup() {

        inboxList.setVisibility(View.VISIBLE);
        shimmer.setVisibility(View.GONE);
        shimmer.stopShimmerAnimation();
    }
    @Override
    public void onStart() {
        super.onStart();
        User loginuser = BaseApp.getInstance(context).getLoginUser();
        inboxQuery = rootRef.child("Inbox").child(loginuser.getId_merchant()).orderByChild("date");
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                shimmertutup();
                inboxArraylist.clear();

                for (DataSnapshot ds:dataSnapshot.getChildren()) {
                    MessageModels model = new MessageModels();
                    model.setId(ds.getKey());
                    model.setName(Objects.requireNonNull(ds.child("name").getValue()).toString());
                    model.setMessage(Objects.requireNonNull(ds.child("msg").getValue()).toString());
                    model.setTimestamp(Objects.requireNonNull(ds.child("date").getValue()).toString());
                    model.setStatus(Objects.requireNonNull(ds.child("status").getValue()).toString());
                    model.setPicture(Objects.requireNonNull(ds.child("pic").getValue()).toString());
                    model.setTokendriver(Objects.requireNonNull(ds.child("tokendriver").getValue()).toString());
                    model.setTokenuser(Objects.requireNonNull(ds.child("tokenuser").getValue()).toString());
                    inboxArraylist.add(model);
                }
                Collections.reverse(inboxArraylist);
                inboxItem.notifyDataSetChanged();

                if(inboxArraylist.isEmpty()){
                    getView.findViewById(R.id.rlnodata).setVisibility(View.VISIBLE);
                }else {
                    getView.findViewById(R.id.rlnodata).setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        };



        inboxQuery.addValueEventListener(valueEventListener);


    }



    @Override
    public void onStop() {
        super.onStop();
        if(inboxQuery !=null)
            inboxQuery.removeEventListener(valueEventListener);
    }

    private boolean checkReadStoragepermission(){
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()).getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        else {
            try {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        Constant.permission_Read_data );
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
        return false;
    }







}