package com.creatershub.attendancesystemstudentapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Login.this,
                        new String[]{Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);
                return;
            }
        }

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("StudentDetails", 0);

        if (!sharedPreferences.getString("userid", "").isEmpty()) {
            Intent intent = new Intent(getApplicationContext(), Dashboard.class);
            startActivity(intent);
            finish();
        }
    }

    public void onClickLogin(View view) {
        TextView loginIdTextView = findViewById(R.id.loginid);
        TextView passwordTextView = findViewById(R.id.password);
        final String login = loginIdTextView.getText().toString();
        final String password = passwordTextView.getText().toString();

        if (login.isEmpty()) {
            Toast.makeText(this, "Please enter your login ID", Toast.LENGTH_SHORT).show();
        }

        else if (password.isEmpty()) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
        }

        else {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("userid", login);
                jsonBody.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String url = "http://attendance-system-archdj.herokuapp.com/studentlogin";
            final ProgressDialog dialog = new ProgressDialog(Login.this);
            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.show();
            JsonObjectRequest jsonObjectRequest =
                    new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Toast.makeText(Login.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                                String id = response.getString("_id");
                                String userid = login;
                                String name = response.getString("name");
                                String branch = response.getString("branch");
                                int semester = response.getInt("semester");
                                String roll = response.getString("roll");
                                String subjects = response.getJSONArray("subjects").toString();
                                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("StudentDetails", 0);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("userid", userid);
                                editor.putString("id", id);
                                editor.putString("password", password);
                                editor.putString("name", name);
                                editor.putString("subjects", subjects);
                                editor.putString("branch", branch);
                                editor.putInt("semester", semester);
                                editor.putString("roll", roll);
                                editor.apply();
                                Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                                startActivity(intent);
                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            dialog.dismiss();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            NetworkResponse networkResponse = error.networkResponse;
                            if (networkResponse == null) {
                                Toast.makeText(Login.this, "Internet Service not available!", Toast.LENGTH_SHORT).show();
                            }

                            else {
                                Log.e("Status", Integer.toString(error.networkResponse.statusCode));
                                Toast.makeText(Login.this, "Wrong credentials!", Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                        }
                    });

            requestQueue.add(jsonObjectRequest);
        }
    }


}
