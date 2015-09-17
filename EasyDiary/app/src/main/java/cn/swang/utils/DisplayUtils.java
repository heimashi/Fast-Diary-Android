
package cn.swang.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.Pair;

import cn.swang.app.GlobalData;

public class DisplayUtils {

    private static DisplayMetrics sMetrics = null;

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    private static void initialize() {
        if (sMetrics == null) {
            sMetrics = GlobalData.app().getResources().getDisplayMetrics();
        }
    }

    public static float getDensity() {
        initialize();
        return sMetrics.density;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(final float dpValue) {
        initialize();
        final float scale = sMetrics.density;
        return (int) ((dpValue * scale) + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(final float pxValue) {
        initialize();
        final float scale = sMetrics.density;
        return (int) ((pxValue / scale) + 0.5f);
    }

    /**
     * 获取屏幕宽度和高度，单位为px
     * 
     * @param context
     * @return pair对象, first是width，second是height
     */
    public static Pair<Integer, Integer> getScreenWidthAndHeight() {
        initialize();
        return new Pair<Integer, Integer>(sMetrics.widthPixels, sMetrics.heightPixels);
    }

    public static int getScreenWidth() {
        initialize();
        return sMetrics.widthPixels;
    }

    public static int getScreenHeight() {
        initialize();
        return sMetrics.heightPixels;
    }

    /**
     * 获取屏幕长宽比(height/width)
     * 
     * @param context
     * @return
     */
    public static float getScreenRate() {
        float H = getScreenHeight();
        float W = getScreenWidth();
        return (H / W);
    }
}
