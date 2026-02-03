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

    public Table(Pager pager) throws IOException {
        this.pager = pager;
        this.rowsPerPage = Pager.PAGE_SIZE / Row.ROW_SIZE;
        System.out.println("rowsPerPage - "+rowsPerPage);

        long fileSize = pager.getChannel().size();
        System.out.println("filesize = " + fileSize);
        long fullPages = fileSize/Pager.PAGE_SIZE;
        int rowsFromFullPages = (int) (fullPages * rowsPerPage);
        long remaining = fileSize % Pager.PAGE_SIZE;
        int rowsInPartialPage = (int) (remaining / Row.ROW_SIZE);
        this.numRows = rowsFromFullPages + rowsInPartialPage;
        System.out.println("Iniaital rows = " + numRows);
    }

    public boolean insertRow(Row row) throws Exception{
        int pageNum = numRows / rowsPerPage;
        int rowOffset = numRows % rowsPerPage;
        ByteBuffer page = pager.getPage(pageNum);

        int writePos = rowOffset * Row.ROW_SIZE;
        serializeRow(row, page, writePos);

        numRows++;
        return true;
    }

    public List<Row> getAllRows() throws Exception{
        System.out.println("in getallrows method");
        List<Row> result = new ArrayList<>();

        for (int i=0; i<numRows; i++){
            int pageNum = i/rowsPerPage;
            int rowOffSet = i % rowsPerPage;
            ByteBuffer page = pager.getPage(pageNum);
            int readPos = rowOffSet * Row.ROW_SIZE;
            Row row = deserializeRow(page, readPos);
            result.add(row);
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

    private Row deserializeRow(ByteBuffer page, int pos){
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
