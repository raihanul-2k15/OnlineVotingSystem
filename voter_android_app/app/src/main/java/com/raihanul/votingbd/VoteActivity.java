package com.raihanul.votingbd;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class VoteActivity extends AppCompatActivity {
    public static final String LOG_TAG = "TEST";
    private ProgressBar mLoadingProgress;
    private TextView mAlreadyVoted;
    private Button mGoBackToSelect;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private String mVoter_id;
    private String mCampaign;

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference rootRef;

    private List<String> candidateIDs = new ArrayList<String>();
    private List<String> candidateNames = new ArrayList<String>();
    private List<String> candidateDescriptions = new ArrayList<String>();
    private List<Bitmap> candidateImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        mLoadingProgress = (ProgressBar) findViewById(R.id.loading_progress);
        mAlreadyVoted = (TextView) findViewById(R.id.already_voted);
        mGoBackToSelect = (Button) findViewById(R.id.go_back_to_select);

        mRecyclerView = (RecyclerView) findViewById(R.id.candidateList_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        rootRef = storage.getReference();

        mVoter_id = getIntent().getStringExtra("voter_id");
        mCampaign = getIntent().getStringExtra("campaign");

        mGoBackToSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // check if user already voted
        db.collection("votes").whereArrayContains("participants", Long.parseLong(mVoter_id)).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                boolean found = false;
                for (DocumentSnapshot doc : docs) {
                    if (doc.getId().equals(mCampaign)) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    mAlreadyVoted.setVisibility(View.VISIBLE);
                    mLoadingProgress.setVisibility(View.GONE);
                } else {
                    mRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        });

        // download candidate data and populate recycler view
        db.document("campaigns/" + mCampaign).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot documentSnapshot) {

                final List<Long> candidate_IDs = (ArrayList<Long>) documentSnapshot.get("candidates");
                final Task<DocumentSnapshot>[] fTask = new Task[candidate_IDs.size()];
                final Task<byte[]>[] sTask = new Task[candidate_IDs.size()];
                for (int i=0; i<candidate_IDs.size(); i++) {
                    fTask[i] = db.document("candidates/" + candidate_IDs.get(i).toString()).get();
                }
                Tasks.whenAllSuccess(fTask).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                    @Override
                    public void onSuccess(List<Object> objects) {
                        for (int i=0; i<candidate_IDs.size(); i++) {
                            DocumentSnapshot res = fTask[i].getResult();
                            candidateIDs.add(res.getId());
                            candidateNames.add((String) res.get("name"));
                            candidateDescriptions.add((String) res.get("desc"));
                            sTask[i] = rootRef.child(res.getId() + ".jpg").getBytes(1024*1024);
                        }
                        Tasks.whenAllComplete(sTask).addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
                            @Override
                            public void onComplete(@NonNull Task<List<Task<?>>> task) {
                                for (int i = 0; i < candidate_IDs.size(); i++) {
                                    if (sTask[i].isSuccessful()) {
                                        byte[] resBytes = sTask[i].getResult();
                                        candidateImages.add(BitmapFactory.decodeByteArray(resBytes, 0, resBytes.length));
                                    } else {
                                        candidateImages.add(null);
                                    }
                                }
                                mAdapter = new MyGridAdapter(candidateNames, candidateDescriptions, candidateImages, candidateIDs, mVoter_id, mCampaign);
                                mRecyclerView.setAdapter(mAdapter);
                                mLoadingProgress.setVisibility(View.GONE);
                            }
                        });
                    }
                });
            }
        });
    }
}
