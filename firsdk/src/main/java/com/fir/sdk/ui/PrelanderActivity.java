package com.fir.sdk.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fir.sdk.R;
import com.fir.sdk.utils.Constants;
import com.fir.sdk.utils.FirebaseConfig;
import com.fir.sdk.utils.Utils;

public class PrelanderActivity extends BaseActivity implements PListAdapter.ItemClickListener {

    FirebaseConfig fc;
    ActivityResultLauncher<Intent> mStartForResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);

        ImageView close = findViewById(R.id.close);
        close.setOnClickListener(view -> {

            Utils.logEvent(getBaseContext(), Constants.pr_pa_cl, "");
            finish();
        });

        fc = FirebaseConfig.getInstance();

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(fc.sub_close_size, fc.sub_close_size);

        layoutParams.topToTop = ConstraintSet.PARENT_ID;
        layoutParams.endToEnd = ConstraintSet.PARENT_ID;
        layoutParams.topMargin = 12;
        layoutParams.rightMargin = 12;
        close.setLayoutParams(layoutParams);

        setLayoutValues();

        mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                if(data != null) {
                    boolean dataIsNotNull = data.hasExtra("status");

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        String server = fc.checkout_portal_endpoint + "?firebase_instance_id=" + fc.webParams.getFirebaseInstanceId() + "&phone_number=" + fc.webParams.getPhoneNumber();

                       /* Intent intent = new Intent(PrelanderActivity.this, WebActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        intent.putExtra(Constants.checkout_portal_endpoint, server);
                        startActivity(intent);*/
                        finish();
                        //todo open external browser
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(server));
                        startActivity(browserIntent);

                    } else if (dataIsNotNull && result.getResultCode() == Activity.RESULT_FIRST_USER) {
                        String msg = data.getStringExtra("status");
                        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getBaseContext(), "An error has occurred!", Toast.LENGTH_LONG).show();
                    }
                }
            });

        Utils.logEvent(getBaseContext(), Constants.pr_pa_op, "");
    }

    @SuppressLint({"SetTextI18n", "ResourceType"})
    private void setLayoutValues() {

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        PListAdapter adapter = new PListAdapter(fc.pay_options);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.addItemClickListener(this);

        TextView headerInfo = findViewById(R.id.headerInfo);
        headerInfo.setText(fc.sub_p_header);

        TextView headerDesc = findViewById(R.id.headerDesc);
        headerDesc.setText(fc.sub_p_desc);

        TextView choose_pay = findViewById(R.id.choose_pay);
        choose_pay.setText(fc.sub_p_title);

    }

    @Override
    public void onItemClick(int position) {
        switch (position) {

            case 1000:

                Utils.logEvent(getBaseContext(), Constants.we_pa_cl, "");

                if (fc.show_customt){
                    String ur = Constants.getMainU(PrelanderActivity.this,fc.sub_endu, fc.webParams);
                    new CustomTabsIntent.Builder().build().launchUrl(this, Uri.parse(ur));
                }else{
                    showLoader();
                    Intent intent = new Intent(PrelanderActivity.this, LoadActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Constants.sub_endu, fc.sub_endu);
                    startActivity(intent);
                    hideLoader();
                }

                break;
            case 1001:

                Intent intent = new Intent(this, SdkPForm.class);
                mStartForResult.launch(intent);

                break;
            case 1002:

                Utils.logEvent(getBaseContext(), Constants.inA_p_cl, "");
                break;
            default:
                break;

        }
    }

    @Override
    public void onBackPressed() {

    }
}
