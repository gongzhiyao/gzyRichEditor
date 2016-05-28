package com.example.gongzhiyao.test_edittext_insert_image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.text.Html.toHtml;

public class Show_Activity extends AppCompatActivity {

    private EditText met_show;
    private static final String TAG = "Show";
    String html;
    private static int screenWidth;
    private static int screenHeigh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_);
        met_show = (EditText) findViewById(R.id.et_show);

        DisplayMetrics dm = new DisplayMetrics();
        //获取屏幕信息
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        screenWidth = dm.widthPixels;

        screenHeigh = dm.heightPixels;


        String name = getIntent().getStringExtra("file_name");
        Log.i(TAG, "传过来的是" + name);
        try {
            FileInputStream fis = openFileInput(name);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            char[] input = new char[fis.available()];
            isr.read(input);
            isr.close();
            fis.close();
            html = new String(input);
            Log.i(TAG, "获取到的html格式文件是" + html);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        met_show.setText(Html.fromHtml(html, imageGetter, null));



    }


    private Html.ImageGetter imageGetter = new Html.ImageGetter() {
        @Override
        public Drawable getDrawable(String source) {
            Log.i(TAG, "此时得到的source是" + source);
//            int id = Integer.parseInt(source);
//            Drawable d = getResources().getDrawable(id);
            Uri tempPath = Uri.parse(source);
            Drawable d = null;
            try {
//                d = Drawable.createFromStream(getContentResolver().openInputStream(tempPath), null);
                d = Drawable.createFromStream(getContentResolver().openInputStream(tempPath), null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            int width=d.getIntrinsicWidth()*3;
            int height=d.getIntrinsicHeight()*3;
            float scanleWidth = 0,scanleHeight = 0;
            if (width > height) {
                //横屏的图片
                if(width>screenWidth/2){
                    scanleWidth=(float)( ((float)screenWidth/(float)width)-0.01);
                    scanleHeight=scanleWidth;
                }else{
                    scanleWidth=(float)screenWidth/(float)2/(float)width;
                    scanleHeight=scanleWidth;
                }

            }
            if (width <= height) {//刚开始的时候是使用的int类型的来除，后来发现不精确，所以在这里全都转化成了float
                //竖屏的图片
                if (width >= screenWidth / 2) {
                    scanleWidth = (float) (((float) screenWidth / (float) width) - 0.01);
                    Log.i(TAG, "缩小比例是多少" + scanleWidth);
                    scanleHeight = scanleWidth;
                }
//            else if (width >= screenWidth / 2) {
//                scanleWidth=(float)( ((float)screenWidth/(float)width)-0.01);
//                scanleHeight=scanleWidth;
//            }


                else {
                    scanleWidth = (float) screenWidth / (float) 2 / (float) width;
                    scanleHeight = scanleWidth;
                }
            }




            ///这一行设置了显示时，图片的大小
            d.setBounds(0, 0, (int) (width*scanleWidth), (int) (height*scanleHeight));
            Log.i(TAG,"传过来的照片的大小是"+d.getIntrinsicWidth()+"    "+d.getIntrinsicHeight());
            return d;
        }
    };

/**
 * 这里的Textwatcher是用来监测text转化成html变化的，测试用的(●'◡'●)
 *
 *
 */
    private TextWatcher textWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String ss=toHtml(s);
            Log.i(TAG,"EditText文本的内容是"+ss);
        }
    };




























//    class URLImageParser implements Html.ImageGetter {
//        TextView mTextView;
//
//        public URLImageParser(TextView textView) {
//            this.mTextView = textView;
//        }
//
//        public Drawable getDrawable(String source) {
//            final URLDrawable urlDrawable = new URLDrawable();
//            Log.d("ChapterActivity", Consts.BASE_URL + source);
//            ImageLoader.getInstance().loadImage(Consts.BASE_URL + source, new SimpleImageLoadingListener() {
//                @Override
//                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                    urlDrawable.bitmap = loadedImage;
//                    urlDrawable.setBounds(0, 0, loadedImage.getWidth(), loadedImage.getHeight());
//                    met_show.invalidate();
//                    met_show.setText(met_show.getText()); // 解决图文重叠
//                }
//            });
//            return urlDrawable;
//        }
//
//        public class URLDrawable extends BitmapDrawable {
//            protected Bitmap bitmap;
//
//            @Override
//            public void draw(Canvas canvas) {
//                if (bitmap != null) {
//                    canvas.drawBitmap(bitmap, 0, 0, getPaint());
//                }
//            }
//        }
//    }
}
