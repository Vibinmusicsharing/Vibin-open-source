package com.shorincity.vibin.music_sharing.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.utils.AppConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
// signup page
public class SignUpAct extends AppCompatActivity {

    Animation frombottom, fromtop;
    Button btnjoin_signup, btnLogin_signup;
    TextView textView2;
    EditText email_signup, password_signup, password_confirm, cust_full_name, username;
    ProgressBar loading;

    public String URL = AppConstants.BASE_URL+"user/signup/";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_FULLNAME = "fullname";
    public static final String TYPE_OF_REGISTRATION = "typeOfRegistration";
    public static final String TIME_OF_REGISTRATION = "timeOfRegistration";

    private String signUpMethod, email, googleId;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // get Intent Data
        getIntentData();

        TextView text = findViewById(R.id.textviewtext);

        frombottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);
        fromtop = AnimationUtils.loadAnimation(this, R.anim.fromtop);

        loading = (ProgressBar) findViewById(R.id.loading_signup);
        btnjoin_signup = (Button) findViewById(R.id.btn_submit);
        btnLogin_signup = (Button) findViewById(R.id.btnLogin_signup);

        textView2 = (TextView) findViewById(R.id.textView2);

        email_signup = (EditText) findViewById(R.id.edt_email_signup);

        password_signup = (EditText) findViewById(R.id.password_signup);
        password_confirm = (EditText) findViewById(R.id.confirm_password);
        cust_full_name = (EditText) findViewById(R.id.cust_full_name);
        username = (EditText) findViewById(R.id.tv_username);


        btnjoin_signup.startAnimation(frombottom);
        btnLogin_signup.startAnimation(frombottom);

        text.startAnimation(frombottom);
        textView2.startAnimation(fromtop);

        username.startAnimation(fromtop);
        cust_full_name.startAnimation(fromtop);
        email_signup.startAnimation(fromtop);
        password_signup.startAnimation(fromtop);
        password_confirm.startAnimation(fromtop);


        btnLogin_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpAct.this, LoginAct.class);
                startActivity(intent);
                finish();
            }
        });

        btnjoin_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });

    }

    private void getIntentData() {

        signUpMethod = "";

        try {
            signUpMethod = getIntent().getStringExtra(AppConstants.INTENT_SIGN_UP_METHOD);
            email = getIntent().getStringExtra(AppConstants.INTENT_SIGN_UP_METHOD);
            googleId = getIntent().getStringExtra(AppConstants.INTENT_GOOGLE_ID);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(TextUtils.isEmpty(signUpMethod))
                signUpMethod = AppConstants.SIGNUP_BY_APP;

            if(!TextUtils.isEmpty(email)) {
                email_signup.setText(email);
                email_signup.setEnabled(false);
                email_signup.setAlpha(0.5f);
            }


            if(!TextUtils.isEmpty(googleId)) {
                password = googleId;
                password_confirm.setVisibility(View.GONE);
                password_signup.setVisibility(View.GONE);
            }

        }
    }

    // create account fucntion
    private void CreateAccount() {

        String userName = username.getText().toString().trim();
        String fullName = cust_full_name.getText().toString().trim();
        String email = email_signup.getText().toString().trim();
        password = password_signup.getText().toString().trim();
        String conf_pass = password_confirm.getText().toString().trim();
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm.isAcceptingText()) {
//            writeToLog("Software Keyboard was shown");
            hideSoftKeyboard(SignUpAct.this);
        } else {
//            writeToLog("Software Keyboard was not shown");
        }


        if (TextUtils.isEmpty(userName) ) {
            Toast.makeText(this, "Please enter User Name!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(fullName) ) {
            Toast.makeText(this, "Please enter your Full Name!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter valid Email Id!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter the Password!", Toast.LENGTH_SHORT).show();
        } else if (password.length() < 6) {
            Toast.makeText(this, "Password must contain at least 6 characters!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(conf_pass)) {
            Toast.makeText(this, "Please enter the Confirm Password!", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(conf_pass)) {
            Toast.makeText(this, "Password and Confirm Password should be same!", Toast.LENGTH_SHORT).show();
        } else {
            loading.setVisibility(View.VISIBLE);
            register(userName, fullName, email, password);
        }
    }

    // registering you through server
    private void register(final String userName, final String fullName , final String email, final String password) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean UserCreated = jsonObject.getBoolean("User Created");


                    if (UserCreated) {
                        Toast.makeText(SignUpAct.this, "Successfully Created your account", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUpAct.this, LoginAct.class);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SignUpAct.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    loading.setVisibility(View.GONE);
                    btnjoin_signup.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SignUpAct.this, "please check your internet connection", Toast.LENGTH_SHORT).show();
                loading.setVisibility(View.GONE);
                btnjoin_signup.setVisibility(View.VISIBLE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                String timeOfRegistration = dateFormat.format(date);

                params.put(KEY_EMAIL, email);
                params.put(KEY_PASSWORD, password);
                params.put(KEY_USERNAME, userName);
                params.put(KEY_FULLNAME, fullName);
                params.put(TYPE_OF_REGISTRATION, signUpMethod);
                params.put(TIME_OF_REGISTRATION, timeOfRegistration);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public static void hideSoftKeyboard(Activity activity) {

        // Swati : need to change this
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
}
