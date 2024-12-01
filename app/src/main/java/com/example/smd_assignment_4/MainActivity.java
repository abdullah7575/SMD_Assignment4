package com.example.smd_assignment_4;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

//    FloatingActionButton fab_add;
//    DatabaseReference database;
//    RecyclerView rvNotes;
//    NotesAdapter adapter;
//    // TextView tvResult;
    ImageView ivLogo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        Animation logoAnimation = AnimationUtils.loadAnimation(this,R.anim.logo_animation);
        ivLogo.startAnimation(logoAnimation);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(MainActivity.this,Login.class);
                startActivity(i);
                finish();
            }
        },2000);
//        fab_add = findViewById(R.id.fab_add);
//        rvNotes = findViewById(R.id.rvNotes);
//
//
//        database =  FirebaseDatabase.getInstance().getReference();
//        Query query = database.child("Notes");
//        FirebaseRecyclerOptions<Note> options =
//                new FirebaseRecyclerOptions.Builder<Note>()
//                        .setQuery(query, Note.class)
//                        .build();
//        adapter = new NotesAdapter(options,this);
//        rvNotes.setHasFixedSize(true);
//        rvNotes.setAdapter(adapter);
//
//     /*   tvResult = findViewById(R.id.tvResult);
//
//        database.child("Notes")
//                        .addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                String text = "";
//                                for (DataSnapshot data: snapshot.getChildren())
//                                {
//                                   text += data.child("title").getValue().toString() + "\n"+ data.child("description").getValue().toString()+"\n\n";
//                                }
//
//                                tvResult.setText(text);
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });
//
//*/
//
//        fab_add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.insert_update_note_design, null, false);
//                AlertDialog.Builder addNote = new AlertDialog.Builder(MainActivity.this)
//                        .setTitle("Add New Note")
//                        .setView(v);
//
//                EditText etTitle, etDescription;
//                etTitle = v.findViewById(R.id.etTitle);
//                etDescription = v.findViewById(R.id.etDescription);
//
//                addNote.setPositiveButton("Add", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        HashMap<String, Object> data = new HashMap<>();
//                        data.put("title", etTitle.getText().toString().trim());
//                        data.put("description", etDescription.getText().toString().trim());
//
//                        database.child("Notes")
//                                .push()
//                                .setValue(data)
//                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void unused) {
//                                        Toast.makeText(MainActivity.this, "Note added", Toast.LENGTH_SHORT).show();
//                                    }
//                                })
//                                .addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//
//                    }
//                });
//
//                addNote.show();
//
//            }
//        });
//    }
//    @Override
//    protected void onStart() {
//        super.onStart();
//        adapter.startListening();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        adapter.stopListening();
//    }
    }
    private void init(){
        ivLogo = findViewById(R.id.ivLogo);
    }
}