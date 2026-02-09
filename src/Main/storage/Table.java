package storage;


import model.Row;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Table {
    private final Pager pager;
    private final int rowsPerPage;
    private int numRows;

    public Table(Pager pager) throws Exception {
        this.pager = pager;
        this.rowsPerPage = Pager.PAGE_SIZE / Row.ROW_SIZE;

        if (pager.getChannel().size() == 0){
            // brand new DB file -> initialize metadata page
            ByteBuffer meta = pager.getPage(0);
            meta.position(0);
            meta.putInt(0);
            pager.flushAll();
        }

        //load numRows from metadata
        ByteBuffer meta = pager.getPage(0);
        meta.position(0);
        this.numRows = meta.getInt();
        System.out.println("num of rows - " + numRows);
    }

    public int getNumRows(){
        return numRows;
    }

    public int getRowsPerPage(){
        return rowsPerPage;
    }

    public Pager getPager(){
        return pager;
    }

    public boolean insertRow(Row row) throws Exception{
        int pageNum = (numRows / rowsPerPage)+1;
        int rowOffset = numRows % rowsPerPage;
        ByteBuffer page = pager.getPage(pageNum);

        int writePos = rowOffset * Row.ROW_SIZE;
        serializeRow(row, page, writePos);

        numRows++;
        ByteBuffer meta = pager.getPage(0);
        meta.position(0);
        meta.putInt(numRows);
        return true;
    }

    public List<Row> getAllRows() throws Exception{
        List<Row> result = new ArrayList<>();

        Cursor cursor = new Cursor(this,0);
        while (!cursor.isEnd()){
            result.add(cursor.getRow());
            cursor.advance();
        }

        return result;
    }

    private void serializeRow(Row row, ByteBuffer page, int pos){
        page.position(pos);
        page.putInt(row.getId());
        putString(page, row.getUsername(), Row.MAX_USERNAME_LENGTH);
        putString(page, row.getEmail(), Row.MAX_EMAIL_LENGTH);
    }

    private void putString(ByteBuffer page, String s, int maxLen){
        byte[] data = s.getBytes(StandardCharsets.UTF_8);
        page.put(data);
        if (data.length < maxLen){
            page.put(new byte[maxLen-data.length]);
        }
    }

    Row deserializeRow(ByteBuffer page, int pos){
        page.position(pos);
        int id = page.getInt();
        String username = getString(page, Row.MAX_USERNAME_LENGTH);
        String email = getString(page, Row.MAX_EMAIL_LENGTH);
        return new Row(id,username,email);
    }

    private String getString(ByteBuffer page, int maxLen){
        byte[] bytes = new byte[maxLen];
        page.get(bytes);
        return new String(bytes, StandardCharsets.UTF_8).trim();
    }

    public void close() throws Exception{
        pager.close();
    }


}
