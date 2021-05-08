package com.bekurir.biz.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bekurir.biz.R;
import com.bekurir.biz.constants.BaseApp;
import com.bekurir.biz.constants.Constant;
import com.bekurir.biz.item.BankItem;
import com.bekurir.biz.json.BankResponseJson;
import com.bekurir.biz.json.ResponseJson;
import com.bekurir.biz.json.WithdrawRequestJson;
import com.bekurir.biz.json.fcm.FCMMessage;
import com.bekurir.biz.models.Notif;
import com.bekurir.biz.models.User;
import com.bekurir.biz.utils.SettingPreference;
import com.bekurir.biz.utils.Utility;
import com.bekurir.biz.utils.api.FCMHelper;
import com.bekurir.biz.utils.api.ServiceGenerator;
import com.bekurir.biz.utils.api.service.MerchantService;

import java.io.IOException;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WithdrawActivity extends AppCompatActivity {

    EditText amount, bank, accnumber, nama;
    Button submit;
    TextView notif;
    ImageView backbtn, images;
    RelativeLayout rlnotif, rlprogress;
    String disableback, type, nominal;
    SettingPreference sp;
    RecyclerView petunjuk;
    LinearLayout llpentunjuk;
    BankItem bankItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        disableback = "false";
        amount = findViewById(R.id.amount);
        bank = findViewById(R.id.bank);
        accnumber = findViewById(R.id.accnumber);
        backbtn = findViewById(R.id.back_btn);
        submit = findViewById(R.id.submit);
        rlnotif = findViewById(R.id.rlnotif);
        notif = findViewById(R.id.textnotif);
        rlprogress = findViewById(R.id.rlprogress);
        nama = findViewById(R.id.namanumber);
        images = findViewById(R.id.imagebackground);
        sp = new SettingPreference(this);
        llpentunjuk = findViewById(R.id.llpentunjuk);
        petunjuk = findViewById(R.id.petunjuk);
        sp = new SettingPreference(this);

        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        llpentunjuk.setVisibility(View.GONE);

        if (Objects.equals(type, "topup")) {
            images.setImageResource(R.drawable.atm);
            images.setScaleType(ImageView.ScaleType.FIT_XY);
            nominal = intent.getStringExtra("nominal");

            Utility.currencyTXT(amount, Objects.requireNonNull(nominal), WithdrawActivity.this);
            petunjuk.setHasFixedSize(true);
            petunjuk.setNestedScrollingEnabled(false);
            petunjuk.setLayoutManager(new GridLayoutManager(this, 1));
            getpetunjuk();
        }

        amount.addTextChangedListener(Utility.currencyTW(amount, this));

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User userLogin = BaseApp.getInstance(WithdrawActivity.this).getLoginUser();
                if (type.equals("withdraw")) {
                    if (amount.getText().toString().isEmpty()) {
                        notif("amount cant be empty!");
                    } else if (Long.parseLong(amount.getText()
                            .toString()
                            .replace(".", "")
                            .replace(",", "")
                            .replace(sp.getSetting()[0], "")) > userLogin.getWalletSaldo()) {
                        notif("your balance is no enought!");
                    } else if (bank.getText().toString().isEmpty()) {
                        notif("bank cant be empty!");
                    } else if (accnumber.getText().toString().isEmpty()) {
                        notif("account number cant be empty!");
                    } else {
                        submit();
                    }
                } else {
                    if (amount.getText().toString().isEmpty()) {
                        notif("amount cant be empty!");
                    } else if (bank.getText().toString().isEmpty()) {
                        notif("bank cant be empty!");
                    } else if (accnumber.getText().toString().isEmpty()) {
                        notif("account number cant be empty!");
                    } else {
                        submit();
                    }
                }
            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void submit() {
        progressshow();
        final User user = BaseApp.getInstance(this).getLoginUser();
        WithdrawRequestJson request = new WithdrawRequestJson();
        request.setId(user.getId());
        request.setBank(bank.getText().toString());
        request.setName(nama.getText().toString());
        request.setAmount(amount.getText().toString().replace(".", "").replace(sp.getSetting()[0], ""));
        request.setCard(accnumber.getText().toString());
        request.setNotelepon(user.getNoTelepon());
        request.setEmail(user.getEmail());
        request.setType(type);

        MerchantService service = ServiceGenerator.createService(MerchantService.class, user.getNoTelepon(), user.getPassword());
        service.withdraw(request).enqueue(new Callback<ResponseJson>() {
            @Override
            public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                progresshide();
                if (response.isSuccessful()) {
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("success")) {
                        Intent intent = new Intent(WithdrawActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();

                        Notif notif = new Notif();
                        if (type.equals("withdraw")) {
                            notif.title = "Tarik Saldo";
                            notif.message = "Permintaan penarikan telah berhasil, kami akan mengirimkan pemberitahuan setelah kami mengirimkan dana ke akun Anda";
                        } else {
                            notif.title = "Topup";
                            notif.message = "Permintaan topup berhasil, kami akan mengirimkan notifikasi setelah kami konfirmasi";
                        }
                        sendNotif(user.getToken_merchant(), notif);

                    } else {
                        notif("error, silakan periksa data akun Anda!");
                    }
                } else {
                    notif("error!");
                }
            }

            @Override
            public void onFailure(Call<ResponseJson> call, Throwable t) {
                progresshide();
                t.printStackTrace();
                notif("Kesalahan server");
            }
        });
    }

    public void onBackPressed() {
        if (!disableback.equals("true")) {
            finish();
        }
    }

    public void notif(String text) {
        rlnotif.setVisibility(View.VISIBLE);
        notif.setText(text);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                rlnotif.setVisibility(View.GONE);
            }
        }, 3000);
    }

    public void progressshow() {
        rlprogress.setVisibility(View.VISIBLE);
        disableback = "true";
    }

    public void progresshide() {
        rlprogress.setVisibility(View.GONE);
        disableback = "false";
    }

    private void sendNotif(final String regIDTujuan, final Notif notif) {

        final FCMMessage message = new FCMMessage();
        message.setTo(regIDTujuan);
        message.setData(notif);

        FCMHelper.sendMessage(Constant.FCM_KEY, message).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) {
            }

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void getpetunjuk() {
        User user = BaseApp.getInstance(this).getLoginUser();
        WithdrawRequestJson request = new WithdrawRequestJson();

        MerchantService service = ServiceGenerator.createService(MerchantService.class, user.getNoTelepon(), user.getPassword());
        service.listbank(request).enqueue(new Callback<BankResponseJson>() {
            @Override
            public void onResponse(Call<BankResponseJson> call, Response<BankResponseJson> response) {
                if (response.isSuccessful()) {
                    llpentunjuk.setVisibility(View.VISIBLE);
                    bankItem = new BankItem(WithdrawActivity.this, Objects.requireNonNull(response.body()).getData(), R.layout.item_bank);
                    petunjuk.setAdapter(bankItem);
                }
            }

            @Override
            public void onFailure(Call<BankResponseJson> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


}
