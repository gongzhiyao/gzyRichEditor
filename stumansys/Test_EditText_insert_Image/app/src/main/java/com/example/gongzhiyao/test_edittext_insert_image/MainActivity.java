package com.example.gongzhiyao.test_edittext_insert_image;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static android.text.Html.toHtml;

public class MainActivity extends Activity implements View.OnClickListener {
    /**
     * Called when the activity is first created.
     */
    Button b;
    Myedittext e;
    private Button mb_save;
    private Button mb_bond, mb_underLine, mb_lighter, mb_italic;
    private static final int PHOTO_SUCCESS = 1;
    private static final int CAMERA_SUCCESS = 2;
    private static final String TAG = "Main";
    String CurrentPath = "";
    String text = "";
    HashMap<String, Integer> key = new HashMap<String, Integer>();
    StringBuffer stringBuffer = new StringBuffer();
    boolean is_image = false;
    private List<Integer> local_point = new ArrayList<Integer>();
    private List<String> uriList = new ArrayList<String>();
    private static int screenWidth;
    private static int screenHeigh;
    Editable s_toHtml;
    private int Start1;
    private int Count1;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
        bindListener();
        DisplayMetrics dm = new DisplayMetrics();
        //获取屏幕信息
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeigh = dm.heightPixels;


        mb_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ss = toHtml(s_toHtml);
                StringBuffer sb = new StringBuffer(ss);
//                Log.i(TAG, "得到的未转换前的是" + s_toHtml);
//                Log.i(TAG, "此时list中记录了几个" + local_point.size());
//                Log.i(TAG, "第一种方案此时获得的html文本是" + ss);
                //这里需要注意如果有图片的话
                int Start = 0;
                for (int i = 0; i < local_point.size(); i++) {//插入的图片数

                    if (i == 0) {
                        Start = local_point.get(0);
                    } else {
                        Start = local_point.get(i);
//                        Log.i(TAG, "start:" + Start);
                    }

                    int a = sb.indexOf("null", Start);
//                    Log.i(TAG, "这里得到了null的位置" + a);
                    sb.replace(a, a + 4, uriList.get(i));
//                    Log.i(TAG, "此时的sb是" + sb.toString());
//                        Log.i(TAG,uriList.get(i));
                    //start 应该加上上一个uri的长度-null的长度
                }
                String html = sb.toString();


                /**
                *
                写下内部存储比较好，舍弃外部存储方案,这里是把文件存到了app中
                */
//                File html_put=new File(getMediaDir(),System.currentTimeMillis()+".txt");
//                if(!html_put.exists()){
//                    try {
//                        html_put.createNewFile();
//                    } catch (IOException e1) {
//                        e1.printStackTrace();
//                    }
//                }

                String File_name = System.currentTimeMillis() + ".txt";

                try {
                    FileOutputStream fos = openFileOutput(File_name, MODE_PRIVATE);
                    OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
                    osw.write(html);
                    osw.flush();
                    fos.flush();
                    osw.close();
                    fos.close();
//                    Log.i(TAG, "已经成功写入");
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                Intent intent = new Intent(MainActivity.this, Show_Activity.class);
                intent.putExtra("file_name", File_name);
                startActivity(intent);
            }
        });


        e.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }



            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String s1 = s.toString().substring(start, start + count);
                if (s1.startsWith("<img src")) {
                    Log.i(TAG, "此时输入的是图片");
                    is_image = true;
                    local_point.add(start);
//                    local_point.add(start+count);
                    String test_s = s1.substring(10, s1.length() - 4);
                    Log.i(TAG, "得到的uri是" + test_s);
                    uriList.add(test_s);

//                    Image_length = count;
//                    stringBuffer.append(s1);
                } else {
//                    Log.i(TAG, "此时输入的是文字");
                    if(count>1){
                        for(int i1=start;i1<start+count;i1++){
                            onTextChanged(s,i1,before,1);

                        }
                    }
                }
                Start1=start;
                Count1=count;
                if (before > 0) {//在删除时故意把所有的字体设置都归零
                    is_bold = false;
                    is_lighter = false;
                    is_underLine = false;
                    is_italic = false;
                    for (int i = local_point.size() - 1; i >= 0; i--) {//在这里要确保，照片删除后，节点也被删除，不然在保存时会空指针错误
                        if (start <= local_point.get(i) + 1) { //这里试了下，如果是不加1，那么就得多删除一位才能删除节点
                            local_point.remove(i);
                            uriList.remove(i);
                            Log.i(TAG, "有一个照片节点被删除");
                        }
                    }

                }


//                Log.i(TAG, "此时s的值是" + s);
//                Log.i(TAG, "此时start是" + start);
//                Log.i(TAG, "此时before是" + before);
//                Log.i(TAG, "此时的count是" + count);




            }



            @Override
            public void afterTextChanged(Editable s) {
//                text = s.toString();
                Log.i(TAG, "此时s的值是" + text);
                ////原来是用s.length来约束字体属性，后来发现使用Start和count实时设置更好
                if (is_bold == true) {

                    for(int i=Start1;i<Start1+Count1;i++){
                        s.setSpan(new StyleSpan(Typeface.BOLD), i,i+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }


                    Log.i(TAG, "粗体依然存在");
                }
                if (is_italic == true) {

                    for(int i=Start1;i<Start1+Count1;i++){
                        s.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC),i,i+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    Log.i(TAG, "斜体依然存在");
                }
                if (is_underLine == true) {


                    for(int i=Start1;i<Start1+Count1;i++){
                        s.setSpan(new UnderlineSpan(), i,i+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    Log.i(TAG, "下划线依然存在");
                }
                if (is_lighter == true) {

                    for(int i=Start1;i<Start1+Count1;i++){
                        s.setSpan(new ForegroundColorSpan(Color.GREEN),i,i+1,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }



                    Log.i(TAG, "高亮依然存在");
                }

                s_toHtml = s;


            }

        });


        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final CharSequence[] items = {"手机相册", "相机拍摄"};
                AlertDialog dlg = new AlertDialog.Builder(MainActivity.this).setTitle("选择图片").setItems(items,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                //这里item是根据选择的方式,
                                //在items数组里面定义了两种方式, 拍照的下标为1所以就调用拍照方法
                                if (item == 1) {
                                    Intent getImageByCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                    Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
                                    File file = new File(getMediaDir(), System.currentTimeMillis() + ".jpg");//文件不存在，就创建
                                    if (!file.exists()) {
                                        try {
                                            file.createNewFile();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    CurrentPath = file.getAbsolutePath();//获得绝对路径
                                    getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));//指定输出路径
                                    startActivityForResult(getImageByCamera, CAMERA_SUCCESS);
                                } else {
                                    Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
                                    getImage.addCategory(Intent.CATEGORY_OPENABLE);
                                    getImage.setType("image/*");
                                    startActivityForResult(getImage, PHOTO_SUCCESS);
                                }
                            }
                        }).create();
                dlg.show();
//                e.insertDrawable(R.drawable.easy);
            }
        });
    }


    private void bindListener() {
        mb_underLine.setOnClickListener(this);
        mb_lighter.setOnClickListener(this);
        mb_bond.setOnClickListener(this);
        mb_italic.setOnClickListener(this);
    }

    private void findView() {
        mb_save = (Button) findViewById(R.id.btn_save);
        b = (Button) findViewById(R.id.myButton);
        e = (Myedittext) findViewById(R.id.myEdit);
        mb_bond = (Button) findViewById(R.id.btn_bold);
        mb_italic = (Button) findViewById(R.id.btn_italic);
        mb_lighter = (Button) findViewById(R.id.btn_lighter);
        mb_underLine = (Button) findViewById(R.id.btn_underLine);

    }

    public File getMediaDir() {//在sd卡上创建一个目录
        File dir = new File(Environment.getExternalStorageDirectory(), "TwoNotes_Image");//
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        ContentResolver resolver = getContentResolver();
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PHOTO_SUCCESS:
                    //获得图片的uri
                    Uri originalUri = intent.getData();
                    Bitmap bitmap = null;
                    try {
                        Bitmap originalBitmap = BitmapFactory.decodeStream(resolver.openInputStream(originalUri));
                        bitmap = resizeImage(originalBitmap, 1080, 1800);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (bitmap != null) {
                        //根据Bitmap对象创建ImageSpan对象
                        ImageSpan imageSpan = new ImageSpan(MainActivity.this, bitmap);
                        //创建一个SpannableString对象，以便插入用ImageSpan对象封装的图像
                        String tempUrl = "<img src=\"" + originalUri + "\" />";
                        SpannableString spannableString = new SpannableString(tempUrl);
                        spannableString.setSpan(imageSpan, 0, tempUrl.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
                        //将选择的图片追加到EditText中光标所在位置
                        int index = e.getSelectionStart(); //获取光标所在位置
                        Editable edit_text = e.getEditableText();
                        if (index < 0 || index >= edit_text.length()) {
                            edit_text.append(spannableString);
                            edit_text.insert(index + spannableString.length(), "\n");//添加换行符
                        } else {
                            edit_text.insert(index, spannableString);
                            edit_text.insert(index + spannableString.length(), "\n");
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "获取图片失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CAMERA_SUCCESS:


                    File sd = Environment.getExternalStorageDirectory();
                    boolean can_write = sd.canWrite();

                    Log.i(TAG, "sd卡是否可读" + can_write);
//
                    Log.i(TAG, "currentPath的值是" + CurrentPath);
                    Bitmap originalBitmap1 = BitmapFactory.decodeFile(CurrentPath);
                    if (originalBitmap1 != null) {
                        bitmap = resizeImage(originalBitmap1, 1080, 1800);

                        //根据Bitmap对象创建ImageSpan对象
                        ImageSpan imageSpan = new ImageSpan(MainActivity.this, bitmap);
                        //创建一个SpannableString对象，以便插入用ImageSpan对象封装的图像
                        String tempUrl = "<img src=\"" + Uri.fromFile(new File(CurrentPath)) + "\" />";
                        SpannableString spannableString = new SpannableString(tempUrl);
                        //  用ImageSpan对象替换face

                        spannableString.setSpan(imageSpan, 0, tempUrl.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        //将选择的图片追加到EditText中光标所在位置

                        int index = e.getSelectionStart(); //获取光标所在位置
                        Editable edit_text = e.getEditableText();
                        if (index < 0 || index >= edit_text.length()) {
                            edit_text.append(spannableString);
                            edit_text.insert(index + spannableString.length(), "\n");
                        } else {
                            edit_text.insert(index, spannableString);
                            edit_text.insert(index + spannableString.length(), "\n");
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "获取图片失败", Toast.LENGTH_SHORT).show();
                    }

                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 图片缩放
     *
     * @param originalBitmap 原始的Bitmap
     * @param newWidth       自定义宽度
     * @return 缩放后的Bitmap
     */
/////////////////////应该根据屏幕的分辨率在设置宽窄
    ////////////////能够根据横屏还是竖屏拍摄的照片做出更改刚好
    public static Bitmap resizeImage(Bitmap originalBitmap, int newWidth, int newHeight) {
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();
        Log.i(TAG, "照片源的宽和高分别是" + width + "     " + height);
        Log.i(TAG, "系统的尺寸是" + screenWidth + "   " + screenHeigh);
        float scanleWidth = 0;
        float scanleHeight = 0;
        if (width > height) {
            //横屏的图片
            if (width > screenWidth / 2) {
                scanleWidth = (float) (((float) screenWidth / (float) width) - 0.01);
                scanleHeight = scanleWidth;
            } else {
                scanleWidth = (float) screenWidth / (float) 2 / (float) width;
                scanleHeight = scanleWidth;
            }

        }
        if (width <= height) {//刚开始的时候是使用的int类型的来除，后来发现不精确，所以在这里全都转化成了float
            //竖屏的图片
            if (width >= screenWidth / 2) {
                scanleWidth = (float) (((float) screenWidth / (float) width) - 0.01);
                Log.i(TAG, "缩小比例是多少" + scanleWidth);
                scanleHeight = scanleWidth;
            }



            else {
                scanleWidth = (float) screenWidth / (float) 2 / (float) width;
                scanleHeight = scanleWidth;
            }

        }



        //创建操作图片用的matrix对象 Matrix
        Matrix matrix = new Matrix();
        // 缩放图片动作
        //matrix.postScale(scanleWidth, scanleHeight);
        matrix.postScale(scanleWidth, scanleHeight);


        //旋转图片 动作
//        matrix.postRotate(45);
        // 创建新的图片Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, width, height, matrix, true);
        // 用完了记得回收
//        resizedBitmap.recycle();
        return resizedBitmap;
    }


    private boolean is_bold = false, is_underLine = false, is_italic = false, is_lighter = false;
    private int position_bold = 1;
    private int position_italic = 1;
    private int position_lighter = 1;
    private int position_underLine = 1;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bold:
                if (is_bold == false) {
                    is_bold = true;

                    Log.i(TAG, "已加粗");
                    position_bold = text.length();
                } else if (is_bold == true) {
                    is_bold = false;
                    Log.i(TAG, "已取消加粗");
                }


                break;
            case R.id.btn_italic:
                if (is_italic == false) {
                    is_italic = true;
                    Log.i(TAG, "已设置为斜体");
                    position_italic = text.length();
                    Log.i(TAG, "斜体的开始位置是" + position_italic);
                } else {
                    is_italic = false;
                    Log.i(TAG, "已取消斜体");
                }
                break;
            case R.id.btn_lighter:
                if (is_lighter == false) {
                    is_lighter = true;
                    Log.i(TAG, "已设置高亮");
                    position_lighter = text.length();
                } else {
                    is_lighter = false;
                    Log.i(TAG, "已取消高亮");
                }
                break;
            case R.id.btn_underLine:
                if (is_underLine == false) {
                    is_underLine = true;
                    Log.i(TAG, "已添加下划线");
                    position_underLine = text.length();
                } else {
                    is_underLine = false;
                    Log.i(TAG, "已取消下划线");
                }
                break;
        }
    }
}