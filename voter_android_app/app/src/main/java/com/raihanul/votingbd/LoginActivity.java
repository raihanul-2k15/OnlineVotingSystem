package com.raihanul.votingbd;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoginActivity extends AppCompatActivity {
    public static final String LOG_TAG = "TEST";
    public static final String AUTH_PASSWORD = "IAmAVoter123";
    public static final String AUTH_EMAIL = "voter@votingbd.com";
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private TextView name;
    private TextView bday;
    private TextView voter_id;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        name = (TextView) findViewById(R.id.name);
        bday = (TextView) findViewById(R.id.bday);
        voter_id = (TextView) findViewById(R.id.voter_id);
        login = (Button) findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();

                final String nameStr = name.getText().toString();
                final String voter_idStr = voter_id.getText().toString();
                final Date bdayDate;
                final String bdayStr;
                try {
                    bdayDate = new SimpleDateFormat("dd/MM/yyyy").parse(bday.getText().toString());
                    bdayStr = new SimpleDateFormat("dd/MM/yyyy").format(bdayDate);
                } catch (ParseException pe) {
                    Log.d(LOG_TAG, "Date parsing error");
                    return;
                }

                final String key = ShaUtils.sha256(nameStr + voter_idStr + bdayStr);
                final String hash = ShaUtils.sha256(key);

                db.document("voters/" + hash).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.getMetadata().isFromCache()) {
                            showErrorDialog("Connection Error", "You must be connected to internet. Please connect and try again later.");
                            return;
                        }
                        if (!documentSnapshot.exists()) {
                            showErrorDialog("Wrong Info", "Your given information about voter is wrong or the voter has not been registered.");
                            return;
                        }
                        AesUtil aesUtil = new AesUtil(128, 1000);
                        final String v = aesUtil.decrypt(AesUtil.SALT, AesUtil.IV, key, (String)documentSnapshot.get("voterid"));
                        final String n = aesUtil.decrypt(AesUtil.SALT, AesUtil.IV, key, (String)documentSnapshot.get("name"));
                        final String ad = aesUtil.decrypt(AesUtil.SALT, AesUtil.IV, key, (String)documentSnapshot.get("address"));
                        final String ag = aesUtil.decrypt(AesUtil.SALT, AesUtil.IV, key, (String)documentSnapshot.get("age"));
                        final String bd = aesUtil.decrypt(AesUtil.SALT, AesUtil.IV, key, (String)documentSnapshot.get("bday"));

                        // TODO: When auth is working, put this code inside auth
                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(LoginActivity.this, UserDetailsActivity.class);
                        i.putExtra("hash", hash);
                        i.putExtra("name", n);
                        i.putExtra("voter_id", v);
                        i.putExtra("bday", bd);
                        i.putExtra("address", ad);
                        i.putExtra("age", ag);
                        startActivity(i);

                        // TODO: this is not working
                        FirebaseUser user = auth.getCurrentUser();
                        Log.d(LOG_TAG, Boolean.toString(user==null));
                        if (user == null) {
                            auth.signInWithEmailAndPassword("refat", "chinga12345").addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Log.d(LOG_TAG, "Inside onsuccess: " + authResult.toString());
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(LOG_TAG, "Firebase Auth error:" + e.getMessage());
                                }
                            });
                        } else {
                            Log.d(LOG_TAG, "Already authed? HOW?");
                        }
                        // TODO: upto this not working
                    }
                });
            }
        });
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void showErrorDialog(String title, String msg) {
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(LoginActivity.this);
        adBuilder.setTitle(title).setMessage(msg).setIcon(R.drawable.ic_error_red_24dp)
        .setPositiveButton("Go back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }
}
