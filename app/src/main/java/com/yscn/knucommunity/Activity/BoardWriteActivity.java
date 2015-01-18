package com.yscn.knucommunity.Activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yscn.knucommunity.CustomView.ClearProgressDialog;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.NetworkUtil;

import org.json.simple.parser.ParseException;

import java.io.IOException;

/**
 * Created by GwonHyeok on 15. 1. 19..
 */
public class BoardWriteActivity extends ActionBarActivity {
    private EditText titleView, contentView;
    private TextView boardTypeView;
    private int boardType;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_board_write);
        actionBarInit();
        viewInit();
    }

    private void viewInit() {
        String boardTypeMessage;
        this.titleView = (EditText) findViewById(R.id.board_write_title);
        this.contentView = (EditText) findViewById(R.id.board_write_content);
        this.boardTypeView = (TextView) findViewById(R.id.board_write_boardtype);
        this.boardType = getIntent().getIntExtra("boardType", -1);

        switch (boardType) {
            case 1:
                boardTypeMessage = getString(R.string.community_freeboard_title);
                break;
            case 2:
                boardTypeMessage = getString(R.string.community_faq_title);
                break;
            case 3:
                boardTypeMessage = getString(R.string.community_greenlight_title);
                break;
            default:
                boardTypeMessage = "null";
        }
        this.boardTypeView.setText(boardTypeMessage);

    }

    private void actionBarInit() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationIcon(R.drawable.ic_cancel);

        findViewById(R.id.board_write_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeBoard();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private Context getContext() {
        return this;
    }

    private void writeBoard() {
        new AsyncTask<Void, Void, Boolean>() {
            private ClearProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                dialog = new ClearProgressDialog(getContext());
                dialog.show();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                boolean result = false;
                try {
                    String title = titleView.getText().toString();
                    String content = contentView.getText().toString();
                    result = NetworkUtil.getInstance().writeBoardContent(boardType, title, content);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(Boolean bool) {
                if (bool) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    // 토스트 말고 다른 Widget 사용
                    // 변경 요망
                    Toast.makeText(getContext(), "글쓰기 실패", Toast.LENGTH_SHORT).show();
                }
                dialog.cancel();
            }
        }.execute();
    }

}
