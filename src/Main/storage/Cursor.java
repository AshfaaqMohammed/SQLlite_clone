package storage;

import model.Row;
import storage.btree.LeafNode;

import java.nio.ByteBuffer;

public class Cursor {
    private final Table table;
    private int pageNum;
    private int cellNum;
    private boolean endOfTable;

    public Cursor(Table table) throws Exception {
        this.table = table;
        this.pageNum = 1;
        this.cellNum = 0;

        ByteBuffer page = table.getPager().getPage(pageNum);
        endOfTable = LeafNode.getNumCells(page) == 0;
    }
    public Cursor(Table table, int pageNum, int cellNum){
        this.table = table;
        this.pageNum = pageNum;
        this.cellNum = cellNum;
    }

    public Row getRow() throws Exception {
        ByteBuffer page = table.getPager().getPage(pageNum);
        return LeafNode.readValue(page,cellNum);
    }
    public int getPageNum(){
        return pageNum;
    }

    public int getCellNum(){
        return cellNum;
    }

    public void advance() throws Exception {
        ByteBuffer page = table.getPager().getPage(pageNum);
        cellNum ++;
        if (cellNum >= LeafNode.getNumCells(page)){
            endOfTable = true;
        }
    }

    public Row value() throws Exception{
        ByteBuffer page = table.getPager().getPage(pageNum);
        return LeafNode.readValue(page, cellNum);
    }

    public boolean isEnd(){
        return endOfTable;
    }
}

