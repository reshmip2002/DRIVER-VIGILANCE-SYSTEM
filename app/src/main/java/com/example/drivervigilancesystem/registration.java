package com.example.drivervigilancesystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class registration extends AppCompatActivity {
    EditText e1,e2,e3,e4,e6,e7,e8,e9,e10,e11,e12,e13;
    ImageView i;
    Button b;
    Bitmap bitmap=null;
    ProgressDialog pd;
    String url,gender="Male";
    SharedPreferences sh;
    RadioButton r1,r2;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        sh= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sh.getString("ip","");
        sh.getString("url","");
        url=sh.getString("url","")+"and_registration";
        e1=findViewById(R.id.editTextTextPersonName5);
        e2=findViewById(R.id.editTextTextPersonName6);
        e3=findViewById(R.id.editTextTextPersonName7);
        e4=findViewById(R.id.editTextTextPersonName8);
        r1=findViewById(R.id.radioButton);
        r2=findViewById(R.id.radioButton2);
        e6=findViewById(R.id.editTextTextPersonName10);
        e7=findViewById(R.id.editTextTextPersonName11);
        e8=findViewById(R.id.editTextTextPersonName12);
        e9=findViewById(R.id.editTextTextPersonName13);
        e10=findViewById(R.id.editTextTextPersonName9);
        e11=findViewById(R.id.editTextTextPersonName14);
        e12=findViewById(R.id.editTextTextPersonName16);
        e13=findViewById(R.id.editTextTextPersonName18);
        i=findViewById(R.id.imageView2);
        b=findViewById(R.id.button8);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            finish();
            startActivity(intent);
            return;

        }
        i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 100);
            }
        });
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name =e1.getText().toString();
                String place =e2.getText().toString();
                String post =e3.getText().toString();
                String pin =e4.getText().toString();
               if(r2.isChecked()){
                   gender="Female";
               }

                String age=e6.getText().toString();
                String blood_group =e7.getText().toString();
                String license_number =e8.getText().toString();
                String aadhar_number=e9.getText().toString();
                String email =e10.getText().toString();
                String phone_number =e11.getText().toString();
                String password =e12.getText().toString();
                String confirm_password=e13.getText().toString();
if(password.equalsIgnoreCase(confirm_password)) {
    uploadBitmap(name, place, post, pin, gender, age, blood_group, license_number, aadhar_number, phone_number,email,password);
}

}});

}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {

            Uri imageUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                i.setImageBitmap(bitmap);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //converting to bitarray
    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    private void uploadBitmap(final String name, final String place, final String post, final String pin, final String gender, final String age, final String blood_group, final String license_number, final String aadhar_number, final String phone_number, String email, String password) {


        pd = new ProgressDialog(registration.this);
        pd.setMessage("Uploading....");
        pd.show();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            pd.dismiss();


                            JSONObject obj = new JSONObject(new String(response.data));

                            if(obj.getString("status").equals("ok")){
                                Toast.makeText(getApplicationContext(), "Registration success", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(getApplicationContext(), public_home.class);
                                startActivity(i);
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Registration failed" ,Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                SharedPreferences o = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                params.put("e1", name);//passing to python
                params.put("e2", place);//passing to python
                params.put("e3", post);
                params.put("e4", pin);
                params.put("e5", gender);
                params.put("e6", age);
                params.put("e7", blood_group);
                params.put("e8", license_number);
                params.put("e9", aadhar_number);
                params.put("e10", phone_number);
                params.put("e11", email);
                params.put("e12", password);
                return params;
            }


            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("pic", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

}



