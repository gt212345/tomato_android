package org.itri.tomato;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;

/**
 * Created by hrw on 15/7/9.
 */
public class MarketListItem {
    Bitmap image1;
    Bitmap image2;
    String content;

    public MarketListItem(Bitmap image1, Bitmap image2, String content) {
        this.image1 = image1;
        this.image2 = image2;
        this.content = content;
    }

    public Bitmap getImage1() {
        return image1;
    }

    public Bitmap getImage2() {
        return image2;
    }

    public String getContent() {
        return content;
    }
}
