package kr.clude.hfcodesender;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import kr.clude.hfcodesender.libs.HighFrequencyCodeManager;
import kr.clude.hfcodesender.models.MessageModel;
import kr.clude.hfcodesender.utils.MessageListViewManager;

import static kr.clude.hfcodesender.libs.Config.MAX_MESSAGE_INDEX;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    private HighFrequencyCodeManager highFrequencyCodeManager;
    private MessageListViewManager messageListViewManager = null;
    private TextView mCurrentTv;

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        ListView lv = (ListView) findViewById(R.id.lv);
        mCurrentTv = (TextView) findViewById(R.id.current_tv);
        View progressBar = findViewById(R.id.progress_bar);
        highFrequencyCodeManager = new HighFrequencyCodeManager(this, findViewById(R.id.sync_progress_bar));

        messageListViewManager = new MessageListViewManager(this,
                new ArrayList<MessageModel>(),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Object messageObj = view.getTag(R.string.tag_message);
                        if (messageObj == null) {
                            return;
                        }

                        MessageModel messageModel = (MessageModel) messageObj;
                        if (mCurrentTv.getText().toString().equals(messageModel.value)) {
                            mCurrentTv.setText("(현재 전송 중인 메세지가 없습니다)");
                            highFrequencyCodeManager.setData(-1);
                        } else {
                            highFrequencyCodeManager.setData(messageModel.index);
                            mCurrentTv.setText(messageModel.value);
                        }
                    }
                },
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        Object messageObj = view.getTag(R.string.tag_message);
                        if (messageObj == null) {
                            return false;
                        }

                        MessageModel messageModel = (MessageModel) messageObj;
                        showDeleteDialog(messageModel.key);
                        return false;
                    }
                },
                progressBar);
        lv.setAdapter(messageListViewManager);

        DatabaseReference myRef = getReference();
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                highFrequencyCodeManager.onPause();
                ArrayList<MessageModel> messageModelArrayList = new ArrayList<>();
                int index = 0;
                for (DataSnapshot messageDataSnapShot : dataSnapshot.getChildren()) {
                    MessageModel messageModel = new MessageModel();
                    messageModel.key = messageDataSnapShot.getKey();
                    messageModel.value = (String) messageDataSnapShot.getValue();
                    messageModel.index = index++;
                    messageModelArrayList.add(messageModel);
                }
                highFrequencyCodeManager.start();
                highFrequencyCodeManager.onResume();

                messageListViewManager.setMessages(messageModelArrayList);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        findViewById(R.id.add_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDialog();
            }
        });
    }

    private void showDeleteDialog(final String key) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
        builder.title("문구 제거")
                .content("정말 이 문구를 제거하시겠어요?")
                .positiveText("확인")
                .negativeText("취소")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        messageListViewManager.showProgressBar();
                        getReference().child(key).removeValue();
                    }
                })
                .show();
    }

    private void showAddDialog() {

        if (messageListViewManager.getCount() >= MAX_MESSAGE_INDEX) {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
            builder.title("문구 추가 불가")
                    .content("더 이상 문구를 추가할 수 없습니다. 기존 문구를 삭제하셔야 새로 문구를 추가하실 수 있습니다.")
                    .show();
        } else {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
            builder.title("문구 추가")
                    .input("추가할 문구를 입력해주세요", "", false, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                            messageListViewManager.showProgressBar();
                            addMessage(input.toString());
                        }
                    })
                    .show();
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        highFrequencyCodeManager.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        highFrequencyCodeManager.onPause();
    }

    private DatabaseReference getReference() {
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users/" + getUid()).child("messages");
        return myRef;
    }

    private void addMessage(String msg) {
        DatabaseReference myRef = getReference().push();
        myRef.setValue(msg);
    }

    /**
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        if (id == R.id.action_sign_out) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            SplashActivity.startActivity(MainActivity.this);
                            finish();
                        }
                    });
            return true;
        } else if (id == R.id.action_api_key) {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
            builder.title("API_KEY 확인")
                    .content(getUid())
                    .positiveText("복사하기")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            // Gets a handle to the clipboard service.
                            ClipboardManager clipboard = (ClipboardManager)
                                    getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("simple text", getUid());
                            clipboard.setPrimaryClip(clip);

                            Toast.makeText(MainActivity.this, "클립보드에 복사했습니다", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .neutralText("닫기")
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }

    private String getUid() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        return uid;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        highFrequencyCodeManager.onDestroy();
    }
}
