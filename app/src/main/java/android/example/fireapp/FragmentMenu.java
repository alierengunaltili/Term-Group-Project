package android.example.fireapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentMenu#newInstance} factory method to
 * create an instance of this fragment.
 *
 * This is basically the pop-up menu for adding new dishes to the menu
 * from the restaurant owner's GUI.
 *@date 27.05.2020
 *@author Group 3C
 */

public class FragmentMenu extends Fragment implements View.OnClickListener{

    //Constants

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //Variables

    String mParam1;
    String mParam2;

    View view;
    EditText etName, etPrice, etDesc;
    ImageView reject, confirm;

    DatabaseReference mRef;
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    FirebaseUser user;

    //Constructors

    public FragmentMenu() {
        // Required empty public constructor
    }

    //Initialization of fragment menu
    public static FragmentMenu newInstance(String param1, String param2) {
        FragmentMenu fragment = new FragmentMenu();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_fragment_menu, container, false);
        etName = view.findViewById(R.id.name_of_dish);
        etPrice = view.findViewById(R.id.price_of_dish);
        etDesc = view.findViewById(R.id.description_of_dish);
        reject = view.findViewById(R.id.reject);
        confirm = view.findViewById(R.id.confirm);

        etName.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        etPrice.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        etDesc.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference("Restaurants");
        user = mAuth.getCurrentUser();

        reject.setOnClickListener(this);
        confirm.setOnClickListener(this);

        return view;
    }

    /**
     * This method contains reject and confirm buttons, and gives functionality to the them.
     * While reject button leads user to the previous page, confirm buttons adds new item to the menu,
     * and updates the firebase.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.reject:
                getActivity().onBackPressed();
                break;
            case R.id.confirm:
                Intent intent = getActivity().getIntent();
                String fireName = intent.getStringExtra("NAME");
                mRef.child(user.getUid()).child("menu").orderByChild("name").equalTo(fireName).addValueEventListener(new ValueEventListener() {

                    final String nameText = etName.getText().toString();
                    final String priceText = etPrice.getText().toString();
                    final String descText = etDesc.getText().toString();
                    int priceValue = 0;

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //Saves changes of name only if it is not empty

                        if ((("").equals(priceText))) {
                            etPrice.setError("Price can't be empty");
                            etPrice.requestFocus();
                            return;
                        }
                        else {
                            priceValue = Integer.parseInt(priceText);
                        }
                        if ((("").equals(nameText))) {
                            etName.setError("Name can't be empty");
                            etName.requestFocus();
                            return;
                        }
                        if ((("").equals(descText))) {
                            etDesc.setError("Description can't be empty");
                            etDesc.requestFocus();
                            return;
                        }

                        String uidFood = mRef.child(user.getUid()).child("menu").push().getKey();
                        mRef.child(user.getUid()).child("menu").child(uidFood).setValue( new Food(nameText, descText, priceValue) );

                        Toast.makeText(getActivity(), "Dish added", Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                break;
        }
    }
}
