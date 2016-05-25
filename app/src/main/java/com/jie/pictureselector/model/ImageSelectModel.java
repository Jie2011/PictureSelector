package com.jie.pictureselector.model;

import java.io.Serializable;

/**
 * Created by ASUS on 2016/4/13.
 */
public class ImageSelectModel implements Serializable {

    public String url;
    public boolean select;

    public ImageSelectModel(String url, boolean select) {
        super();
        this.url = url;
        this.select = select;
    }

}