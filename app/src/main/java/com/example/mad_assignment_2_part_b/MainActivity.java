package com.example.mad_assignment_2_part_b;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mad_assignment_2_part_b.fragments.SingleColumnDisplayFragment;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity
{
    private EditText searchEditText;
    private Button searchButton;
    private ProgressBar progressBar;
    private List<Bitmap> imageList;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        imageList = new ArrayList<>();
        storageReference = FirebaseStorage.getInstance().getReference();

        searchButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                searchImage();
            }
        });
    }

    public void searchImage()
    {
        Toast.makeText(MainActivity.this, "Searching Starts", Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.VISIBLE);
        SearchTask searchTask = new SearchTask(MainActivity.this);
        searchTask.setSearchKey(searchEditText.getText().toString());
        Single<String> searchObservable = Single.fromCallable(searchTask);
        searchObservable = searchObservable.subscribeOn(Schedulers.io());
        searchObservable = searchObservable.observeOn(AndroidSchedulers.mainThread());

        searchObservable.subscribe(new SingleObserver<String>()
        {
            @Override
            public void onSubscribe(@NonNull Disposable d)
            {
            }

            @Override
            public void onSuccess(@NonNull String s)
            {
                Toast.makeText(MainActivity.this, "Searching Ends", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);

                try
                {
                    JSONObject jBase = new JSONObject(s);
                    JSONArray jHits = jBase.getJSONArray("hits");

                    if(jHits.length() > 15)
                    {
                        for(int i = 0; i < 15; i++)
                        {
                            loadImage(s, i, 15);
                        }
                    }
                    else
                    {
                        for(int i = 0; i < jHits.length(); i++)
                        {
                            loadImage(s, i, jHits.length());
                        }
                    }
                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(@NonNull Throwable e)
            {
                Toast.makeText(MainActivity.this, "Searching Error", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);

            }
        });
    }

    public void loadImage(String response, int index, int imageCount)
    {
        ImageRetrievalTask imageRetrievalTask = new ImageRetrievalTask(MainActivity.this, index);
        imageRetrievalTask.setData(response);

        Toast.makeText(MainActivity.this, "Image loading starts", Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.VISIBLE);
        Single<Bitmap> searchObservable = Single.fromCallable(imageRetrievalTask);
        searchObservable = searchObservable.subscribeOn(Schedulers.io());
        searchObservable = searchObservable.observeOn(AndroidSchedulers.mainThread());

        searchObservable.subscribe(new SingleObserver<Bitmap>()
        {
            @Override
            public void onSubscribe(@NonNull Disposable d)
            {
            }

            @Override
            public void onSuccess(@NonNull Bitmap bitmap)
            {
                Toast.makeText(MainActivity.this, "Image loading Ends", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                imageList.add(bitmap);

                if(imageList.size() == imageCount)
                {
                    searchEditText.setVisibility(View.INVISIBLE);
                    searchButton.setVisibility(View.INVISIBLE);

                    FragmentManager fm = getSupportFragmentManager();
                    SingleColumnDisplayFragment singleColumnDisplayFragment = (SingleColumnDisplayFragment) fm.findFragmentById(R.id.fragment_container);

                    if(singleColumnDisplayFragment == null)
                    {
                        singleColumnDisplayFragment = new SingleColumnDisplayFragment(searchEditText.getText().toString(), imageList, storageReference);
                        fm.beginTransaction().add(R.id.fragment_container, singleColumnDisplayFragment).commit();
                    }
                }
            }

            @Override
            public void onError(@NonNull Throwable e)
            {
                Toast.makeText(MainActivity.this, "Image loading error, search again", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}