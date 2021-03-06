    package android.example.fireapp;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AlertDialog;
    import androidx.appcompat.app.AppCompatActivity;

    import android.content.DialogInterface;
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
    import android.widget.Toast;

    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;

    import java.util.ArrayList;
    import java.util.Iterator;

    /**
     * Showing the all restaurants that is saved to database to customers after the All Restaurants button is called from navigation drawer.
     * @date 28.04.2020
     * @author Group_g3C
     */

    public class AllRestaurantsDisplay extends AppCompatActivity {

       //Properties

        ListView listViewAllRestaurants;
        ArrayAdapter myAdapter;
        ArrayList<String> allRestaurants = new ArrayList();
        FirebaseDatabase database;
        DatabaseReference reference, mRef;
        FirebaseAuth mAuth;
        FirebaseUser user;
        SearchView searchView;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_all_restaurants_display);

            //Initialize

            listViewAllRestaurants = (ListView)findViewById(R.id.lvAllRestaurants2);
            myAdapter = new ArrayAdapter<String>(this, R.layout.listrow, R.id.textView2, allRestaurants);
            listViewAllRestaurants.setAdapter(myAdapter);
            database = FirebaseDatabase.getInstance();
            reference = database.getReference();
            mAuth = FirebaseAuth.getInstance();
            mRef = FirebaseDatabase.getInstance().getReference("Customers");
            user = mAuth.getCurrentUser();
            searchView = (SearchView) findViewById(R.id.searchView);
            int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
            TextView textView = searchView.findViewById(id);
            textView.setTextColor(Color.WHITE);

            //Methods called

            displayAllRestaurants();
            listOnLongClickAction();
            displayRestProfileAction();
            searchRestaurant();
            searchView.clearFocus();
        }

        //METHODS

        /**
         * Searching the restaurant and quering them by taking the typed string constantly.
         */
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

    /*
    Prints all of the restaurants on the related listview. Iterates trough firebase and adds each
    restaurant to all restaurants list view.
     */
    private void displayAllRestaurants () {
        reference.child("Restaurants").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();
                while (items.hasNext()) {

                        DataSnapshot item = items.next();
                        String name;
                        name = "" + (String)item.child("name").getValue() + ", " + (String) item.child("genre").getValue();

                        allRestaurants.add(name);
                        myAdapter.notifyDataSetChanged();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }

    /*
    This method makes restaurants long-clickable. If a customers long-clicks on a restaurant, they are
    asked if they want to add that restaurant to favorite restaurants list.
     */
    private void listOnLongClickAction() {
        listViewAllRestaurants.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                //final int index;
                new AlertDialog.Builder(AllRestaurantsDisplay.this)
                        .setIcon(android.R.drawable.ic_input_add)
                        .setTitle("Are you sure?")
                        .setMessage("Do you want to add this restaurant to your favorite restaurants?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String item = myAdapter.getItem(position).toString();
                                int index1 = item.indexOf(", ");
                                final String s = item.substring(0,index1);
                                reference.child("Restaurants").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();
                                        while(items.hasNext()) {
                                            DataSnapshot item1 = items.next();
                                            String searchedId;
                                            if(item1.child("name").getValue().toString().equals(s)){
                                                searchedId = item1.child("uid").getValue().toString();
                                                mRef.child(user.getUid()).child("fav restaurants").child(searchedId).push();
                                                mRef.child(user.getUid()).child("fav restaurants").child(searchedId).child("name").setValue(s);
                                                mRef.child(user.getUid()).child("fav restaurants").child(searchedId).child("uid").setValue(searchedId);
                                                myAdapter.notifyDataSetChanged();
                                                Toast.makeText(getApplicationContext(), "Restaurant has been added to favorites", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                });
                                    myAdapter.notifyDataSetChanged();
                            }
                        })
                            .setNegativeButton("No", null)
                            .show();

                    return true;
                }
            });
    }

        /**
        This method makes all restaurants clickable. If a customer clicks on a restaurant, they are directed
        to the profile of that restaurant.
         */
        private void displayRestProfileAction() {
            listViewAllRestaurants.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //gets the name of the dish
                    String item = allRestaurants.get(position);
                    int indexOfName = item.indexOf(", ");
                    final String name = item.substring(0,indexOfName);

                    final DatabaseReference refRests = FirebaseDatabase.getInstance().getReference("Restaurants");
                    refRests.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();
                            while (items.hasNext()) {
                                DataSnapshot item1 = items.next();
                                String searchedId;
                                String nameOfRestaurant = (String)item1.child("name").getValue();
                                if ((nameOfRestaurant).equals(name)) {

                                    searchedId = item1.child("uid").getValue().toString();
                                    Intent intent = new Intent(AllRestaurantsDisplay.this, CustomerPOVRestaurant.class);
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

        /**
         * if customer presses to back with out taking any action, we delete this activity from activity history
         */
        @Override
        public void onBackPressed() {
            super.onBackPressed();
            finish();
        }
    }
