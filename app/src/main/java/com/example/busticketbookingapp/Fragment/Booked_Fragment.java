package com.example.busticketbookingapp.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.busticketbookingapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.EncodeHintType;
import android.graphics.Color;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Booked_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Booked_Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public String username;
    LinearLayout parentLayout;

    public Booked_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Booked_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Booked_Fragment newInstance(String param1, String param2) {
        Booked_Fragment fragment = new Booked_Fragment();
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        SharedPreferences prefs = getActivity().getSharedPreferences("getUsernameFromPrefrence", Context.MODE_PRIVATE);
        username = prefs.getString("username", "");

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_history_, container, false);
        parentLayout = rootView.findViewById(R.id.linear_layout_container);
//        List<String> ticketIds = new ArrayList<>();
        String ticketIds;
        // Retrieve user ticket IDs from the database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(username);
        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if data exists
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        // Get user ticket IDs
                        DataSnapshot ticketsSnapshot = userSnapshot.child("ticketsBookByUser");
                        Log.d("testing log", "1");
                        if (ticketsSnapshot.exists()) {
                            // Iterate through ticket IDs
                            for (DataSnapshot ticketSnapshot : ticketsSnapshot.getChildren()) {
                                String ticketId = ticketSnapshot.getValue(String.class);
                                getTicketData(ticketId);
                                Log.d("testing log", "Id : " + ticketId);
                            }

                        }
                    }
                } else {
                    // Handle case where no data exists
                    Toast.makeText(getContext(), "No user data found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
            }
        });
        return rootView;
    }

    private void getTicketData(String ticketId) {
        DatabaseReference ticketsReference = FirebaseDatabase.getInstance().getReference("tickets").child(ticketId);
        ticketsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get ticket data
                    String busPlateNumber = dataSnapshot.child("busPlateNumber").getValue(String.class);
                    String noOfPassenger = dataSnapshot.child("noOfPassengers").getValue(Long.class).toString();
                    Boolean ticketIsCheckByDriver = dataSnapshot.child("ticketIsCheckByDriver").getValue(Boolean.class);
                    String userSourceName = dataSnapshot.child("userSourceName").getValue(String.class);
                    String userDestinationName = dataSnapshot.child("userDestinationName").getValue(String.class);
                    String ticket_id = dataSnapshot.child("ID").getValue(String.class);


                    TextView textView = new TextView(getContext());

                    textView.setText("Bus Plate No.: "+busPlateNumber +
                            "\nNo. of Passenger : "+noOfPassenger +
                            "\nTicket Check by driver : "+ ticketIsCheckByDriver+
                            "\nFrom : "+userSourceName +
                            "\nTo : "+ userDestinationName);

                    if (!ticketIsCheckByDriver) {
                        textView.setBackgroundResource(R.drawable.ticket_check_yes);
                    } else {
                        textView.setBackgroundResource(R.drawable.ticket_check_no);
                    }




                    textView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            // Call your function here
                            ticketReceipt(ticket_id);
                            return true; // Consume the long click event
                        }
                    });

                    textView.setTextSize(16);
                    // Add TextView to parent layout
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    layoutParams.setMargins(0, 16, 0, 16); // Adjust margins as needed
                    textView.setPadding(20, 10, 20, 10);
                    textView.setLayoutParams(layoutParams);
                    parentLayout.addView(textView);

                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
            }
        });
    }

    private void ticketReceipt(String ticketId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        ImageView imageView = new ImageView(getContext());
        // Generate the QR code bitmap
        Bitmap qrCodeBitmap = generateQRCodeBitmap(ticketId, 512, 512);

        // Set the QR code bitmap to the ImageView
        imageView.setImageBitmap(qrCodeBitmap);
        builder.setView(imageView)
                .setMessage("Ticket ID: " + ticketId)
                .setCancelable(false)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel(); // Dismiss the dialog when Cancel button is clicked
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void makeToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private Bitmap generateQRCodeBitmap(String data, int width, int height) {
        // Create a QRCodeWriter
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        try {
            // Set QR code parameters
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 1);

            // Generate the QR code
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height, hints);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            // Fill the Bitmap with QR code data
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

}