package com.example.ajaykhanna.intact;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class NoticeFragment extends Fragment {
    private FloatingActionButton fabAddNotice;


    public NoticeFragment() {

        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_notice,
                container, false);


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

        return view;
    }

}
