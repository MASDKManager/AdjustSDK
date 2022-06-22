package com.ma.fbsdk.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ma.fbsdk.Bandora;
import com.ma.fbsdk.R;
import com.ma.fbsdk.models.Params;
import com.ma.fbsdk.utils.Constants;
import com.ma.fbsdk.utils.FirebaseConfig;
import com.ma.fbsdk.utils.Utils;

public class PrelanderActivity extends BaseActivity   implements PaymentListAdapter.ItemClickListener {

    FirebaseConfig fc;
    private int RB1_ID = 1000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_1);

        ImageView close = findViewById(R.id.close);
        close.setOnClickListener(view -> {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

            Utils.logEvent(getBaseContext(), Constants.prelandar_page_closed, "");
            finish();
        });

        fc = FirebaseConfig.getInstance();
        setLayoutValues();

        Utils.logEvent(getBaseContext(), Constants.prelandar_page_opened, "");
    }

    @SuppressLint({"SetTextI18n", "ResourceType"})
    private void setLayoutValues(){

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        PaymentListAdapter adapter = new PaymentListAdapter(fc.payments);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.addItemClickListener(this);

        int pay_card_text_color = getResources().getColor(R.color.pay_card_text_color);
        int pay_card_selected_text_color = getResources().getColor(R.color.pay_card_selected_text_color);
        int pay_card_btn_color = getResources().getColor(R.color.pay_card_btn_color);

        TextView headerInfo = findViewById(R.id.headerInfo);
        headerInfo.setText(fc.prelander_title);

        TextView choose_pay = findViewById(R.id.choose_pay);
        choose_pay.setText(fc.prelander_payments_title);

        TextView headerDesc = findViewById(R.id.headerDesc);
        headerDesc.setText(fc.prelander_description);

        Button button = findViewById(R.id.button);
        button.setText(fc.prelander_submit);
        button.setBackgroundColor(pay_card_btn_color);

    }

    @Override
    public void onItemClick(int position) {
        switch (position) {

            case 1000:

                Utils.logEvent(getBaseContext(), Constants.web_payment_clicked, "");

                Intent intent = new Intent(PrelanderActivity.this, AppFileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Params params = (Params) getIntent().getSerializableExtra("webParams");
                intent.putExtra("webParams", params);
                startActivity(intent);
                finish();
                break;
            case 1001:

                Utils.logEvent(getBaseContext(), Constants.checkout_payment_clicked, "");
                Intent intent1 = new Intent(PrelanderActivity.this, SdkPaymentForm.class);
                //   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);

                break;
            case 1002:

                Utils.logEvent(getBaseContext(), Constants.inApp_payment_clicked, "");
                Toast.makeText(PrelanderActivity.this, "Coming soon", Toast.LENGTH_LONG).show();
                break;
            default:
                break;

        }
    }

    @Override
    public void onBackPressed() {

    }
}
