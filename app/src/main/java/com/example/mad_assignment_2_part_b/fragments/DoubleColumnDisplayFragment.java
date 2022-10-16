package com.example.mad_assignment_2_part_b.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.mad_assignment_2_part_b.R;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DoubleColumnDisplayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DoubleColumnDisplayFragment extends Fragment
{
    private List<Bitmap> imageList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DoubleColumnDisplayFragment()
    {
        // Required empty public constructor
    }

    public DoubleColumnDisplayFragment(List<Bitmap> imageList)
    {
        this.imageList = imageList;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DoubleColumnDisplayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DoubleColumnDisplayFragment newInstance(String param1, String param2)
    {
        DoubleColumnDisplayFragment fragment = new DoubleColumnDisplayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_double_column_display, container, false);
        Button singleColumnButton = view.findViewById(R.id.singleColumnButton);
        Button homeButton = view.findViewById(R.id.homeButton);
        RecyclerView rv = view.findViewById(R.id.doubleRecyclerView);
        rv.setLayoutManager(new GridLayoutManager(getContext(), 2));
        Adapter adapter = new Adapter(imageList);
        rv.setAdapter(adapter);

        singleColumnButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                SingleColumnDisplayFragment singleColumnDisplayFragment = new SingleColumnDisplayFragment(imageList);
                fm.beginTransaction().replace(R.id.fragment_container, singleColumnDisplayFragment).commit();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                getActivity().getSupportFragmentManager().beginTransaction().remove(DoubleColumnDisplayFragment.this).commit(); // Removing fragment
                getActivity().recreate(); // Recreating MainActivity
            }
        });

        return view;
    }

    private class ViewHolder extends RecyclerView.ViewHolder // ViewHolder inner class
    {
        public ImageView image;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            image = itemView.findViewById(R.id.doubleImageView);
        }
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> // Adapter inner class
    {
        List<Bitmap> imageData;

        public Adapter(List<Bitmap> imageData)
        {
            this.imageData = imageData;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.each_image_double_view, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position)
        {
            Bitmap bitmap = imageData.get(position);
            holder.image.setImageBitmap(bitmap);
        }

        @Override
        public int getItemCount()
        {
            return imageData.size();
        }
    }
}