package bookshelf.globdig.com.bookshelf;

/**
 * Created by Sandip on 02/17/2016.
 */
public class PagesObject {

    String magazine_id;
    String page_number;
    String heading;
    String content;

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMagazine_id() {
        return magazine_id;
    }

    public void setMagazine_id(String magazine_id) {
        this.magazine_id = magazine_id;
    }

    public String getPage_number() {
        return page_number;
    }

    public void setPage_number(String page_number) {
        this.page_number = page_number;
    }
}
