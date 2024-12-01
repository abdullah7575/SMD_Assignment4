package com.example.smd_assignment_4;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ShoppingList extends AppCompatActivity {
    FloatingActionButton fab_add;
    DatabaseReference database;
    RecyclerView rvItems;
    Button btnVerify;
    Button btnLogout;
    ItemsAdaptor adapter;
    FirebaseAuth auth;
    FirebaseUser user;
    String uID;
    FirebaseFirestore firestoredb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shopping_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        user.reload();
        if(user==null)
        {
            startActivity(new Intent(ShoppingList.this, Login.class));
            finish();
        }
        else
        {

            if(user.isEmailVerified())
            {
                btnVerify.setVisibility(View.GONE);
                btnLogout.setVisibility(View.VISIBLE);
            }
            else
            {
                btnLogout.setVisibility(View.GONE);
                btnVerify.setVisibility(View.VISIBLE);
            }

        }

        DatabaseReference shoppingItemsRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uID)
                .child("shopping_items");

        // Create FirebaseRecyclerOptions with Item model
        FirebaseRecyclerOptions<Item> options = new FirebaseRecyclerOptions.Builder<Item>()
                .setQuery(shoppingItemsRef, Item.class) // Specify the database reference and model class
                .build();

        // Create the FirebaseRecyclerAdapter
        adapter = new ItemsAdaptor(options, ShoppingList.this);  // or 'this' if it's an Activity
        rvItems.setHasFixedSize(true);
        rvItems.setAdapter(adapter);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = LayoutInflater.from(ShoppingList.this).inflate(R.layout.add_new_item_design, null,
                        false);
                AlertDialog.Builder addItemDialog = new AlertDialog.Builder(ShoppingList.this)
                        .setTitle("Add New Item")
                        .setView(v);
                EditText etItemName,etItemQuantity,etItemPrice;
                etItemName = v.findViewById(R.id.etItemName);
                etItemQuantity = v.findViewById(R.id.etItemQuantity);
                etItemPrice = v.findViewById(R.id.etItemPrice);
                addItemDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name;
                        int price,quantity;
                        name = etItemName.getText().toString().trim();
                        quantity = Integer.parseInt(etItemQuantity.getText().toString().trim());
                        price = Integer.parseInt(etItemQuantity.getText().toString().trim());
                        if(name.isEmpty() || etItemQuantity.getText().toString().trim().isEmpty() || etItemPrice.getText().toString().trim().isEmpty()){
                            Toast.makeText(ShoppingList.this, "Please enter all details", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        addItemToRealtimeDatabase(new Item(name,quantity,price,-1));
                        addItemToFireStore(name,quantity,price);

                    }
                })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                addItemDialog.show();
            }
        });

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.sendEmailVerification()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(ShoppingList.this, "Verify your Email from your Inbox", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ShoppingList.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                startActivity(new Intent(ShoppingList.this, Login.class));
                finish();
            }
        });
    }
    private void init(){
        btnVerify = findViewById(R.id.btnVerify);
        btnLogout = findViewById(R.id.btnLogout);
        fab_add = findViewById(R.id.fab_add);
        rvItems = findViewById(R.id.rvItems);
        auth = FirebaseAuth.getInstance();  //Note if firebasedatabase then .getInstance().getReference()
        user = auth.getCurrentUser();
        uID = user.getUid();
        firestoredb = FirebaseFirestore.getInstance();
    }
    private void addItemToRealtimeDatabase(Item newItem) {
        DatabaseReference shoppingItemsRef = FirebaseDatabase.getInstance().getReference("users").child(uID).child("shopping_items");

        // Get the current highest itemId by fetching all items
        shoppingItemsRef.orderByChild("itemId").limitToLast(1).get()
                .addOnSuccessListener(dataSnapshot -> {
                    int newItemId = 1;  // Default to 1 if no items exist

                    // Check if there are any existing items
                    if (dataSnapshot.exists()) {
                        // Get the highest itemId from the last item in the query
                        DataSnapshot lastItem = dataSnapshot.getChildren().iterator().next();
                        newItemId = lastItem.child("itemId").getValue(Integer.class) + 1;
                    }

                    // Set the itemId for the new item
                    newItem.setItemId(newItemId);

                    // Add the new item to Realtime Database
                    shoppingItemsRef.child(String.valueOf(newItemId)).setValue(newItem)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Item added to RTDB", Toast.LENGTH_SHORT).show();
//                                Log.d("AddItem", "Item added to Realtime Database with itemId: " + newItemId);
                            })
                            .addOnFailureListener(e ->
                                    Log.e("AddItem", "Error adding item to Realtime Database", e));
                    Toast.makeText(this, "Error in adding item to RTDB", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Log.e("AddItem", "Error fetching highest itemId in Realtime Database", e));
    }
    private void addItemToFireStore(String itemName, int quantity, int price) {
        // Get reference to Firestore collection for the authenticated user
        CollectionReference shoppingItemsRef = firestoredb.collection("users").document(uID).collection("shopping_items");

        // Get the highest current itemId by querying all items and ordering by itemId
        shoppingItemsRef.orderBy("itemId", Query.Direction.DESCENDING).limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int newItemId = 1;  // Default to 1 if no items exist

                    // Check if we have any items
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot lastItem = queryDocumentSnapshots.getDocuments().get(0);
                        newItemId = lastItem.getLong("itemId").intValue() + 1;  // Increment last item's itemId
                    }

                    // Create the new item
                    Item newItem = new Item(itemName, quantity, price, newItemId);

                    // Add the item to Firestore with the generated itemId
                    shoppingItemsRef.document(String.valueOf(newItemId)).set(newItem)
                            .addOnSuccessListener(aVoid -> {
//                                Log.d("AddItem", "Item added successfully with itemId: " + newItemId);
//                                addItemToRealtimeDatabase(newItem);  // Also add to Realtime Database
                                Toast.makeText(this, "Item added to Firestore", Toast.LENGTH_SHORT).show();

                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Error in adding to Firestore", Toast.LENGTH_SHORT).show());

//                    Log.e("AddItem", "Error adding item to Firestore", e));
                })
                .addOnFailureListener(e ->
//                        Log.e("AddItem", "Error fetching highest itemId", e));
                        Toast.makeText(this, "error adding to Firestore", Toast.LENGTH_SHORT).show());

    }
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}