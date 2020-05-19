package android.example.fireapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class GenreSpecialRes extends AppCompatActivity {
    //Properties
    TextView tvGenreType;
    ListView listViewGenreRestaurants;
    ArrayAdapter myAdapter;
    ArrayList<String> genreSpecificRestaurant = new ArrayList<String>();
    DatabaseReference mRef;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_genre_special_res);

        //Initialize
        tvGenreType = (TextView)findViewById(R.id.genreTypeSpecial);
        listViewGenreRestaurants = (ListView)findViewById(R.id.lvGenreSpecificRestaurants);
        myAdapter = new ArrayAdapter<String>(this, R.layout.listrow, R.id.textView2, genreSpecificRestaurant);
        searchView = (SearchView) findViewById(R.id.searchView);
        listViewGenreRestaurants.setAdapter(myAdapter);
        mRef = FirebaseDatabase.getInstance().getReference("Restaurants");


        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = searchView.findViewById(id);
        textView.setTextColor(Color.WHITE);

        //Set tv the genre type
        Intent i = getIntent();
        String genre = i.getStringExtra("GENRE");
        tvGenreType.setText(genre + " Restaurants");


        //Methods called
        displayRestaurantProfile();
        displayRestaurantsOnList(genre);
        searchRestaurant();
        searchView.clearFocus();
    }


    //METHODS
    private void displayRestaurantsOnList(String genre) {
        mRef.orderByChild("genre").equalTo( genre ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();
                while (items.hasNext()) {

                    DataSnapshot item = items.next();
                    String name = item.child("name").getValue().toString();
                    genreSpecificRestaurant.add(name);
                    myAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void searchRestaurant(){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                myAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    private void displayRestaurantProfile() {
        listViewGenreRestaurants.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //gets the name of the dish
                String item = genreSpecificRestaurant.get(position);
                final String name = item.toString();

                mRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();
                        while (items.hasNext()) {
                            DataSnapshot item1 = items.next();
                            String searchedId;
                            if (item1.child("name").getValue().toString().equals(name)) {

                                searchedId = item1.child("uid").getValue().toString();
                                Intent intent = new Intent(GenreSpecialRes.this, CustomerPOVRestaurant.class);
                                intent.putExtra("UID", searchedId);
                                startActivity( intent);
                                finish();
                                myAdapter .notifyDataSetChanged();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });
    }
}
