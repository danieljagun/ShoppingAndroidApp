package com.example.shoppingapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ShoppingActivity extends AppCompatActivity {

    private EditText productName, quantity, unitPrice;
    private Button addButton, viewButton, removeButton;
    private ArrayList<String> shoppingList;
    private ArrayList<String> productNamesList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);

        productName = findViewById(R.id.product_name);
        quantity = findViewById(R.id.item_quantity);
        unitPrice = findViewById(R.id.unit_price);
        addButton = findViewById(R.id.add_button);
        viewButton = findViewById(R.id.view_items);
        removeButton = findViewById(R.id.remove_button);
        shoppingList = new ArrayList<>();
        productNamesList = new ArrayList<>();

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

                        if (productNamesList.contains(product)) {
                            Toast.makeText(ShoppingActivity.this, "Item is already in Shopping Cart", Toast.LENGTH_SHORT).show();
                        } else {
                            shoppingList.add("Item: " + product + ", Quantity: " + parsedQuantity + " and Price: €" + parsedPrice);
                            productNamesList.add(product);

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
                if (shoppingList.isEmpty()) {
                    Toast.makeText(ShoppingActivity.this, "Shopping cart is empty", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShoppingActivity.this);
                    builder.setTitle("Shopping Cart");
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(ShoppingActivity.this, android.R.layout.simple_list_item_1, shoppingList);
                    builder.setAdapter(adapter, null);
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