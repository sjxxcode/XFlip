package com.sj.flip.custom;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sj.flip.R;
import com.sj.flip.custom.view.online.view.FlipboardView;

import java.lang.ref.WeakReference;
import java.util.Random;

/**
 * Created by SJ on 2018/4/25.
 */

public class TextAct extends Activity{

    private FlipboardView mFlipboardView;
    private Button next;
    private Button pre;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.act_custom_layout);

        this.initView();
        this.setListener();

        this.mFlipboardView.setAdapter(new MyAdapter(this));
    }

    private void initView(){
        this.next = (Button) this.findViewById(R.id.next);
        this.pre = (Button) this.findViewById(R.id.pre);

        this.mFlipboardView = (FlipboardView) this.findViewById(R.id.flipboard);
    }

    private void setListener(){
        this.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFlipboardView.endNextByAni(0);
            }
        });

        this.pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFlipboardView.endPreByAni(180);
            }
        });
    }

    private static class MyAdapter extends BaseAdapter{

        private String[] imgs = {"http://a3.topitme.com/1/21/79/1128833621e7779211o.jpg",
                                 "http://image.meifajie.com/pic/45791364863846.jpg",
                                 "http://img.mp.itc.cn/upload/20170709/cb68911f6ccf41f694e293f4a3be691a_th.jpg"};

        private LayoutInflater inflater;

        private WeakReference<Context> context;

        public MyAdapter(Context context) {
            this.context = new WeakReference<Context>(context);
        }

        @Override
        public int getCount() {
            return imgs.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(this.inflater == null){
                Context context = parent.getContext();
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }

            View view = inflater.inflate(R.layout.item_flip_layout, parent, false);
            TextView text1 = (TextView) view.findViewById(R.id.text1);
            TextView text2 = (TextView) view.findViewById(R.id.text2);
            ImageView img = (ImageView) view.findViewById(R.id.img);

            final String str = "Text:" + (position + 1) + "" + (position + 1);
            final String url = imgs[position];
            text1.setText(str);
            text2.setText(str);

            Glide.with(context.get())
                    .load(url)
                    .into(img);

            view.setBackgroundColor(this.getRandomColor());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(context != null && context.get() != null){
                        Toast.makeText(context.get(), str, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            return view;
        }

        private int getRandomColor(){
            int red = new Random().nextInt(0xff);
            int green = new Random().nextInt(0xff);
            int blue = new Random().nextInt(0xff);

            return Color.rgb(red, green, blue);
        }
    }
}
