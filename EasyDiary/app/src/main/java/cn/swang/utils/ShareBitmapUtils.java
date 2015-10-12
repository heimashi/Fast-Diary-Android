package cn.swang.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;


import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import cn.swang.R;
import cn.swang.app.GlobalData;
import cn.swang.app.IConstants;
import cn.swang.entity.DayCard;
import cn.swang.entity.NoteCard;

/**
 * Created by sw on 2015/9/21.
 */
public class ShareBitmapUtils {

    public void convertDayCardBitmap(ConvertDayCardListener listener, DayCard dayCard, boolean isExport) {
        new ConvertDayCardAsyncTask(listener, dayCard, isExport).execute();
    }

    public void convertDayCardBitmap(ConvertDayCardListener listener, DayCard dayCard) {
        new ConvertDayCardAsyncTask(listener, dayCard, false).execute();
    }

    public interface ConvertDayCardListener {
        void onConvertSuccess(String imagePath, DayCard dayCard);
    }

    private class ConvertDayCardAsyncTask extends AsyncTask<Void, Void, String> {
        private ConvertDayCardListener listener;
        private DayCard dayCard;
        private boolean isExport;

        public ConvertDayCardAsyncTask(ConvertDayCardListener listener, DayCard dayCard, boolean isExport) {
            this.listener = listener;
            this.dayCard = dayCard;
            this.isExport = isExport;
        }

        @Override
        protected String doInBackground(Void... params) {
            return convertDayCardToBitmap(dayCard, isExport);
        }

        @Override
        protected void onPostExecute(String bitmap) {
            super.onPostExecute(bitmap);
            if (listener != null) {
                listener.onConvertSuccess(bitmap,dayCard);
            }
            ;
        }
    }

    private HashMap<String, ArrayList<String>> cacheMap = new HashMap<String, ArrayList<String>>();

    ArrayList<String> contentConvertArray(String text, int textWordCount) {
        if (cacheMap.containsKey(text)) {
            return cacheMap.get(text);
        }
        ArrayList<String> res = new ArrayList<String>();
        //String chinese = "[\u4e00-\u9fa5]";
        //String sign = "[,.!/<>?；‘：“、：。！？%￥#@，~—-]+";
        //String chinese = "^[\u4e00-\u9fa5]+$";
        String english = "^[a-zA-Z]+$";
        String sign = "[,.!~/';<>?]";
        int k = 0;
        int length = textWordCount * 2;
        for (int i = k; i < text.length(); i++) {
            String ch = text.substring(i, i + 1);
            if (ch.matches(english) || ch.matches(sign)) {
                length--;
            } else {
                length -= 2;
            }
            if (length <= 1) {
                String tmp = text.substring(k, i);
                res.add(tmp);
                length = textWordCount * 2;
                k = i;
            }
        }
        String tmp2 = text.substring(k, text.length());
        res.add(tmp2);
        cacheMap.put(text, res);
        return res;
    }

    public static final String SHARE_DIARY_SHARE_PREFERENCE = "share_diary_sp";
    public static final String IS_SHOW_TITLE_DATE = "is_show_title_date_sp";
    public static final String IS_SHOW_END_TAG = "is_show_end_tag_sp";
    public static final String SHOW_IMAGE_SIZE = "show_image_size";
    public static final String SHOW_TITLE_STRING = "show_title_string";

    public int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize =(int) Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = (int) Math.round((float) width / (float) reqWidth);
            }
//            double a= Math.ceil((float) height / (float) reqHeight);;
        }
        return inSampleSize;
    }

    public Bitmap decodeSampledBitmapFromFile(String filename,
                                                     int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filename, options);
    }

    public String convertDayCardToBitmap(DayCard dayCard, boolean isExport) {
        //HashMap<String,WeakReference<Bitmap>> hashMap = new HashMap<String,WeakReference<Bitmap>>();
        HashMap<String,Bitmap> hashMap = new HashMap<String,Bitmap>();
        int bitmap_height = 182;//header+footer
        int title_height = 45;
        int bitmap_width = 600;
        int img_width = 500;
        int img_height = 500;
        int textWordCount = 22;
        int noteMargin = 18;
        int textHeight = 30;
        int bitmapMargin = 22;//30;
        float bitmapRadio = 0.83f;

        try{
            SharedPreferences sharedPreferences = GlobalData.app().getSharedPreferences(SHARE_DIARY_SHARE_PREFERENCE, Context.MODE_PRIVATE);
            float img_size = sharedPreferences.getFloat(SHOW_IMAGE_SIZE, 100f);
            String titleInput = sharedPreferences.getString(SHOW_TITLE_STRING, "");
            boolean isShowTitle = !TextUtils.isEmpty(titleInput);
            bitmapRadio *= (img_size / 100f);
            img_height*=(img_size/100f);
            img_width*=(img_size/100f);

            //caculate width height
            if(isShowTitle){
                bitmap_height+=title_height;
            }
            for (NoteCard noteCard : dayCard.getNoteSet()) {
                if (!TextUtils.isEmpty(noteCard.getVoicePath())) continue;
                bitmap_height += noteMargin;
                if (!TextUtils.isEmpty(noteCard.getContent())) {
                    //bitmap_height+=Math.ceil(noteCard.getContent().length() * 1.0 / textWordCount)*textHeight;
                    bitmap_height += contentConvertArray(noteCard.getContent(), textWordCount).size() * textHeight;
                } else if (!TextUtils.isEmpty(noteCard.getImgPath())) {
                    final String key = noteCard.getImgPath();
                    //Bitmap mbitmap = BitmapFactory.decodeFile(noteCard.getImgPath());
                    Bitmap mbitmap = decodeSampledBitmapFromFile(noteCard.getImgPath(),img_width,img_height);
                    if (mbitmap.getWidth() > bitmap_width*bitmapRadio) {
                        int height = (int) ((mbitmap.getHeight() * bitmap_width / mbitmap.getWidth()) * bitmapRadio);
                        mbitmap = zoomImage(mbitmap, bitmap_width * bitmapRadio, height);
                    }
                    bitmap_height += (mbitmap.getHeight() + bitmapMargin);
                    hashMap.put(key, mbitmap);
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(bitmap_width, bitmap_height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);
            paint.setTextSize(23.0f);
            canvas.drawColor(Color.WHITE);
            int x = 50, y = 33;

            //draw start
            paint.setColor(Color.GRAY);
            String title = dayCard.getYear() + "/" + dayCard.getMouth() + "/" + dayCard.getDay();
            if (sharedPreferences.getBoolean(IS_SHOW_TITLE_DATE, true)) {
                canvas.drawText(title, bitmap_width - 140, y, paint);
            }
            y += 12;
            canvas.drawLine(10, y, bitmap_width - 10, y + 1, paint);
            paint.setColor(Color.BLACK);
            y += 50;

            //draw title
            if(isShowTitle){
                Paint titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);
                titlePaint.setTextSize(34.0f);
                titlePaint.setColor(Color.GRAY);
                titlePaint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(titleInput, bitmap_width/2, y, titlePaint);
                y+=title_height;
                sharedPreferences.edit().putString(SHOW_TITLE_STRING,"").commit();
            }

            //y=95
            for (NoteCard noteCard : dayCard.getNoteSet()) {
                if (!TextUtils.isEmpty(noteCard.getVoicePath())) continue;
                y += noteMargin;
                if (!TextUtils.isEmpty(noteCard.getContent())) {
                    //draw text
                    ArrayList<String> arrayList = contentConvertArray(noteCard.getContent(), textWordCount);
                    for (String tmp : arrayList) {
                        canvas.drawText(tmp, x, y, paint);
                        y += textHeight;
                    }
                } else if (!TextUtils.isEmpty(noteCard.getImgPath())) {
                    //draw bitmap
//                WeakReference<Bitmap> reference= hashMap.get(noteCard.getImgPath());
//                Bitmap tmpBitmap;
//                if(reference.get()!=null){
//                    tmpBitmap=reference.get();
//                }else {
//                    tmpBitmap = decodeSampledBitmapFromFile(noteCard.getImgPath(),img_width,img_height);
//                }
                    Bitmap tmpBitmap=hashMap.get(noteCard.getImgPath());
                    canvas.drawBitmap(tmpBitmap, x, y, paint);
                    y += (tmpBitmap.getHeight() + bitmapMargin);
                }
            }

            //draw end sign
            y += 40;
            paint.setColor(Color.GRAY);
            canvas.drawLine(10, y, bitmap_width - 10, y + 1, paint);
            y += 30;
            String endSign = GlobalData.app().getString(R.string.app_name);
            paint.setTextAlign(Paint.Align.CENTER);
            if (sharedPreferences.getBoolean(IS_SHOW_END_TAG, true)) {
                canvas.drawText(endSign, 300, y, paint);
            }

            canvas.save(canvas.ALL_SAVE_FLAG);//保存所有图层
            canvas.restore();
            String filePath;
            if (isExport) {
                filePath = Environment.getExternalStorageDirectory() + IConstants.SHARE_DIARY_LIST_PATH + dayCard.getYear() + "_" + dayCard.getMouth() + "_" + dayCard.getDay() + "/" + dayCard.getYear() + "_" + dayCard.getMouth() + "_" + dayCard.getDay() + ".jpg";
            } else {
                filePath = Environment.getExternalStorageDirectory() + IConstants.SHARE_PHOTO_PATH + dayCard.getYear() + "_" + dayCard.getMouth() + "_" + dayCard.getDay() + ".jpg";
            }
            File newFIle = new File(filePath);
            if (!newFIle.getParentFile().exists()) {
                newFIle.getParentFile().mkdirs();
            }
            saveBitmaptoFile(bitmap, newFIle.getAbsolutePath());
            return newFIle.getAbsolutePath();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(GlobalData.app(),GlobalData.app().getString(R.string.about_convert_diary_tips),Toast.LENGTH_SHORT).show();
        }
        return "";
    }

    public Bitmap zoomImage(Bitmap bgimage, double newWidth,
                            double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }

    public void saveBitmaptoFile(Bitmap bmp, String imagePath) {
        FileOutputStream fop;
        try {
            //实例化FileOutputStream，参数是生成路径
            fop = new FileOutputStream(imagePath);
            //压缩bitmap写进outputStream 参数：输出格式  输出质量  目标OutputStream
            //格式可以为jpg,png,jpg不能存储透明
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fop);
            fop.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
