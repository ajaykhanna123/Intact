package com.example.ajaykhanna.intact;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NoticeFragment extends Fragment {
    private FloatingActionButton fabAddNotice;
    private RecyclerView noticePostListView;
    private List<NoticePost> noticeList;
    private FirebaseFirestore firebaseFirestore;
    private NoticeRecyclerAdapter noticeRecyclerAdapter;



    public NoticeFragment() {

        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_notice,
                container, false);

        noticeList=new ArrayList<>();
        // Inflate the layout for this fragment
        noticePostListView=view.findViewById(R.id.noticePostListView);

        noticeRecyclerAdapter=new NoticeRecyclerAdapter(noticeList);


        noticePostListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        noticePostListView.setAdapter(noticeRecyclerAdapter);



        fabAddNotice=(FloatingActionButton)view.findViewById(R.id.fabAddNotice);

        if (fabAddNotice != null) {
            fabAddNotice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent addNoticeIntent=new Intent(getActivity(),AddNoticeActivity.class);
                    startActivity(addNoticeIntent);
                    ((Activity) getActivity()).overridePendingTransition(0,0);//means no Animation in transition.

                }
            });
        }

        firebaseFirestore=FirebaseFirestore.getInstance();

        Query firstQuery=firebaseFirestore.collection("Posts").orderBy("timeStamp"
                ,Query.Direction.DESCENDING);//post wich is posted after is added on front of fragment

        firstQuery.addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges())
                {
                    if(doc.getType()==DocumentChange.Type.ADDED)
                    {
                        NoticePost blogPost=doc.getDocument().toObject(NoticePost.class);
                        noticeList.add(blogPost);

                        noticeRecyclerAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        return view;
    }

}
