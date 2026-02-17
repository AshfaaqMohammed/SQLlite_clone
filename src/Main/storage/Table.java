package storage;


import model.Row;
import storage.btree.LeafNode;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Table {
    private final Pager pager;
    private int rootPageNum;

    public Table(Pager pager) throws Exception {
        this.pager = pager;

        if (pager.getChannel().size() == 0){
            this.rootPageNum = 1;
            ByteBuffer root = pager.getPage(rootPageNum);
            LeafNode.initializeLeafNode(root,true);
            pager.flushAll();
        }else{
            this.rootPageNum = 1;
        }
    }

    public int getRootPageNum(){
        return rootPageNum;
    }

    public void setRootPageNum(int rootPageNum){
        this.rootPageNum = rootPageNum;
    }

    public Pager getPager(){
        return pager;
    }

    public boolean insertRow(Row row) throws Exception{
        int key = row.getId();
        ByteBuffer rootPage = pager.getPage(rootPageNum);

        int insertPos = LeafNode.findInsertPosition(rootPage,key);
        int numCells = LeafNode.getNumCells(rootPage);

        if (insertPos < numCells && LeafNode.getKey(rootPage,insertPos) == key){
            System.out.println("Error: Duplicate key " + key);
            return false;
        }

        if (numCells >= LeafNode.LEAF_NODE_MAX_CELLS){
            LeafNode.leafNodeSplitAndInsert(this, rootPageNum,key,row);
        }else{
            LeafNode.insert(rootPage, key, row);
        }
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
