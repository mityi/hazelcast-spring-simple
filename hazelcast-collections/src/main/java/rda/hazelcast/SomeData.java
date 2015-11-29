package rda.hazelcast;

import java.io.Serializable;

public class SomeData implements Serializable {
    private static final long serialVersionUID = -670286299469698691L;

    private String id;
    private String text;
    private int count;
    private boolean active;

    public SomeData() {
    }

    public SomeData(String id) {
        this.id = id;
    }

    public SomeData(String id, String text) {
        this.id = id;
        this.text = text;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "SomeData{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                ", count=" + count +
                ", active=" + active +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SomeData someData = (SomeData) o;

        if (id != null ? !id.equals(someData.id) : someData.id != null) return false;
        return !(text != null ? !text.equals(someData.text) : someData.text != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        return result;
    }
}