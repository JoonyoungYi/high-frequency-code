package kr.clude.hfcodesender.utils;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import kr.clude.hfcodesender.R;
import kr.clude.hfcodesender.models.MessageModel;

/**
 * Created by yearnning on 15. 11. 12..
 */
public class MessageViewHolderManager {

    private static final String TAG = "MessageViewHolderManager";

    private Activity activity = null;
    private View.OnClickListener onClickListener;
    private View.OnLongClickListener onLongClickListener;

    public MessageViewHolderManager(Activity activity, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener) {
        this.activity = activity;
        this.onClickListener = onClickListener;
        this.onLongClickListener = onLongClickListener;
    }

    public MessageViewHolder onCreateViewHolder(View convertView) {

        MessageViewHolder viewHolder = new MessageViewHolder(convertView);

        viewHolder.mNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClick(v);
            }
        });

        viewHolder.mNameTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                onLongClickListener.onLongClick(view);
                return false;
            }
        });

        return viewHolder;
    }


    public void onBindViewHolder(MessageViewHolder viewHolder,
                                 MessageModel messageModel,
                                 int position) {

        viewHolder.mNameTv.setText(messageModel.value);

        viewHolder.mNameTv.setTag(R.string.tag_message, messageModel);
        viewHolder.mNameTv.setTag(R.string.tag_position, position);
    }
}
