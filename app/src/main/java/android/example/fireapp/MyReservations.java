package android.example.fireapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.SQLOutput;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 *This class displays the past and present reservations of a customer, from which he rate past
 * reservations or remove upcoming ones.
 *@date 27.05.2020
 *@author Group 3C
 */

public class MyReservations extends AppCompatActivity {

    //Properties
    ListView lvCurrentReservations, lvPastReservations;
    ArrayAdapter myAdapter, myAdapter2;
    ArrayList<String> pastReservations = new ArrayList<String>();
    ArrayList<String> currentReservations = new ArrayList<String>();

    FirebaseUser user;
    DatabaseReference refCurrentReservations, refPastReservations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_my_reservations);

        //Initialization
        lvCurrentReservations = (ListView)findViewById(R.id.lvCurrentRezCustomer);
        lvPastReservations = (ListView)findViewById(R.id.lvPastRezCustomer);
        myAdapter = new ArrayAdapter<String>(this, R.layout.listrow, R.id.textView2, currentReservations);
        myAdapter2 = new ArrayAdapter<String>(this, R.layout.listrow, R.id.textView2, pastReservations);
        lvCurrentReservations.setAdapter(myAdapter);
        lvPastReservations.setAdapter(myAdapter2);

        refCurrentReservations = FirebaseDatabase.getInstance().getReference("Reservations").child("CurrentReservations");
        refPastReservations = FirebaseDatabase.getInstance().getReference("Reservations").child("PastReservations");
        user = FirebaseAuth.getInstance().getCurrentUser();

        //Methods called
        updatePastReservations();
        displayCurrentReservations();
        displayPastReservations();
        ratePastReservations();
        showCurrentReservation();
        deleteCurrentReservations();
    }

    //METHODS

    /**
     * This method takes the user to a new activity in which detailed information about the
     * reservation can be accessed.
     */
    private void showCurrentReservation() {
        lvCurrentReservations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String reservationTxt = currentReservations.get(position);
                Intent i = new Intent(MyReservations.this, ShowCurrentReservation.class);
                i.putExtra("RESERVATION", reservationTxt);
                startActivity(i);
            }
        });
    }

    /**
     * This method enables users to rate their past reservations ny performing a long click.
     */
    private void ratePastReservations() {
        lvPastReservations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String reservationText = pastReservations.get(position);
                int indexOfResName = reservationText.indexOf("\n");
                String resName = reservationText.substring(0, indexOfResName);
                Intent i = new Intent(MyReservations.this, RateReservation.class);
                i.putExtra("RESTNAME", resName);
                i.putExtra("RESERVTEXT", reservationText);
                startActivity(i);
                finish();
            }
        });
    }

    /**
     * This method moves an upcoming reservation to past reservations
     * once the time of the reservation passes.
     */
    private void updatePastReservations() {
        refCurrentReservations.orderByChild("cusID").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();
                while (items.hasNext()) {
                    DataSnapshot item = items.next();
                    String timeSlot = item.child("timeSlot").getValue().toString();
                    String date = item.child("date").getValue().toString();

                    LocalDate dateOfRez = LocalDate.parse(date);
                    LocalDate today = LocalDate.now();

                    int h = (int) (Integer.parseInt(timeSlot) / 60);
                    int m = Integer.parseInt(timeSlot) % 60;

                    LocalTime rezTime = LocalTime.of(h, m);
                    LocalTime now = LocalTime.now();

                    if (dateOfRez.isBefore(today) || (dateOfRez.isEqual(today) && rezTime.isBefore(now)))
                    {
                        //Clone the reservation & add it to past reservations & remove from current reservations
                        String resName = item.child("restaurantName").getValue().toString();
                        String cusID = item.child("cusID").getValue().toString();
                        String resID = item.child("restaurantID").getValue().toString();
                        String cusName = item.child("cusName").getValue().toString();
                        String resPhone = item.child("restaurantPhone").getValue().toString();
                        String cusPhone = item.child("cusPhone").getValue().toString();
                        String proOrder = item.child("preOrder").getValue().toString();
                        String preOrderTxt = (String)item.child("preOrderText").getValue();
                        String seat = item.child("seat").getValue().toString();
                        String totalPrice = item.child("totalPrice").getValue().toString();
                        String timeSlotClone = item.child("timeSlot").getValue().toString();
                        String dateClone = item.child("date").getValue().toString();
                        String uid = item.child("reservID").getValue().toString();
                        Reservation r = new Reservation(uid, cusID, resID, cusName, resName,
                                cusPhone, resPhone, proOrder, dateClone, timeSlotClone, totalPrice, seat);
                        refPastReservations.child(uid).setValue(r);
                        refPastReservations.child(uid).child("preOrderText").setValue(preOrderTxt);
                        refCurrentReservations.child(uid).removeValue();

                    }
                    myAdapter.notifyDataSetChanged();
                    myAdapter2.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * This method displays the past reservations in the page.
     */
    private void displayPastReservations() {
        refPastReservations.orderByChild("cusID").equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pastReservations.clear();
                Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();
                while (items.hasNext()) {
                    DataSnapshot item = items.next();
                    String resName = item.child("restaurantName").getValue().toString();
                    String rezID = item.child("reservID").getValue().toString();
                    String resPhone = item.child("restaurantPhone").getValue().toString();
                    String proOrder = item.child("preOrder").getValue().toString();
                    String preOrderTxt = (String)item.child("preOrderText").getValue();
                    String seat = item.child("seat").getValue().toString();
                    String totalPrice = item.child("totalPrice").getValue().toString();
                    String timeSlot = item.child("timeSlot").getValue().toString();
                    String date = item.child("date").getValue().toString();
                    String table = "Table " + seat.substring(4);
                    int h = (int) (Integer.parseInt(timeSlot) / 60);
                    int m = Integer.parseInt(timeSlot) % 60;
                    String hS = String.valueOf(h);
                    if (h == 0)
                        hS = "00";
                    String mS = String.valueOf(m);
                    if (m == 0)
                        mS = "00";
                    String timeSlotString = hS + ":" + mS;

                    String toString = resName + "\n" + date + "   " + timeSlotString + " " + "" + table + "\n" +
                            proOrder + "___" + totalPrice + "TL\nRestaurant info: +90 " + resPhone;
                    toString += "\n\n\nPre-order: \n" + preOrderTxt;
                    toString += "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nID:" + rezID + "   ";
                    pastReservations.add(toString);
                    myAdapter.notifyDataSetChanged();
                    myAdapter2.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    /**
     * This method displays the current reservations in the page.
     */
    private void displayCurrentReservations() {

        refCurrentReservations.orderByChild("cusID").equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentReservations.clear();
                Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();
                while (items.hasNext()) {
                    DataSnapshot item = items.next();
                    String resName = item.child("restaurantName").getValue().toString();
                    String resPhone = item.child("restaurantPhone").getValue().toString();
                    String rezID = item.child("reservID").getValue().toString();
                    String proOrder = item.child("preOrder").getValue().toString();
                    String preOrderTxt = (String)item.child("preOrderText").getValue();
                    String seat = item.child("seat").getValue().toString();
                    String totalPrice = item.child("totalPrice").getValue().toString();
                    String timeSlot = item.child("timeSlot").getValue().toString();
                    String date = item.child("date").getValue().toString();
                    String table = "Table " + seat.substring(4);
                    int h = (int)(Integer.parseInt(timeSlot) / 60);
                    int m = Integer.parseInt(timeSlot) % 60;
                    String hS = String.valueOf(h);
                    if (h == 0)
                        hS = "00";
                    String mS = String.valueOf(m);
                    if (m == 0)
                        mS = "00";
                    String timeSlotString = hS + ":" + mS;

                    String toString = resName + "\n" + date + " - " +  timeSlotString + " - " + table +  "\n"+
                            proOrder + ": " + totalPrice + " g3Coins\nRestaurant info: +90 " + resPhone;
                    toString += "\n\n\nPre-order: \n" + preOrderTxt;
                    toString += "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nID:" + rezID + "   ";
                    currentReservations.add(toString);
                    myAdapter.notifyDataSetChanged();
                    myAdapter2.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * This method enables a user to delete their upcoming reservation by long clicking on it.
     */
    private void deleteCurrentReservations(){
        lvCurrentReservations.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final String rezTxt = currentReservations.get(position);

                new AlertDialog.Builder(MyReservations.this)
                        .setIcon(android.R.drawable.ic_input_add)
                        .setTitle("Cancel Reservation!")
                        .setMessage("Are you sure to cancel this reservation?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                int indexOfIDStart = rezTxt.indexOf("ID:") + 3;
                                int indexOfIDEnd  = rezTxt.indexOf("   ", indexOfIDStart);
                                final String rezID = rezTxt.substring(indexOfIDStart, indexOfIDEnd);

                                    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                final String date = (String) dataSnapshot.child("Reservations").child("CurrentReservations").child(rezID).child("date").getValue();
                                                final String restaurantID = (String) dataSnapshot.child("Reservations").child("CurrentReservations").child(rezID).child("restaurantID").getValue();
                                                final String seat = (String) dataSnapshot.child("Reservations").child("CurrentReservations").child(rezID).child("seat").getValue();
                                                final String timeSlot = (String) dataSnapshot.child("Reservations").child("CurrentReservations").child(rezID).child("timeSlot").getValue(); // in the form of minutes

                                                final long maxSeatingDura = (long) dataSnapshot.child("Restaurants").child(restaurantID).child("maxSeatingDuration").getValue();
                                                final int maxSeatingDuration = (int) maxSeatingDura;

                                                for (DataSnapshot snapshot : dataSnapshot.child("Restaurants").child(restaurantID).child("seats").child(seat).child(date).getChildren()) {
                                                    String ts = (String) snapshot.getKey(); // time slot is in the form of minutes!
                                                    int relatedTimeSlot = Integer.parseInt(ts);
                                                    Object tS = snapshot.getValue();
                                                    HashMap<String, Object> oldTimeMap = (HashMap<String, Object>) tS;
                                                    long lay;
                                                    if (snapshot.child("layer").exists()) // all new restaurants have layer
                                                        lay = (long) oldTimeMap.get("layer");
                                                    else // for old type of restaurants - not in use currently
                                                        lay = 0;
                                                    int layer = (int) lay;
                                                    if (layer > 0)
                                                        layer = layer - 1;
                                                    else
                                                        layer = 0;

                                                    if (Math.abs(Integer.parseInt(timeSlot) - relatedTimeSlot) + 1 <= (int) maxSeatingDuration) {
                                                        if (layer != 0) {
                                                            ref.child("Restaurants").child(restaurantID).child("seats").child(seat).child(date).child(String.valueOf(relatedTimeSlot)).child("layer").setValue(layer);
                                                        } else {
                                                            ref.child("Restaurants").child(restaurantID).child("seats").child(seat).child(date).child(String.valueOf(relatedTimeSlot)).child("layer").setValue(layer);
                                                            ref.child("Restaurants").child(restaurantID).child("seats").child(seat).child(date).child(String.valueOf(relatedTimeSlot)).child("reservedStatus").setValue(false);
                                                        }
                                                    }
                                                }

                                                ref.removeEventListener(this);

                                                ref.child("Reservations").
                                                        child("CurrentReservations").
                                                        child(rezID).setValue(null);
                                                myAdapter.notifyDataSetChanged();

                                                Intent i = new Intent(MyReservations.this, MyReservations.class);
                                                Toast.makeText(getApplicationContext(), "Reservation has been deleted", Toast.LENGTH_SHORT).show();
                                                startActivity(i);
                                                finish();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
