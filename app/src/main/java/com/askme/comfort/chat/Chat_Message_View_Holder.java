package com.askme.comfort.chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.askme.comfort.CircleImageView;
import com.askme.comfort.R;

/**
 * Created by Amit on 10/28/2016.
 */

public class Chat_Message_View_Holder extends RecyclerView.ViewHolder {

    public TextView TextView_Message;
    public TextView TextView_Person_Name;
    public CircleImageView Image_View_Person;

    public Chat_Message_View_Holder(View item) {
        super(item);

        TextView_Message = (TextView)itemView.findViewById(R.id.TextView_Chat_Message);
        TextView_Person_Name = (TextView)itemView.findViewById(R.id.TextView_Chat_Person_Name);
        Image_View_Person = (CircleImageView)itemView.findViewById(R.id.Image_View_Person);
    }
}
