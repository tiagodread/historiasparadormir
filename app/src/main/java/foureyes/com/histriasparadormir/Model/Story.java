package foureyes.com.histriasparadormir.Model;

/**
 * Created by dev on 21/02/18.
 */

public class Story {
    private String title;
    private String content;
    private String type;
    private String thumbnail;

    public Story(String title, String content, String type, String thumbnail) {
        this.title = title;
        this.content = content;
        this.type = type;
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
