package com.hv.briskybakeserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hv.briskybakeserver.Common.Common;
import com.hv.briskybakeserver.Model.Food;
import com.hv.briskybakeserver.Model.Rating;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FoodDetails extends AppCompatActivity {


    TextView food_price, food_description,food_dis,discount_txt,food_val;
    ImageView foodimage;
    CollapsingToolbarLayout collapsingToolbarLayout;
    RatingBar ratingBar;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    String foodId = "";

    FirebaseDatabase database;
    DatabaseReference foods;
    DatabaseReference ratingTbl;

    String foodUnit;

    Food currentFood;
    Spinner spinnerUnit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);

        //Firebase
        database = FirebaseDatabase.getInstance();
        foods = database.getReference("Food");
        ratingTbl = database.getReference("Rating");

        //Init view
        spinnerUnit=findViewById(R.id.unitSpinner);
        ratingBar = findViewById(R.id.ratingBar);
        ;

        food_description = findViewById(R.id.food_description);
        food_price = findViewById(R.id.food_price);
        food_dis=findViewById(R.id.food_discount);
        food_val=findViewById(R.id.food_value);
        discount_txt=findViewById(R.id.food_dis);

        foodimage = findViewById(R.id.img_food);

        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);


        //Get foodId from intent
        if (getIntent() != null)
            foodId = getIntent().getStringExtra("FoodId");
        if (!foodId.isEmpty()) {
                getDetailFood(foodId);
                getRatingFood(foodId);

        }



    }

    private void getRatingFood(String foodId) {
        Query foodRating = ratingTbl.orderByChild("foodId").equalTo(foodId);

        foodRating.addValueEventListener(new ValueEventListener() {
            int count = 0, sum = 0;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Rating item = postSnapshot.getValue(Rating.class);
                    sum += Integer.parseInt(item.getRateValue());
                    count++;
                }
                if (count != 0) {
                    float average = sum / count;
                    ratingBar.setRating(average);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getDetailFood(String foodId) {
        foods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentFood = snapshot.getValue(Food.class);

                Picasso.get().load(currentFood.getImage()).into(foodimage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

                collapsingToolbarLayout.setTitle(currentFood.getName());
                if (currentFood.getDiscount()==null|| currentFood.getDiscount().equals("0")) {
                    food_dis.setVisibility(View.GONE);
                    discount_txt.setVisibility(View.GONE);

                }
                else {
                    food_dis.setText(String.format("%s ", currentFood.getDiscount(),"% OFF"));
                    food_dis.setVisibility(View.VISIBLE);
                    discount_txt.setVisibility(View.VISIBLE);
                }
                food_val.setText(currentFood.getMenuValue());
                food_description.setText(currentFood.getDescription());
                List<String> food_Unit=new ArrayList<String>(currentFood.getUnit());
                ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_spinner_dropdown_item,food_Unit);
                //  spinnerUnit.se(food_Unit);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerUnit.setAdapter(arrayAdapter);
                foodUnit=currentFood.getUnit().get(0);
                spinnerUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        foodUnit=spinnerUnit.getSelectedItem().toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        //.toString();
                        //  foodUnit=parent.getItemAtPosition(0).toString();
                        //   foodUnit=arrayAdapter.getItem(0);
                    }
                });

                // Double actualPrice=Double.parseDouble(currentFood.getPrice())*Double.parseDouble(foodUnit);
                food_price.setText(String.format("₹ %s of 1 %s",currentFood.getPrice(),currentFood.getMenuValue()));


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}