package com.ma.fbsdk.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ma.fbsdk.R;
import com.ma.fbsdk.models.Params;
import com.ma.fbsdk.models.checkout.CheckoutResponse;
import com.ma.fbsdk.utils.Constants;
import com.ma.fbsdk.utils.FirebaseConfig;
import com.ma.fbsdk.utils.Utils;

public class PrelanderActivity extends BaseActivity   implements PaymentListAdapter.ItemClickListener {

    FirebaseConfig fc;
    ActivityResultLauncher<Intent> mStartForResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_1);

        ImageView close = findViewById(R.id.close);
        close.setOnClickListener(view -> {

            Utils.logEvent(getBaseContext(), Constants.prelandar_page_closed, "");
            finish();
        });

        fc = FirebaseConfig.getInstance();

        close.setVisibility(fc.show_prelander_close ? View.VISIBLE : View.GONE);

        setLayoutValues();

        mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                if(data != null) {
                    boolean dataIsNotNull = data.hasExtra("status");

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Toast.makeText(getBaseContext(), "Payment has been made successfully!", Toast.LENGTH_LONG).show();
                        finish();

                    } else if (dataIsNotNull && result.getResultCode() == Activity.RESULT_FIRST_USER) {
                        String msg = data.getStringExtra("status");
                        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getBaseContext(), "An error has occurred!", Toast.LENGTH_LONG).show();
                    }
                }
            });

        Utils.logEvent(getBaseContext(), Constants.prelandar_page_opened, "");
    }

    @SuppressLint({"SetTextI18n", "ResourceType"})
    private void setLayoutValues() {

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

                mStartForResult.launch(new Intent(this, SdkPaymentForm.class));

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
