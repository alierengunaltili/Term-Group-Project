package android.example.fireapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * ImageRecycler for the upload image in restaurant GUI & gallery in customer GUI
 * @date 10.05.2020
 * @author Group 3C
 */

public class ImageRecycler extends AppCompatActivity {

    //Properties

    private RecyclerView recyclerView;
    private Adapter adapter;
    private DatabaseReference databaseReference;
    private List<Upload> uploads;
    FirebaseAuth mAuth;
    FirebaseUser user;

    //Methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_recycler);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        uploads = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference("Restaurants");
        databaseReference.child(user.getUid()).child("Pictures").child("Gallery").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) { //dataSnapshot is a object that store datas like arraylists.
                for( DataSnapshot snapshot : dataSnapshot.getChildren()){ //checking the every object of data
                    Upload upload = snapshot.getValue(Upload.class);
                    uploads.add(upload);
                }
                adapter = new Adapter(ImageRecycler.this,uploads);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ImageRecycler.this,"failed to save the data" + databaseError.getMessage(), Toast.LENGTH_SHORT)
                        .show();
            }
        });

    }
}
