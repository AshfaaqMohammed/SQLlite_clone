package storage;


import model.Row;
import storage.btree.LeafNode;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Table {
    private final Pager pager;

    public Table(Pager pager) throws Exception {
        this.pager = pager;

        if (pager.getChannel().size() == 0){
            ByteBuffer root = pager.getPage(1);
            LeafNode.initializeLeafNode(root,true);
            pager.flushAll();
        }
    }

    public Pager getPager(){
        return pager;
    }

    public boolean insertRow(Row row) throws Exception{
        ByteBuffer root = pager.getPage(1);
        LeafNode.insert(root,row.getId(),row);
        return true;
    }

    public List<Row> getAllRows() throws Exception{
        List<Row> rows = new ArrayList<>();
        Cursor cursor = new Cursor(this);

        while(!cursor.isEnd()){
            rows.add(cursor.getRow());
            cursor.advance();
        }
        return rows;
    }

    public void close() throws Exception{
        pager.close();
    }

    public void printBTree() throws Exception {
        System.out.println("B-Tree Leaf Node Visualization");
        ByteBuffer page = pager.getPage(1);

        // Node header
        System.out.println("Node type      : LEAF");
        System.out.println("Is root        : true");
        int numCells = LeafNode.getNumCells(page);
        System.out.println("num_cells      : " + numCells);

        // Print each cell: key and full row
        for (int i = 0; i < numCells; i++) {
            int key = LeafNode.getKey(page, i);
            Row row = LeafNode.readValue(page, i);

            System.out.println("  - cell " + i);
            System.out.println("      key      : " + key);
            System.out.println("      row.id   : " + row.getId());
            System.out.println("      username : " + row.getUsername());
            System.out.println("      email    : " + row.getEmail());
        }
    }
}
