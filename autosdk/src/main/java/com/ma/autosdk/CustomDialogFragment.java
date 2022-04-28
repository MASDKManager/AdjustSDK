package com.ma.autosdk;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.sma.ssdkm.R;

public class CustomDialogFragment extends Dialog {

    private String bodyText;
    private String titleText;

    public CustomDialogFragment(@NonNull Context context, String titleText, String bodyText) {
        super(context);
        this.bodyText = bodyText;
        this.titleText = titleText;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_pp);
        TextView body = findViewById(R.id.body);
        TextView title = findViewById(R.id.title);
        ImageView close = findViewById(R.id.close);
        body.setText(Html.fromHtml(bodyText));
        title.setText(titleText);
        close.setOnClickListener(view -> dismiss());
    }

    @Override
    protected void onStart() {
        super.onStart();
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().setLayout(width, height);
    }
}
