package com.example.shoppingapp;

import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShoppingActivity extends AppCompatActivity {

    private EditText productName, quantity, unitPrice;
    private Button addButton, viewButton, removeButton;
    private ArrayList<String> shoppingList;
    private ArrayList<String> productNamesList;
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private double totalValue = 0.0;
    private int totalQuantity = 0;
    private TextView totalTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        productName = findViewById(R.id.product_name);
        quantity = findViewById(R.id.item_quantity);
        unitPrice = findViewById(R.id.unit_price);
        addButton = findViewById(R.id.add_button);
        viewButton = findViewById(R.id.view_items);
        shoppingList = new ArrayList<>();
        productNamesList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String product = productName.getText().toString().trim();
                String quan = quantity.getText().toString();
                String price = unitPrice.getText().toString();


                if (product.isEmpty()) {
                    productName.setError("Product name cannot be empty");
                } else if (quan.isEmpty()) {
                    quantity.setError("Quantity cannot be empty");
                } else if (price.isEmpty()) {
                    unitPrice.setError("Unit price cannot be empty");
                } else {
                    try {

                        int parsedQuantity = Integer.parseInt(quan);
                        double parsedPrice = Double.parseDouble(price);

                        totalValue += parsedQuantity * parsedPrice;
                        totalQuantity += parsedQuantity;

                        if (productNamesList.contains(product)) {
                            Toast.makeText(ShoppingActivity.this, "Item is already in Shopping Cart", Toast.LENGTH_SHORT).show();
                        } else {
                            String item = "Item: " + product + ", Quantity: " + parsedQuantity + " and Price: €" + parsedPrice;
                            shoppingList.add(item);
                            productNamesList.add(product);

                            mDatabase.child("shoppingList").push().setValue(item);

                            productName.getText().clear();
                            quantity.getText().clear();
                            unitPrice.getText().clear();

                            Toast.makeText(ShoppingActivity.this, "Item added to Shopping Cart", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        // Handle NumberFormatException for invalid format input
                        Toast.makeText(ShoppingActivity.this, "Invalid quantity or price format, try again", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference shoppingListFB = mDatabase.child("shoppingList");
                shoppingListFB.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<String> firebaseShoppingList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String item = snapshot.getValue(String.class);
                            firebaseShoppingList.add(item);
                        }
                        ShoppingListDialog(firebaseShoppingList);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle the error
                        Toast.makeText(ShoppingActivity.this, "Failed to retrieve shopping list", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            private void ShoppingListDialog(ArrayList<String> shoppingList) {
                if (shoppingList.isEmpty()) {
                    Toast.makeText(ShoppingActivity.this, "Shopping cart is empty", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShoppingActivity.this);
                    builder.setTitle("Shopping Cart");
                    final String[] shoppingItems = shoppingList.toArray(new String[0]);

                    builder.setItems(shoppingItems, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String selectedItem = shoppingItems[which];
                            DatabaseReference shoppingListFB = mDatabase.child("shoppingList");
                            shoppingListFB.orderByValue().equalTo(selectedItem).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        dataSnapshot.getRef().removeValue();
                                        Toast.makeText(ShoppingActivity.this, "Item removed from Shopping Cart", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(ShoppingActivity.this, "Failed to remove item from Shopping Cart", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

    }
}