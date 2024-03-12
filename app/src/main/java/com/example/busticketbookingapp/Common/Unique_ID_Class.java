package com.example.busticketbookingapp.Common;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.CompletableFuture;

public class Unique_ID_Class {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("uniqueId");

    public CompletableFuture<String> getUniqueIDOfRoute() {
        CompletableFuture<String> future = new CompletableFuture<>();

        reference.child("routeLastID").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Integer routeLastID = dataSnapshot.getValue(Integer.class);
                    Integer temp = routeLastID + 1;
                    String uniqueID = "RID" + String.format("%06d", temp);

                    reference.child("routeLastID").setValue(temp);

                    future.complete(uniqueID);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Database Error: " + databaseError.getMessage());
                future.completeExceptionally(databaseError.toException());
            }
        });

        return future;
    }


    public CompletableFuture<String> getUniqueIDOfTicket() {
        CompletableFuture<String> future = new CompletableFuture<>();

        reference.child("ticketLastId").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Integer ticketLastID = dataSnapshot.getValue(Integer.class);
                    Integer temp = ticketLastID + 1;
                    String uniqueID = "TID" + String.format("%06d", temp);

                    reference.child("ticketLastId").setValue(temp);

                    future.complete(uniqueID);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Database Error: " + databaseError.getMessage());
                future.completeExceptionally(databaseError.toException());
            }
        });

        return future;
    }

}
