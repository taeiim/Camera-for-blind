package com.example.parktaeim.cameraforblind.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.example.parktaeim.cameraforblind.Adapter.SettingsRecyclerViewAdapter;
import com.example.parktaeim.cameraforblind.Model.SettingsItem;
import com.example.parktaeim.cameraforblind.R;
import com.example.parktaeim.cameraforblind.RecyclerItemClickListener;

import java.util.ArrayList;

/**
 * Created by parktaeim on 2017. 10. 14..
 */

public class SettingsActivity extends AppCompatActivity {
    Context context;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ImageView back_icon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setUpRecyclerView();

        //뒤로가기
        back_icon = (ImageView) findViewById(R.id.back_icon);
        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == 0) {
                    Intent intent = new Intent(SettingsActivity.this, ImageRecognitionActivity.class);
                    startActivity(intent);
                }
//                }else if(position == 1){
//
//                }
            }
        }));
//        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//
//            }
//
//        }));


    }

    private void setUpRecyclerView() {
        context = getApplicationContext();
        recyclerView = (RecyclerView) findViewById(R.id.settingsRecyclerView);
        layoutManager = new LinearLayoutManager(this);

        ArrayList<SettingsItem> items= new ArrayList<>();
        items.add(new SettingsItem(R.drawable.icon_photo,"갤러리 확인"));
        items.add(new SettingsItem(R.drawable.icon_record_voice,"사진 촬영 음성"));

        recyclerView.setHasFixedSize(true);
        adapter = new SettingsRecyclerViewAdapter(items,context);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        //구분선
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(getApplicationContext().getResources().getDrawable(R.drawable.recycler_divider));
        recyclerView.addItemDecoration(dividerItemDecoration);
    }
}
