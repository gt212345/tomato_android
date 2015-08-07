package org.itri.tomato;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;

/**
 * Created by hrw on 15/7/9.
 */
public class ListItem {
    Bitmap image1;
    Bitmap image2;
    String content;
    boolean isMy, able;
    boolean has2Image;

    public ListItem(Bitmap image1, Bitmap image2, String content, boolean has2Image, boolean isMy, boolean able) {
        this.image1 = image1;
        this.image2 = image2;
        this.content = content;
        this.has2Image = has2Image;
        this.isMy = isMy;
        this.able = able;
    }

    public Bitmap getImage1() {
        return image1;
    }

    public Bitmap getImage2() {
        if (has2Image) {
            return image2;
        } else {
            return null;
        }
    }

    public String getContent() {
        return content;
    }

    public boolean isHas2Image() {
        return has2Image;
    }

    public boolean isMy() {
        return isMy;
    }

    public boolean getAble() {
        return able;
    }
}
