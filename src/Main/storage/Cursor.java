package storage;

import model.Row;

import java.nio.ByteBuffer;

public class Cursor {
    private final Table table;
    private int rowNum;
    private boolean endOfTable;

    public Cursor(Table table, int rowNum){
        this.table = table;
        this.rowNum = rowNum;
        this.endOfTable = rowNum >= table.getNumRows();
    }

    public Row getRow() throws Exception {
        int rowsPerPage = table.getRowsPerPage();
        int pageNum = (rowNum / rowsPerPage) + 1;
        int rowOffSet = rowNum % rowsPerPage;

        ByteBuffer page = table.getPager().getPage(pageNum);
        int pos = rowOffSet * Row.ROW_SIZE;

        return table.deserializeRow(page,pos);
    }

    public void advance(){
        rowNum++;
        if(rowNum >= table.getNumRows()){
            endOfTable = true;
        }
    }

    public boolean isEnd(){
        return endOfTable;
    }
}

