package kr.clude.hfcodesender.utils;

import android.view.View;
import android.widget.TextView;

import kr.clude.hfcodesender.R;

/**
 * Created by yearnning on 16. 1. 20..
 */
public class MessageViewHolder {

    public TextView mNameTv;

    /**
     * @param convertView
     */
    public MessageViewHolder(View convertView) {
        mNameTv = (TextView) convertView.findViewById(R.id.name_tv);
    }

}
