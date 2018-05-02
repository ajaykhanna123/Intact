package com.example.ajaykhanna.intact;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.Date;
import java.util.List;

public class NoticeRecyclerAdapter  extends RecyclerView.Adapter<NoticeRecyclerAdapter.ViewHolder> {

    public List<NoticePost> noticePost;
    public Context context;
    private FirebaseFirestore firebaseFirestore;

    public NoticeRecyclerAdapter(List<NoticePost> noticePost) {
        this.noticePost = noticePost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.notice_list_view, parent, false);
        context = parent.getContext();
        firebaseFirestore=FirebaseFirestore.getInstance();

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        String descData = noticePost.get(position).getDesc();
        holder.setDescText(descData);

        String image_url=noticePost.get(position).getImage_url();
        holder.setBlogImage(image_url);

        String user_id=noticePost.get(position).getUser_id();
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    String userName=task.getResult().getString("name");
                    String userImage=task.getResult().getString("image");

                    holder.setUserData(userName,userImage);

                }else
                {
                    //firebase exception
                }
            }
        });

        long milliseconds=noticePost.get(position).getTimeStamp().getTime();
        String dateString = DateFormat.format("MM/dd/yyyy", new Date(milliseconds)).toString();
        holder.setTime(dateString);



    }

    @Override
    public int getItemCount() {
        return noticePost.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView descView;
        private ImageView noticeImgView;
        private TextView noticeDate;
        private CircleImageView imgUser;
        private TextView noticeUserName;



        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void setDescText(String descText) {
            descView = mView.findViewById(R.id.noticePostDecription);
            descView.setText(descText);
        }

        public void setBlogImage(String downloadUri) {
            noticeImgView = mView.findViewById(R.id.noticePostImage);
            Glide.with(context).load(downloadUri).into(noticeImgView);
        }

        public void setTime(String date) {
            noticeDate = mView.findViewById(R.id.noticePostDate);
            noticeDate.setText(date);
        }

        public void setUserData(String name, String image) {
            imgUser = mView.findViewById(R.id.noticeUserImage);
            noticeUserName = mView.findViewById(R.id.noticeUserName);

            noticeUserName.setText(name);

            RequestOptions placeHolderOptions = new RequestOptions();
            placeHolderOptions.placeholder(R.drawable.default_image);
            Glide.with(context).applyDefaultRequestOptions(placeHolderOptions).load(image).into(imgUser);
        }
    }
}
