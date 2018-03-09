package kr.clude.hfcodesender.utils;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import kr.clude.hfcodesender.R;
import kr.clude.hfcodesender.models.MessageModel;

/**
 * Created by yearnning on 15. 9. 18..
 */
public class MessageListViewManager extends ArrayAdapter<MessageModel> {

    /**
     *
     */
    private static final String TAG = "MessageListViewManager";

    /**
     *
     */
    private MessageViewHolderManager messageViewHolderManager = null;

    /**
     *
     */
    private ArrayList<MessageModel> messageModels = new ArrayList<>();

    /**
     *
     */
    private MessageViewHolder viewHolder = null;
    private Activity activity = null;
    private int textViewResourceId;
    private View progressBar;

    /**
     * @param activity
     * @param messageModels
     */
    public MessageListViewManager(final Activity activity,
                                  ArrayList<MessageModel> messageModels,
                                  View.OnClickListener onClickListener,
                                  View.OnLongClickListener onLongClickListener,
                                  View progressBar) {
        super(activity, R.layout.main_activity_lv, messageModels);

        /**
         *
         */
        this.activity = activity;
        this.textViewResourceId = R.layout.main_activity_lv;
        this.messageModels = messageModels;
        this.progressBar = progressBar;

        showProgressBar();

        /**
         *
         */
        messageViewHolderManager = new MessageViewHolderManager(activity, onClickListener, onLongClickListener);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public int getCount() {
        return messageModels.size();
    }

    @Override
    public MessageModel getItem(int position) {
        return messageModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        /*
         * UI Initiailizing : View Holder
         */
        if (convertView == null) {
            convertView = activity.getLayoutInflater()
                    .inflate(textViewResourceId, null);

            viewHolder = messageViewHolderManager.onCreateViewHolder(convertView);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (MessageViewHolder) convertView.getTag();
        }

        /**
         *
         */
        MessageModel messageModel = this.getItem(position);
        messageViewHolderManager.onBindViewHolder(viewHolder, messageModel, position);

        return convertView;
    }

    public void showProgressBar() {
        this.progressBar.setVisibility(View.VISIBLE);
    }

    public void setMessages(ArrayList<MessageModel> messageModels) {
        this.messageModels.clear();
        this.messageModels.addAll(messageModels);
        this.notifyDataSetChanged();
        this.progressBar.setVisibility(View.GONE);
    }

}


