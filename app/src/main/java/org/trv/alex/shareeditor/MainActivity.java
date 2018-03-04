package org.trv.alex.shareeditor;

import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ConfirmDialog.DialogAction {

    private static final String URI_LIST_KEY = "uriListKey";

    private EditText mEditSharedText;
    private ListView mListSharedFiles;
    private TextView mTextViewTitleFiles;
    private CheckBox mNotSendTextBox;
    private Button mShareForwardButton;

    private ArrayAdapter<Uri> mAdapter;
    private Intent mIntent;
    private List<Uri> mUriList;
    private String mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditSharedText = findViewById(R.id.shared_text);
        mListSharedFiles = findViewById(R.id.shared_files);
        mTextViewTitleFiles = findViewById(R.id.title_files);
        mNotSendTextBox = findViewById(R.id.not_send_text);
        mShareForwardButton = findViewById(R.id.share_forward);

        mIntent = getIntent();
        mType = mIntent.getType();

        if (Intent.ACTION_SEND.equals(mIntent.getAction()) && mIntent.getType() != null) {
            String sharedText = mIntent.getStringExtra(Intent.EXTRA_TEXT);
            Uri fileUri = mIntent.getParcelableExtra(Intent.EXTRA_STREAM);
            if (sharedText != null) {
                mEditSharedText.setText(sharedText);
            }
            if (fileUri != null) {
                if (savedInstanceState != null) {
                    mUriList = savedInstanceState.getParcelableArrayList(URI_LIST_KEY);
                }
                if (mUriList == null) {
                    mUriList = new ArrayList<>(1);
                    mUriList.add(fileUri);
                }
                mAdapter = new ArrayAdapter<Uri>(this, android.R.layout.simple_list_item_1, mUriList);
                mListSharedFiles.setAdapter(mAdapter);
                boolean isEmpty = mUriList.isEmpty();
                mListSharedFiles.setVisibility(isEmpty ? View.INVISIBLE : View.VISIBLE);
                mTextViewTitleFiles.setVisibility(isEmpty ? View.INVISIBLE : View.VISIBLE);
            }
        }

        mNotSendTextBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mEditSharedText.setEnabled(!isChecked);
            }
        });

        // Deletes item on click
        mListSharedFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DialogFragment dialog = ConfirmDialog.newInstance(R.string.remove_file, R.string.remove_file_msg, position);
                dialog.show(getFragmentManager(), "confirmDialog");
            }
        });

        mShareForwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                String sharedText = mNotSendTextBox.isChecked() ? null : mEditSharedText.getText().toString();
                intent.putExtra(Intent.EXTRA_TEXT, sharedText);
                if (mUriList != null && !mUriList.isEmpty()) {
                    intent.putExtra(Intent.EXTRA_STREAM, mUriList.get(0));
                    intent.setType(mType);
                } else {
                    intent.setType("text/plain");
                }
                startActivity(Intent.createChooser(intent, getString(R.string.share_forward_string)));
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mUriList != null) {
            outState.putParcelableArrayList(URI_LIST_KEY, new ArrayList<>(mUriList));
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPositiveAction(int index) {
        mUriList.remove(index);
        mAdapter.notifyDataSetChanged();
        if (mUriList.isEmpty()) {
            mTextViewTitleFiles.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onNegativeAction() {
    }
}
