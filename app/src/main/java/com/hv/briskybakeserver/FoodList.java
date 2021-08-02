package com.hv.briskybakeserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hv.briskybakeserver.Common.Common;
import com.hv.briskybakeserver.Interface.ItemClickListener;
import com.hv.briskybakeserver.Model.Food;
import com.hv.briskybakeserver.ViewHolder.FoodViewHolder;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    CoordinatorLayout rootLayout;

    FloatingActionButton fab;

    //Firebase
    FirebaseDatabase db;
    DatabaseReference foodList;
    FirebaseStorage storage;
    StorageReference storageReference;

    String categoryId="";

    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    //Add new food
    EditText edtName,edtDescription,edtPrice,edtDiscount,edtFoodValue,edtUnit,edtUnitUpdate;
    Button unitAdd,unitEdit,unitUpdate,unitDelete;
    MaterialSpinner spinnerUnit;
    Button btnSelect,btnUpload,bAdd;
    Food newFood;
    Uri saveUri;
    List<String> arrUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        //Firebase
        db = FirebaseDatabase.getInstance();
        foodList = db.getReference("Food");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        arrUnit=new ArrayList<>();

        //Init
        recyclerView =findViewById(R.id.recycler_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        rootLayout =findViewById(R.id.rootLayout);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddFoodDialog();
            }
        });


        if(getIntent()!=null) {
            categoryId = getIntent().getStringExtra("CategoryId");
        }
        if (!categoryId.isEmpty())
            loadListFood(categoryId);
    }

    private void showAddFoodDialog() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(FoodList.this);
        alertDialog.setTitle("Add Item");
        alertDialog.setMessage("Enter details :");

        LayoutInflater inflater=this.getLayoutInflater();
        View add_menu_layout=inflater.inflate(R.layout.add_new_food_layout,null);

        edtName = add_menu_layout.findViewById(R.id.TextName);
        edtDescription = add_menu_layout.findViewById(R.id.Description);
        edtPrice = add_menu_layout.findViewById(R.id.Price);
        edtFoodValue=add_menu_layout.findViewById(R.id.FoodValue);
        edtDiscount = add_menu_layout.findViewById(R.id.Discount);
        edtUnit=add_menu_layout.findViewById(R.id.Unit_element);
        btnSelect = add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload = add_menu_layout.findViewById(R.id.btnUpload);
        bAdd=add_menu_layout.findViewById(R.id.btnAdd);

        //Event for Button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();//let user select Image from gallery and save Url of the image
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        //=new List<String>();

        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(edtUnit.getText().toString().isEmpty())&&!(arrUnit.contains(edtUnit.getText().toString())))
                {
                    arrUnit.add(edtUnit.getText().toString());
                    Toast.makeText(FoodList.this, "Unit Added!!", Toast.LENGTH_SHORT).show();
                }
                if ((edtUnit.getText().toString().isEmpty())&&arrUnit.isEmpty())
                {
                    Toast.makeText(FoodList.this, "No unit added. Please enter a unit", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_baseline_shopping_cart_24);

        //set Button
        alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

               // dialog.dismiss();

                //new Category
                if ( newFood != null&& newFood.getUnit()!=null)
                {
                    dialog.dismiss();
                    foodList.push().setValue(newFood);
                    Snackbar.make(rootLayout,newFood.getName()+" was added",Snackbar.LENGTH_SHORT).show();

                }
                else if (newFood!=null && newFood.getUnit()==null)
                {
                    Toast.makeText(FoodList.this, "No unit added. Add a unit", Toast.LENGTH_SHORT).show();
                }

            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }
        });
        alertDialog.show();
    }

    private void uploadImage() {
        if (saveUri != null)
        {
            ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            StorageReference imageFolder = storageReference.child("image/*"+imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDialog.dismiss();
                    Toast.makeText(FoodList.this, "Uploaded !", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //set value for newCategory if image upload and we can get Download link
                            newFood = new Food();
                            newFood.setName(edtName.getText().toString());
                            newFood.setDescription(edtDescription.getText().toString());
                            newFood.setPrice(edtPrice.getText().toString());
                            newFood.setDiscount(edtDiscount.getText().toString());
                            newFood.setMenuValue(edtFoodValue.getText().toString());
                            newFood.setMenuId(categoryId);
                            newFood.setImage(uri.toString());
                            newFood.setUnit(arrUnit);

                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(FoodList.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            int progress = (int) (100.0 *snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded "+progress+"%");

                        }
                    });

        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), Common.PICK_IMAGE_REQUEST);
    }

    private void loadListFood(String categoryId) {
        FirebaseRecyclerOptions<Food> options=new FirebaseRecyclerOptions.Builder<Food>().
                setQuery(foodList.orderByChild("menuId").equalTo(categoryId),Food.class).
                build();
        adapter=new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull Food model) {
                holder.food_name.setText(model.getName());
          //      holder.textItemPrice.setText(String.format("₹ %s",model.getPrice()));
                Picasso.get().load(model.getImage()).into(holder.food_image, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

                final Food local=model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Start new Activity
                        Intent foodDetail=new Intent(FoodList.this,FoodDetails.class);
                       foodDetail.putExtra("FoodId",adapter.getRef(position).getKey()); //Send food id to new activity
                        startActivity(foodDetail);
                    }
                });
            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item,parent,false);
                return new FoodViewHolder(view);
            }
        };

        GridLayoutManager gridLayoutManager=new GridLayoutManager(getApplicationContext(),1);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter.startListening();
        recyclerView.setAdapter(adapter);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK &&
                data != null && data.getData() != null)
        {
            saveUri = data.getData();
            btnSelect.setText("Image Selected");
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE_FOOD))
        {
            showUpdateFoodDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else if (item.getTitle().equals(Common.DELETE))
        {
            deleteFood(adapter.getRef(item.getOrder()).getKey());
        }
        else if (item.getTitle().equals(Common.UPDATE_UNIT))
        {
            showUpdateUnitDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        return super.onContextItemSelected(item);
    }

    private void showUpdateUnitDialog(String key, Food item) {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(FoodList.this);
        alertDialog.setTitle("Edit Unit");
     //   alertDialog.setMessage("Enter details :");

        LayoutInflater inflater=this.getLayoutInflater();
        View edit_unit_layout=inflater.inflate(R.layout.update_unit_layout,null);

        edtUnit=edit_unit_layout.findViewById(R.id.Unit_element);
        edtUnitUpdate=edit_unit_layout.findViewById(R.id.Unit_element_update);
        unitAdd=edit_unit_layout.findViewById(R.id.btnAddUnit);
        unitEdit=edit_unit_layout.findViewById(R.id.btnEditUnit);
        unitUpdate=edit_unit_layout.findViewById(R.id.btnUpdateUnit);
        unitDelete=edit_unit_layout.findViewById(R.id.btnDeleteUnit);
        spinnerUnit=edit_unit_layout.findViewById(R.id.unitSpinner);

      //  List<String> list=item.getUnit();
    //    ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item,item.getUnit());
     //   arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    //    spinnerUnit.setAdapter(arrayAdapter);
        spinnerUnit.setItems(item.getUnit());
        alertDialog.setView(edit_unit_layout);

        unitAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(edtUnit.getText().toString().isEmpty()))
                {
                    item.getUnit().add(edtUnit.getText().toString());
                    Toast.makeText(FoodList.this, "Unit Added!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        unitDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.getUnit().remove(spinnerUnit.getSelectedIndex());
                Toast.makeText(FoodList.this, "Unit removed!!", Toast.LENGTH_SHORT).show();
            }
        });

        unitEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtUnitUpdate.setVisibility(View.VISIBLE);
                unitUpdate.setVisibility(View.VISIBLE);
            }
        });

        unitUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(edtUnitUpdate.getText().toString().isEmpty()))
                {
                    item.getUnit().set(spinnerUnit.getSelectedIndex(), edtUnitUpdate.getText().toString());
                    Toast.makeText(FoodList.this, "Unit Updated!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                foodList.child(key).setValue(item);
                Snackbar.make(rootLayout,item.getName()+" was updated",Snackbar.LENGTH_SHORT).show();
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();

    }

    private void deleteFood(String key) {
        foodList.child(key).removeValue();
    }

    private void showUpdateFoodDialog(String key, Food item) {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(FoodList.this);
        alertDialog.setTitle("Edit Food Item");
        alertDialog.setMessage("Enter details :");

        LayoutInflater inflater=this.getLayoutInflater();
        View add_menu_layout=inflater.inflate(R.layout.update_food_layout,null);

        edtName = add_menu_layout.findViewById(R.id.TextName);
        edtDescription = add_menu_layout.findViewById(R.id.Description);
        edtPrice = add_menu_layout.findViewById(R.id.Price);
        edtFoodValue=add_menu_layout.findViewById(R.id.FoodValue);
        edtDiscount = add_menu_layout.findViewById(R.id.Discount);

        //Set default value
        edtName.setText(item.getName());
        edtPrice.setText(item.getPrice());
        edtDiscount.setText(item.getDiscount());
        edtFoodValue.setText(item.getMenuValue());
        edtDescription.setText(item.getDescription());

        btnSelect = add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload = add_menu_layout.findViewById(R.id.btnUpload);

        //Event for Button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();//let user select Image from gallery and save Url of the image
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImage(item);
            }
        });

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_baseline_shopping_cart_24);

        //set Button
        alertDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                    item.setName(edtName.getText().toString());
                    item.setPrice(edtPrice.getText().toString());
                    item.setDiscount(edtDiscount.getText().toString());
                    item.setMenuValue(edtFoodValue.getText().toString());
                    item.setDescription(edtDescription.getText().toString());

                    foodList.child(key).setValue(item);
                    Snackbar.make(rootLayout,item.getName()+" was updated",Snackbar.LENGTH_SHORT).show();

            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void changeImage(final Food item) {
        if (saveUri != null)
        {
            ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            StorageReference imageFolder = storageReference.child("image/"+imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDialog.dismiss();
                    Toast.makeText(FoodList.this, "Uploaded!!", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //set value for newCategory if image upload and we can get Download link
                            item.setImage(uri.toString());

                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(FoodList.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            int progress = (int) (100.0 *snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded "+progress+"%");

                        }
                    });

        }
    }

}