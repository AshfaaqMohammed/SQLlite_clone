package model;

import java.util.ArrayList;
import java.util.List;

public class Table {
    private final List<Row> rows = new ArrayList<>();

    public void insertRow(Row row){
        rows.add(row);
    }

    public List<Row> getAllRows(){
        return rows;
    }
}
