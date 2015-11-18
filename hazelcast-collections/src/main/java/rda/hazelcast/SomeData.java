package rda.hazelcast;

import java.io.Serializable;

public class SomeData implements Serializable {
    private static final long serialVersionUID = -670286299469698691L;

    private String id;
    private String text;

    public SomeData() {
    }

    public SomeData(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "SomeData{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}