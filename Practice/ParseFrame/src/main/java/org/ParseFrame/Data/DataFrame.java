package org.ParseFrame.Data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class DataFrame {
    private String name;
    private Map<String, Column<?>> columns;
    private int rowCount;

    public DataFrame(String name){
        this.name = name;
        this.columns = new HashMap<>();
        this.rowCount = 0;
    }
    public DataFrame(){
        this("unnamed_dataframe");
    }
    // Column operations
    public <T> void addColumn(Column<T> column) {
        if (column == null) {
            throw new IllegalArgumentException("Column cannot be null");
        }

        if (!columns.isEmpty() && column.getData().size() != rowCount) {
            throw new IllegalArgumentException(
                    "Column size mismatch. Expected: " + rowCount + ", Got: " + column.getData().size());
        }

        columns.put(column.getName(), column);

        if (columns.size() == 1) {
            rowCount = column.getData().size();
        }
    }

}
