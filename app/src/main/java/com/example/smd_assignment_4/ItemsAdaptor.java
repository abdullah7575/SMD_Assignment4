package com.example.smd_assignment_4;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;

public class ItemsAdaptor extends FirebaseRecyclerAdapter<Item, ItemsAdaptor.ItemsViewHolder> {
    Context context;
    FirebaseAuth auth;
    FirebaseUser user;
    String uID;
    FirebaseFirestore firestoredb;
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ItemsAdaptor(@NonNull FirebaseRecyclerOptions<Item> options, Context context) {
        super(options);
        this.context = context;
    }

//    @Override
//    protected void onBindViewHolder(@NonNull ItemsViewHolder holder, int position, @NonNull Item model) {
//        holder.tvTitle.setText(model.getTitle());
//        holder.tvDescription.setText(model.getDescription());
//        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                View v = LayoutInflater.from(context).inflate(R.layout.insert_update_Item_design, null, false);
//                AlertDialog.Builder editItem = new AlertDialog.Builder(context)
//                        .setTitle("Update Item")
//                        .setView(v);
//
//                EditText etTitle, etDescription;
//                etTitle = v.findViewById(R.id.etTitle);
//                etDescription = v.findViewById(R.id.etDescription);
//
//                etTitle.setText(model.getTitle());
//                etDescription.setText(model.getDescription());
//
//
//                editItem.setPositiveButton("Update", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        HashMap<String, Object> data = new HashMap<>();
//                        data.put("title", etTitle.getText().toString().trim());
//                        data.put("description", etDescription.getText().toString().trim());
//
//                        FirebaseDatabase.getInstance().getReference().child("Items")
//                                .child(getRef(position).getKey())
//                                .updateChildren(data)
//                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void unused) {
//                                        Toast.makeText(context, "Record Updated", Toast.LENGTH_SHORT).show();
//                                    }
//                                })
//                                .addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//
//                    }
//                });
//
//                editItem.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        FirebaseDatabase.getInstance().getReference().child("Items")
//                                .child(getRef(position).getKey())
//                                .removeValue()
//                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void unused) {
//                                        Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                    }
//                });
//
//                editItem.show();
//                return false;
//            }
//        });
//    }

    @Override
    protected void onBindViewHolder(@NonNull ItemsViewHolder holder, int position, @NonNull Item model) {
        holder.tvItemName.setText(model.getName());
        holder.tvItemQuantity.setText(model.getQuantity());
        holder.tvItemPrice.setText(model.getPrice());

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the itemId of the current item
                String itemId = String.valueOf(model.getItemId());  // Assuming itemId is an integer
                deleteItemFromRealTimeDatabase(itemId);
                deleteItemFromFirestore(itemId);
            }
        });

//        holder.itemView.setOnLongClickListener(view -> {
//            View v = LayoutInflater.from(context).inflate(R.layout.insert_update_Item_design, null, false);
//            AlertDialog.Builder editItem = new AlertDialog.Builder(context)
//                    .setTitle("Update Item")
//                    .setView(v);
//
//            EditText etTitle = v.findViewById(R.id.etTitle);
//            EditText etDescription = v.findViewById(R.id.etDescription);
//
//            etTitle.setText(model.getTitle());
//            etDescription.setText(model.getDescription());
//
//            editItem.setPositiveButton("Update", (dialogInterface, i) -> {
//                HashMap<String, Object> data = new HashMap<>();
//                data.put("title", etTitle.getText().toString().trim());
//                data.put("description", etDescription.getText().toString().trim());
//
//                FirebaseDatabase.getInstance().getReference()
//                        .child("Items")
//                        .child(getRef(position).getKey())
//                        .updateChildren(data)
//                        .addOnSuccessListener(unused -> Toast.makeText(context, "Record Updated", Toast.LENGTH_SHORT).show())
//                        .addOnFailureListener(e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show());
//            });
//
//            editItem.setNegativeButton("Delete", (dialogInterface, i) -> {
//                FirebaseDatabase.getInstance().getReference()
//                        .child("Items")
//                        .child(getRef(position).getKey())
//                        .removeValue()
//                        .addOnSuccessListener(unused -> Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show());
//            });
//
//            editItem.show();
//            return false;
//        });
    }

    @NonNull
    @Override
    public ItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.single_item_design, parent, false);
        //notice here...
        return new ItemsViewHolder(v);
    }
    private void init(){
        auth = FirebaseAuth.getInstance();  //Note if firebasedatabase then .getInstance().getReference()
        user = auth.getCurrentUser();
        uID = user.getUid();
        firestoredb = FirebaseFirestore.getInstance();
    }
    private void deleteItemFromRealTimeDatabase(String itemId) {
        // First, delete the item from Realtime Database
        DatabaseReference shoppingItemsRef = FirebaseDatabase.getInstance().getReference("users").child(uID).child("shopping_items");

        shoppingItemsRef.child(itemId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Delete", "Item successfully deleted from Realtime Database");
                    // After deletion, renumber the remaining items
                    renumberItemsInRealtimeDatabase();
                })
                .addOnFailureListener(e -> Log.e("Delete", "Error deleting item from Realtime Database", e));
    }

    private void renumberItemsInRealtimeDatabase() {
        DatabaseReference shoppingItemsRef = FirebaseDatabase.getInstance().getReference("users").child(uID).child("shopping_items");

        // Get all items ordered by itemId
        shoppingItemsRef.orderByChild("itemId").get()
                .addOnSuccessListener(dataSnapshot -> {
                    int newItemId = 1;  // Start renumbering from 1

                    // Loop through all remaining items and update their itemId
                    for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                        String itemId = itemSnapshot.getKey();
                        shoppingItemsRef.child(itemId).child("itemId").setValue(newItemId)
                                .addOnSuccessListener(aVoid -> {
//                                    Log.d("Renumber", "Item ID updated to: " + newItemId);
                                })
                                .addOnFailureListener(e -> Log.e("Renumber", "Error updating item ID", e));

                        newItemId++;  // Increment the itemId for the next item
                    }
                })
                .addOnFailureListener(e -> Log.e("Renumber", "Error fetching items in Realtime Database", e));
    }

    //deleting from firestore
    private void deleteItemFromFirestore(String itemId) {
        // Delete item from Firestore
        firestoredb.collection("users").document(uID).collection("shopping_items").document(itemId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Delete", "Item successfully deleted!");
                    // Delete item from Realtime Database
                    FirebaseDatabase.getInstance().getReference("users").child(uID)
                            .child("shopping_items").child(itemId).removeValue()
                            .addOnSuccessListener(aVoid1 -> {
                                Log.d("Delete", "Item successfully deleted from Realtime Database");
                                // Renumber remaining items in Firestore
                                renumberItemsInFirestore();
                            });
                })
                .addOnFailureListener(e -> Log.e("Delete", "Error deleting item from Firestore", e));
    }

    private void renumberItemsInFirestore() {
        // Fetch all remaining items in Firestore, ordered by itemId
        firestoredb.collection("users").document(uID).collection("shopping_items")
                .orderBy("itemId")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> items = queryDocumentSnapshots.getDocuments();
                    int newId = 1;  // Start renumbering from 1

                    // Loop through remaining items and update their itemId
                    for (DocumentSnapshot item : items) {
                        String oldItemId = item.getId();
                        firestoredb.collection("users").document(uID)
                                .collection("shopping_items").document(oldItemId)
                                .update("itemId", newId)
                                .addOnSuccessListener(aVoid -> {
//                                    Log.d("Renumber", "Item ID updated to: " + newId);
                                })
                                .addOnFailureListener(e -> Log.e("Renumber", "Error updating item ID", e));

                        newId++;  // Increment the itemId
                    }
                })
                .addOnFailureListener(e -> Log.e("Renumber", "Error fetching remaining items", e));
    }


    public class ItemsViewHolder extends RecyclerView.ViewHolder{
        TextView tvItemName, tvItemQuantity, tvItemPrice;
        ImageView ivDelete, ivEdit;
        public ItemsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemQuantity = itemView.findViewById(R.id.tvItemQuantity);
            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            ivEdit = itemView.findViewById(R.id.ivEdit);
        }
    }
}
