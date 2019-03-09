package com.raihanul.votingbd;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UserDetailsActivity extends AppCompatActivity {

    private ProgressBar imgLoadProgress;
    private ImageView voterImg;
    private TextView txtName;
    private TextView txtVoterID;
    private TextView txtBday;
    private TextView txtAge;
    private TextView txtAddress;
    private Button go_back_to_login;
    private Button proceed_to_voting;

    private FirebaseStorage storage;
    private StorageReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        storage = FirebaseStorage.getInstance();
        rootRef = storage.getReference();

        imgLoadProgress = (ProgressBar) findViewById(R.id.img_loading_progress);
        voterImg = (ImageView) findViewById(R.id.voter_img);
        txtName = (TextView) findViewById(R.id.user_details_txtName);
        txtVoterID = (TextView) findViewById(R.id.user_details_txtVoterID);
        txtBday = (TextView) findViewById(R.id.user_details_txtBday);
        txtAge = (TextView) findViewById(R.id.user_details_txtAge);
        txtAddress = (TextView) findViewById(R.id.user_details_txtAddress);
        go_back_to_login = (Button) findViewById(R.id.go_back_to_login);
        proceed_to_voting = (Button) findViewById(R.id.proceed_to_voting);

        Bundle intentData = getIntent().getExtras();
        final String hash = intentData.getString("hash");
        final String nameStr = intentData.getString("name");
        final String voter_idStr = intentData.getString("voter_id");
        final String bdayStr = intentData.getString("bday");
        final String addressStr = intentData.getString("address");
        final String ageStr = intentData.getString("age");

        rootRef.child("voters").child(hash + ".jpg").getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap img = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                voterImg.setImageBitmap(img);
                imgLoadProgress.setVisibility(View.GONE);
                voterImg.setVisibility(View.VISIBLE);
            }
        });

        txtName.setText(nameStr);
        txtVoterID.setText(voter_idStr);
        txtBday.setText(bdayStr);
        txtAge.setText("Age: " + ageStr);
        txtAddress.setText(addressStr);

        go_back_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        proceed_to_voting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserDetailsActivity.this, SelectCampaignActivity.class);
                i.putExtra("voter_id", voter_idStr);
                startActivity(i);
            }
        });
    }
}
