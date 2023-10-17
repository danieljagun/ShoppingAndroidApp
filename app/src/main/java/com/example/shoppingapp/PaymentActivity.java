package com.example.shoppingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PaymentActivity extends AppCompatActivity {

    private TextView totalValueT;
    private TextView totalQuantityT;
    private EditText cardNo, expDate;
    private Button payButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        totalValueT = findViewById(R.id.total_value);
        totalQuantityT = findViewById(R.id.total_quantity);
        cardNo = findViewById(R.id.payment_number);
        expDate = findViewById(R.id.payment_date);
        payButton = findViewById(R.id.payment_button);

        FirebaseDatabase.getInstance().getReference("shoppingList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double totalValue = 0;
                int totalQuantity = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String item = snapshot.getValue(String.class);
                    if (item != null) {
                        String[] parts = item.split(", ");
                        for (String part : parts) {
                            if (part.startsWith("Price: €")) {
                                try {
                                    String priceString = part.substring(8);
                                    totalValue += Double.parseDouble(priceString);
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            } else if (part.contains("Quantity: ")) {
                                try {
                                    String quantityString = part.replaceAll("[^0-9]", "");
                                    totalQuantity += Integer.parseInt(quantityString);
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }

                totalValueT.setText("Total value of items: €" + totalValue);
                totalQuantityT.setText("Total quantity of items: " + totalQuantity);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PaymentActivity.this, "Failed to retrieve shopping list", Toast.LENGTH_SHORT).show();
            }
        });

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String card = cardNo.getText().toString().trim();
                String expiry = expDate.getText().toString().trim();

                if (card.isEmpty()) {
                    cardNo.setError("Card Number cannot be empty");
                } else if (!isCardValid(card)) {
                    cardNo.setError("Invalid card");
                } else if (expiry.isEmpty()) {
                    expDate.setError("Expiry Date cannot be empty");
                } else if (!isDateValid(expiry)) {
                    expDate.setError("Invalid Expiry Date");
                } else {

                    if (isCardValid(card) && isDateValid(expiry)) {

                        FirebaseDatabase.getInstance().getReference("shoppingList").removeValue();

                        Toast.makeText(PaymentActivity.this, "Payment successful. Going back to Profile Screen", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private boolean isCardValid(String card) {
        return card.length() == 16 && card.matches("\\d+");
    }

    private boolean isDateValid(String date) {
        return true;
    }
}