package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_MOVIE = 1; // Unique request code for picking a movie

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private List<Movie> movieList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        // Firebase and UI initialization
        mAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // No user is signed in, redirect to login activity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // RecyclerView setup
        recyclerView = findViewById(R.id.recyclerViewAnime);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new MovieAdapter(movieList);
        recyclerView.setAdapter(adapter);

        // Fetch movies from Firebase
        fetchMovies();

        // FAB setup for picking a movie
        binding.appBarMain.fab.setOnClickListener(view -> selectMovie());

        // Navigation setup

        DrawerLayout drawer = binding.drawerLayout;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, binding.appBarMain.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Handle logout action
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
            return true;
        });
    }

    private void selectMovie() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_MOVIE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_MOVIE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri movieUri = data.getData();
            uploadMovieToFirebase(movieUri);
        }
    }

    private void uploadMovieToFirebase(Uri movieUri) {
        StorageReference movieRef = storageReference.child("movies/" + movieUri.getLastPathSegment());
        movieRef.putFile(movieUri).addOnSuccessListener(taskSnapshot -> movieRef.getDownloadUrl().addOnSuccessListener(this::saveMovieInfoToDatabase))
                .addOnFailureListener(e -> Log.e("UploadMovie", "Error uploading movie", e));
    }

    private void saveMovieInfoToDatabase(Uri downloadUrl) {
        // Lấy tham chiếu đến nút 'movies' trong Realtime Database
        DatabaseReference moviesRef = FirebaseDatabase.getInstance().getReference("movies");

        // Tạo một ID mới cho phim
        String movieId = moviesRef.push().getKey();

        if (movieId != null) {
            // Nhận thông tin phim từ người dùng hoặc thông qua một quá trình nào đó
            String movieTitle = "New Movie Title"; // Tiêu đề phim
            String imageUrl = "Image URL"; // URL của ảnh bìa phim

            // Tạo đối tượng Movie mới
            Movie movie = new Movie(movieTitle, imageUrl, downloadUrl.toString());

            // Lưu trữ thông tin phim vào Database dưới ID mới tạo
            moviesRef.child(movieId).setValue(movie);
        }
    }


    private void fetchMovies() {
        DatabaseReference moviesRef = FirebaseDatabase.getInstance().getReference("movies");
        moviesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                movieList.clear();
                for (DataSnapshot movieSnapshot : dataSnapshot.getChildren()) {
                    Movie movie = movieSnapshot.getValue(Movie.class);
                    movieList.add(movie);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MainActivity", "Failed to read movies", databaseError.toException());
            }
        });
    }

    // Other overridden methods like onCreateOptionsMenu and onSupportNavigateUp
}
