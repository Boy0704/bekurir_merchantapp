package com.bekurir.biz.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bekurir.biz.R;
import com.bekurir.biz.constants.BaseApp;
import com.bekurir.biz.constants.Constant;
import com.bekurir.biz.json.AddEditItemRequestJson;
import com.bekurir.biz.json.ResponseJson;
import com.bekurir.biz.models.User;
import com.bekurir.biz.utils.SettingPreference;
import com.bekurir.biz.utils.Utility;
import com.bekurir.biz.utils.api.ServiceGenerator;
import com.bekurir.biz.utils.api.service.MerchantService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class AddmenuActivity extends AppCompatActivity {

    ImageView backbtn, menuimage;
    EditText namamenu, descmenu, hargamenu, hargapromo;
    SwitchCompat activepromo;
    Button submit, addimage;
    String active,idkategori;
    byte[] imageByteArray;
    Bitmap decoded;
    private Uri selectedImage;
    SettingPreference sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addmenu);

        backbtn = findViewById(R.id.back_btn);
        menuimage = findViewById(R.id.menuimage);
        namamenu = findViewById(R.id.namamenu);
        descmenu = findViewById(R.id.descmenu);
        hargamenu = findViewById(R.id.hargamenu);
        hargapromo = findViewById(R.id.hargapromo);
        activepromo = findViewById(R.id.activepromo);
        submit = findViewById(R.id.buttonsavemenu);
        addimage = findViewById(R.id.addimage);

        sp = new SettingPreference(this);

        Intent intent = getIntent();
        idkategori = intent.getStringExtra("idkategori");

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        hargamenu.addTextChangedListener(Utility.currencyTW(hargamenu,this));

        activepromo.setChecked(false);
        active = "0";
        hargapromo.setEnabled(false);
        activepromo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    hargapromo.setEnabled(true);
                    hargapromo.addTextChangedListener(Utility.currencyTW(hargapromo,AddmenuActivity.this));
                    active = "1";
                } else {
                    active = "0";
                    hargapromo.setText("");
                    hargapromo.setEnabled(false);
                }
            }
        });

        addimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (namamenu.getText().toString().isEmpty()){
                    Toast.makeText(AddmenuActivity.this, "Menu name cant be empty!", Toast.LENGTH_SHORT).show();
                } else if (descmenu.getText().toString().isEmpty()) {
                    Toast.makeText(AddmenuActivity.this, "Description cant be empty", Toast.LENGTH_SHORT).show();
                } else if (hargamenu.getText().toString().isEmpty()) {
                    Toast.makeText(AddmenuActivity.this, "Price cant be empty!", Toast.LENGTH_SHORT).show();
                } else if (activepromo.isChecked() && hargapromo.getText().toString().isEmpty()) {
                    Toast.makeText(AddmenuActivity.this, "Promo price cant be empty!", Toast.LENGTH_SHORT).show();
                } else if (activepromo.isChecked() && Long.parseLong(hargapromo.getText().toString().replace(sp.getSetting()[0],"").replace(".","")) > Long.parseLong(hargamenu.getText().toString().replace(sp.getSetting()[0],"").replace(".",""))) {
                    Toast.makeText(AddmenuActivity.this, "promo price cannot be higher than the base price!", Toast.LENGTH_SHORT).show();
                } else if (selectedImage ==null) {
                    Toast.makeText(AddmenuActivity.this, "Image cant be empty!", Toast.LENGTH_SHORT).show();
                } else {
                    addmenu();
                }
            }
        });

    }

    private void selectImage() {
        if (check_ReadStoragepermission()) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 2);
        }
    }

    private boolean check_ReadStoragepermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            Constant.permission_Read_data);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
        return false;
    }

    public String getPath(Uri uri) {
        String result = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = this.getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        if (result == null) {
            result = "Not found";
        }
        return result;
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        imageByteArray = baos.toByteArray();
        return Base64.encodeToString(imageByteArray, Base64.DEFAULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
             if (requestCode == 2) {
                selectedImage = data.getData();
                menuimage.setImageURI(selectedImage);
//                InputStream imageStream = null;
//                try {
//                    imageStream = this.getContentResolver().openInputStream(Objects.requireNonNull(selectedImage));
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//                final Bitmap imagebitmap = BitmapFactory.decodeStream(imageStream);
//                 Bitmap scaleBitmap = Bitmap.createScaledBitmap(imagebitmap, (int) (imagebitmap.getWidth() * 0.1), (int) (imagebitmap.getHeight()*0.1) ,  true);
//
//                String path = getPath(selectedImage);
//                Matrix matrix = new Matrix();
//                ExifInterface exif;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    try {
//                        exif = new ExifInterface(path);
//                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
//                        switch (orientation) {
//                            case ExifInterface.ORIENTATION_ROTATE_90:
//                                matrix.postRotate(90);
//                                break;
//                            case ExifInterface.ORIENTATION_ROTATE_180:
//                                matrix.postRotate(180);
//                                break;
//                            case ExifInterface.ORIENTATION_ROTATE_270:
//                                matrix.postRotate(270);
//                                break;
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                Bitmap rotatedBitmap = Bitmap.createBitmap(scaleBitmap, 0, 0, scaleBitmap.getWidth(), scaleBitmap.getHeight(), matrix, true);
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
//                menuimage.setImageBitmap(rotatedBitmap);
//                imageByteArray = baos.toByteArray();
//                decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(baos.toByteArray()));

            }
        }

    }

    @SuppressLint("SetTextI18n")
    private void addmenu() {
        submit.setEnabled(false);
        submit.setText("Please Wait");
        submit.setBackground(getResources().getDrawable(R.drawable.button_round_3));

        try {
            decoded = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        User loginUser = BaseApp.getInstance(this).getLoginUser();
        AddEditItemRequestJson request = new AddEditItemRequestJson();
        request.setNotelepon(loginUser.getNoTelepon());
        request.setIdmerchant(loginUser.getId_merchant());
        request.setNama(namamenu.getText().toString());
        request.setHarga(hargamenu.getText().toString().replace(".","").replace(sp.getSetting()[0],""));
        request.setHargapromo(hargapromo.getText().toString().replace(".","").replace(sp.getSetting()[0],""));
        request.setKategori(idkategori);
        request.setDeskripsi(descmenu.getText().toString());
        request.setStatus(active);
        request.setFoto(getStringImage(decoded));


        MerchantService service = ServiceGenerator.createService(MerchantService.class, loginUser.getEmail(), loginUser.getPassword());
        service.additem(request).enqueue(new Callback<ResponseJson>() {
            @Override
            public void onResponse(@NonNull Call<ResponseJson> call,@NonNull Response<ResponseJson> response) {
                if (response.isSuccessful()) {
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("success")) {
                        finish();

                    } else {
                        submit.setEnabled(true);
                        submit.setText("Save");
                        submit.setBackground(getResources().getDrawable(R.drawable.button_round_1));
                        Toast.makeText(AddmenuActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    submit.setEnabled(true);
                    submit.setText("Save");
                    submit.setBackground(getResources().getDrawable(R.drawable.button_round_1));
                    Toast.makeText(AddmenuActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseJson> call,@NonNull Throwable t) {
                t.printStackTrace();
                submit.setEnabled(true);
                submit.setText("Save");
                submit.setBackground(getResources().getDrawable(R.drawable.button_round_1));
                Toast.makeText(AddmenuActivity.this, "Error Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
