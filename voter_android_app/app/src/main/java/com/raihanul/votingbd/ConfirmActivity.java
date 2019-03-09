package com.raihanul.votingbd;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.List;
import java.util.Map;

public class ConfirmActivity extends AppCompatActivity {
    public static final String LOG_TAG = "TEST";
    private String voter_id;
    private String campaign;
    private String candidateID;

    private TextView votingRes;
    private ImageView candidateImg;
    private TextView candidateName;
    private TextView candidateDesc;
    private Button cancel;
    private Button confirm;
    private Button done;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        votingRes = (TextView) findViewById(R.id.voting_res);
        candidateImg = (ImageView) findViewById(R.id.candidate_img);
        candidateName = (TextView) findViewById(R.id.candidate_name);
        candidateDesc = (TextView) findViewById(R.id.candidate_description);
        cancel = (Button) findViewById(R.id.cancel);
        confirm = (Button) findViewById(R.id.confirm);
        done = (Button) findViewById(R.id.done);

        Intent i = getIntent();
        voter_id = i.getStringExtra("voter_id");
        campaign = i.getStringExtra("campaign");
        candidateID = i.getStringExtra("candidate_id");
        candidateName.setText(i.getStringExtra("candidate_name"));
        candidateDesc.setText(i.getStringExtra("candidate_desc"));
        Bitmap c_img = (Bitmap) i.getExtras().get("candidate_img");
        if (c_img != null) {
            candidateImg.setImageBitmap(c_img);
        }

        db = FirebaseFirestore.getInstance();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.document("campaigns/"+campaign).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(final DocumentSnapshot documentSnapshot) {
                        if (!documentSnapshot.getBoolean("active")) {
                            failure();
                            return;
                        }
                        db.runTransaction(new Transaction.Function<Void>() {
                            @Nullable
                            @Override
                            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                DocumentReference dRef = db.document("votes/" + campaign);
                                DocumentSnapshot doc = transaction.get(dRef);
                                Map<String, Long> count = (Map<String, Long>) doc.get("count");
                                List<Long> participants = (List<Long>) doc.get("participants");
                                participants.add(Long.parseLong(voter_id));
                                count.put(candidateID, count.get(candidateID) + 1);
                                transaction.update(dRef, "count", count);
                                transaction.update(dRef, "participants", participants);
                                return null;
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                successful();
                                Log.d(LOG_TAG, "Transaction success");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showCustomDialog("Unknown Error", "Some unknown error has ocurred", true);
                                Log.d(LOG_TAG, e.toString());
                            }
                        });
                    }
                });
            }
        });
    }

    private void successful() {
        candidateDesc.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);
        confirm.setVisibility(View.GONE);

        votingRes.setText("Successfully voted in " + campaign + " for: ");
        votingRes.setVisibility(View.VISIBLE);
        done.setVisibility(View.VISIBLE);

        showCustomDialog("Success!", "Voting successful!", false);
    }

    private void failure() {
        candidateImg.setVisibility(View.GONE);
        candidateName.setVisibility(View.GONE);
        candidateDesc.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);
        confirm.setVisibility(View.GONE);

        votingRes.setText("Campaign " + campaign +" was over. You could not vote in time.");
        votingRes.setVisibility(View.VISIBLE);
        done.setVisibility(View.VISIBLE);

        showCustomDialog("Failure", "Campaign is over. Your vote will not be counted", true);
    }

    private void showCustomDialog(String title, String msg, boolean error) {
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(this);
        adBuilder.setTitle(title).setMessage(msg).setIcon(error ? R.drawable.ic_error_red_24dp : R.drawable.ic_done_green_24dp)
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }
}
