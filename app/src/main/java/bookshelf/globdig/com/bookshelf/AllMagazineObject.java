package bookshelf.globdig.com.bookshelf;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by Navin on 02/11/2016.
 */
public class AllMagazineObject {

    String name;
    String image_url;
    Bitmap photo;
    int id;
    String desc;
    String image_url1;
    int id1;
    byte[]image;
    PagesObject obj;
    ArrayList<PagesObject>pageList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public int getId() {
        return id;
    }

    public int getId1() {
        return id1;
    }

    public void setId1(int id1) {
        this.id1 = id1;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage_url1() {
        return image_url1;
    }

    public void setImage_url1(String image_url1) {
        this.image_url1 = image_url1;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public PagesObject getObj() {
        return obj;
    }

    public void setObj(PagesObject obj) {
        this.obj = obj;
    }

    public ArrayList<PagesObject> getPageList() {
        return pageList;
    }

    public void setPageList(ArrayList<PagesObject> pageList) {
        this.pageList = pageList;
    }
}
