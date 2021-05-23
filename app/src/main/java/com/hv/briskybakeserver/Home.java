package com.hv.briskybakeserver;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hv.briskybakeserver.Common.Common;
import com.hv.briskybakeserver.Interface.ItemClickListener;
import com.hv.briskybakeserver.Model.Category;
import com.hv.briskybakeserver.Model.Token;
import com.hv.briskybakeserver.ViewHolder.MenuViewHolder;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.UUID;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{


    TextView txtFullName;

    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;

    //Add new Menu
    EditText edtName;
    Button btnUpload,btnSelect;

    Category newCategory;

    Uri saveUri;


    DrawerLayout drawer;

    FirebaseDatabase database;
    DatabaseReference category;
    FirebaseStorage storage;
    StorageReference storageReference;

    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Menu management");
        setSupportActionBar(toolbar);

        updateToken(FirebaseMessaging.getInstance().getToken());
        
        //Init Firebase
        database=FirebaseDatabase.getInstance();
        category= database.getReference("Category");
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        //set name for user
        View headerView = navigationView.getHeaderView(0);
        txtFullName = headerView.findViewById(R.id.textFullName);
     //   Common userName= new Common();
        txtFullName.setText(Common.currentUser.getName());

        //Load Menu
        recycler_menu=findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);

        loadMenu();
    }

    private void updateToken(Task<String> token) {
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference tokens=db.getReference("Tokens");
        Token data=new Token(token,true);
        tokens.child(Common.currentUser.getPhone()).setValue(data);
    }

    private void showDialog() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Add new Category");
        alertDialog.setMessage("Enter details :");

        LayoutInflater inflater=this.getLayoutInflater();
        View add_menu_layout=inflater.inflate(R.layout.add_new_menu_layout,null);

        edtName = add_menu_layout.findViewById(R.id.TextName);
        btnSelect = add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload = add_menu_layout.findViewById(R.id.btnUpload);

        //Event for Button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();   //let user select Image from gallery and save Url of the image
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_baseline_shopping_cart_24);

        //set Button
        alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                //new Category
                if (newCategory != null)
                {
                    category.push().setValue(newCategory);
                    Snackbar.make(drawer,"New category "+newCategory.getName()+" was added",Snackbar.LENGTH_SHORT).show();

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
                    Toast.makeText(Home.this, "Uploaded !", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //set value for newCategory if image upload and we can get Download link
                            newCategory = new Category(edtName.getText().toString(),uri.toString());

                        }
                    });
                }
            })
             .addOnFailureListener(new OnFailureListener() {
                 @Override
                 public void onFailure(@NonNull Exception e) {
                     mDialog.dismiss();
                     Toast.makeText(Home.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),Common.PICK_IMAGE_REQUEST);
    }

    private void loadMenu() {
        FirebaseRecyclerOptions<Category> options=new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(category,Category.class)
                .build();
        adapter=new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull Category model) {
                holder.textMenuName.setText(model.getName());
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Get CategoryId and send to New Activity
                                     Intent list=new Intent(Home.this, FoodList.class);
                                          //Because CategoryId is Key, so we take key of the item
                                          list.putExtra("CategoryId",adapter.getRef(position).getKey());
                                          startActivity(list);
                    }
                });
                Picasso.get().load(model.getImage()).into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
         //         final Category clickItem=model;
           //     holder.setItemClickListener(new ItemClickListener() {
                //    @Override
               //     public void onClick(View view, int position, boolean isLongClick) {
//
            //        }
             //   });
            }

            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item,parent,false);
                return new MenuViewHolder(view);
            }
        };

        GridLayoutManager gridLayoutManager=new GridLayoutManager(getApplicationContext(),1);
        recycler_menu.setLayoutManager(gridLayoutManager);
        adapter.startListening();
        recycler_menu.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.nav_orders){
            Intent orderIntent=new Intent(Home.this,OrderStatus.class);
            startActivity(orderIntent);
        }
        DrawerLayout drawer=findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Update/Delete
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE))
        {
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else if (item.getTitle().equals(Common.DELETE))
        {
            deleteCategory(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }

    private void deleteCategory(String key) {
        
        DatabaseReference foods=database.getReference("Foods");
        Query foodInCategory=foods.orderByChild("menuId").equalTo(key);
        foodInCategory.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot:snapshot.getChildren())
                {
                    postSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        
        category.child(key).removeValue();
        Toast.makeText(this, "Item Deleted Successfully! ", Toast.LENGTH_SHORT).show();
    }

    private void showUpdateDialog(String key, Category item) {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Update Category");
        alertDialog.setMessage("Enter details :");

        LayoutInflater inflater=this.getLayoutInflater();
        View add_menu_layout=inflater.inflate(R.layout.add_new_menu_layout,null);

        edtName = add_menu_layout.findViewById(R.id.TextName);
        btnSelect = add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload = add_menu_layout.findViewById(R.id.btnUpload);

        //set Default name
        edtName.setText(item.getName());

        //Event for Button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();  //let user select Image from gallery and save Url of the image
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

                //Update info
                item.setName(edtName.getText().toString());
                category.child(key).setValue(item);

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

    private void changeImage(final Category item) {
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
                    Toast.makeText(Home.this, "Uploaded!!", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(Home.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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