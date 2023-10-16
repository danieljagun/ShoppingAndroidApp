package com.example.shoppingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private Button logoutButton;
    private TextView userEmailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        logoutButton = findViewById(R.id.logout_button);
        userEmailTextView = findViewById(R.id.user_email_text_view);

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String userEmail = ((FirebaseUser) user).getEmail();
            userEmailTextView.setText(userEmail);
        }
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        int itemId = item.getItemId();

        if (itemId == R.id.action_purchase) {
            Toast.makeText(MainActivity.this, "Loading Purchase Screen", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MainActivity.this, ShoppingActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_cart) {
            Toast.makeText(MainActivity.this, "Select Cart", Toast.LENGTH_LONG).show();
            return true;
        } else if (itemId == R.id.action_update) {
            Toast.makeText(MainActivity.this, "Select Update", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }
}