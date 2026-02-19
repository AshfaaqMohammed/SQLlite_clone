package storage;


import model.Row;
import storage.btree.InternalNode;
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
        Cursor cursor = tableFind(key);

        ByteBuffer page = pager.getPage(cursor.getPageNum());

        if (cursor.getCellNum() < LeafNode.getNumCells(page)){
            int keyAtIndex = LeafNode.getKey(page, cursor.getCellNum());
            if( keyAtIndex == key){
                System.out.println("Duplicate key.");
                return false;
            }
        }
        if (LeafNode.getNumCells(page) >= LeafNode.LEAF_NODE_MAX_CELLS){
            LeafNode.leafNodeSplitAndInsert(this, cursor.getPageNum(), key, row);
        }else{
            LeafNode.insert(page, key, row);
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

    //part 11
    public Cursor tableFind(int key)throws Exception{
        return nodeFind(rootPageNum,key);
    }

    private Cursor nodeFind(int pageNum, int key) throws Exception{
        ByteBuffer page = pager.getPage(pageNum);
        byte nodeType = page.get(0);

        if (nodeType == LeafNode.NODE_TYPE_LEAF){
            return LeafNode.leafNodeFind(this, pageNum, key);
        }else{
            return internalNodeFind(pageNum,key);
        }
    }

    private Cursor internalNodeFind(int pageNum, int key) throws Exception{
        ByteBuffer page = pager.getPage(pageNum);
        int numKeys = InternalNode.getNumKeys(page);

        int childIndex = 0;
        while(childIndex < numKeys){
            int keyToRight = InternalNode.getKey(page, childIndex);
            if(key <= keyToRight){
                break;
            }
            childIndex++;
        }
        int childPageNum = InternalNode.getChild(page,childIndex);
        return nodeFind(childPageNum, key);
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
