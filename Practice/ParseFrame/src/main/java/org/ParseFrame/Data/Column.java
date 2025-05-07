package org.ParseFrame.Data;
import java.util.ArrayList;
import java.util.List;

public class Column<T> {
    private String name;
    private String type;
    private List<T> data;
    public Column(String name, String type){
        this.type = type;
        this.name = name;
        this.data = new ArrayList<>();
    }
    public Column(String name, String type, List<T> data) {
        this.name = name;
        this.type = type;
        this.data = new ArrayList<>(data);
    }
    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = new ArrayList<>(data);
    }
    //Data manipulation methods
    public void add(List<T> values){
        data.addAll(values);
    }

}
