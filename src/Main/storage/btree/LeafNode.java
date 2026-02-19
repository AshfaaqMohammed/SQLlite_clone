package storage.btree;


import model.Row;
import storage.Cursor;
import storage.Pager;
import storage.Table;

import javax.swing.plaf.PanelUI;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;

public class LeafNode {
    private LeafNode(){

    }

    /*-------------------Header layout-------------------------------- */

    public static final byte NODE_TYPE_LEAF = 1;

    public static final int NODE_TYPE_SIZE = 1;
    public static final int IS_ROOT_SIZE = 1;
    public static final int NUM_CELLS_SIZE = 4;

    public static final int NODE_TYPE_OFFSET = 0;
    public static final int IS_ROOT_OFFSET = NODE_TYPE_OFFSET + NODE_TYPE_SIZE;
    public static final int NUM_CELLS_OFFSET = IS_ROOT_OFFSET + IS_ROOT_SIZE;

    public static final int LEAF_NODE_HEADER_SIZE = NODE_TYPE_SIZE + IS_ROOT_SIZE + NUM_CELLS_SIZE;

    /*-------------------Cell layout-------------------------------- */

    public static final int  LEAF_NODE_KEY_SIZE = Integer.BYTES;
    public static final int LEAF_NODE_VALUE_SIZE = Row.ROW_SIZE;

    public static final int LEAF_NODE_CELL_SIZE = LEAF_NODE_KEY_SIZE + LEAF_NODE_VALUE_SIZE;

    public static final int LEAF_NODE_SPACE_FOR_CELLS = Pager.PAGE_SIZE - LEAF_NODE_HEADER_SIZE;

    public final static int LEAF_NODE_MAX_CELLS = LEAF_NODE_SPACE_FOR_CELLS / LEAF_NODE_CELL_SIZE;

    /*-------------New constants for splitting node----------------------------*/
    public static final int LEAF_NODE_RIGHT_SPLIT_COUNT =  (LEAF_NODE_MAX_CELLS + 1) /2;
    public static final int LEAF_NODE_LEFT_SPLIT_COUNT = LEAF_NODE_MAX_CELLS - LEAF_NODE_RIGHT_SPLIT_COUNT;

    private static int cellOffset(int cellNum){
        return LEAF_NODE_HEADER_SIZE + cellNum * LEAF_NODE_CELL_SIZE;
    }

    private static int keyOffSet(int cellNum){
        return cellOffset(cellNum);
    }

    private static int valueOffset(int cellNum){
        return cellOffset(cellNum) + LEAF_NODE_KEY_SIZE;
    }

    /*-------------------Header helpers-------------------------------- */
    public static int getNumCells(ByteBuffer page){
        return page.getInt(NUM_CELLS_OFFSET);
    }

    public static void setNumCells(ByteBuffer page, int value){
        page.putInt(NUM_CELLS_OFFSET, value);
    }

    public static void initializeLeafNode(ByteBuffer page, boolean isRoot){
        page.put(NODE_TYPE_OFFSET,NODE_TYPE_LEAF);
        page.put(IS_ROOT_OFFSET, (byte) (isRoot ? 1 : 0));
        setNumCells(page,0);
    }

    /*------------------- Key / value helpers -------------------------------- */
    public static int getKey(ByteBuffer page, int cellNum){
        return page.getInt(keyOffSet(cellNum));
    }

    private static void setKey(ByteBuffer page, int cellNum, int key){
        page.putInt(keyOffSet(cellNum),key);
    }

    public static Row readValue(ByteBuffer page, int cellNum){
        page.position(valueOffset(cellNum));
        int id = page.getInt();
        String username = readString(page, Row.MAX_USERNAME_LENGTH);
        String email = readString(page, Row.MAX_EMAIL_LENGTH);
        return new Row(id,username,email);
    }
    private static void writeValue(ByteBuffer page, int cellNum, Row row){
        page.position(valueOffset(cellNum));
        page.putInt(row.getId());
        writeString(page, row.getUsername(),Row.MAX_USERNAME_LENGTH);
        writeString(page, row.getEmail(),Row.MAX_EMAIL_LENGTH);
    }

    /*-------------------  Insert logic -------------------------------- */
    public static void insert(ByteBuffer page, int key, Row row){
        int numCells = getNumCells(page);

        if (numCells >= LEAF_NODE_MAX_CELLS){
            throw new IllegalStateException("LeafNode full (split not implemented)");
        }

        int insertPos = 0;
        while (insertPos < numCells && getKey(page,insertPos) < key){
            insertPos ++;
        }

        for (int i = numCells; i> insertPos; i--){
            byte[] cell = new byte[LEAF_NODE_CELL_SIZE];
            page.position(cellOffset(i-1));
            page.get(cell);
            page.position(cellOffset(i));
            page.put(cell);
        }

        setKey(page, insertPos, key);
        writeValue(page, insertPos, row);
        setNumCells(page, numCells +1);
    }

    public static void leafNodeSplitAndInsert(
            Table table,
            int oldPageNum,
            int key,
            Row row
    ) throws Exception{
        Pager pager = table.getPager();

        //get old page
        ByteBuffer oldPage = pager.getPage(oldPageNum);

        // create/allocate new page
        int newPageNum = pager.getUnusedPageNum();
        ByteBuffer newPage = pager.getPage(newPageNum);

        initializeLeafNode(newPage, false);

        int oldNumCells = getNumCells(oldPage);
        int totalCells = oldNumCells + 1;

        int[] keys = new int[totalCells];
        Row[] rows = new Row[totalCells];

        int insertPos = findInsertPosition(oldPage, key);
        for(int i=0, j=0; i<totalCells; i++){
            if (i == insertPos){
                keys[i] = key;
                rows[i] = row;
            }else{
                keys[i] = getKey(oldPage,j);
                rows[i] = readValue(oldPage,j);
                j++;
            }
        }

        //reset life page
        setNumCells(oldPage,0);

        //left half
        for (int i=0; i < LEAF_NODE_LEFT_SPLIT_COUNT; i++){
            insert(oldPage, keys[i],rows[i]);
        }

        //right half
        for (int i=LEAF_NODE_LEFT_SPLIT_COUNT; i<LEAF_NODE_LEFT_SPLIT_COUNT + LEAF_NODE_RIGHT_SPLIT_COUNT;i++){
            insert(newPage, keys[i],rows[i]);
        }

        createNewRoot(table, oldPageNum, newPageNum);
    }

    public static void createNewRoot(
            Table table,
            int leftChildPageNum,
            int rightChildPageNum
    )throws Exception{
        Pager pager = table.getPager();

       int oldRootPageNum = table.getRootPageNum();
       ByteBuffer oldRoot = pager.getPage(oldRootPageNum);

       int leftCopyPageNum = pager.getUnusedPageNum();
       ByteBuffer leftCopy = pager.getPage(leftCopyPageNum);

       oldRoot.position(0);
       oldRoot.limit(Pager.PAGE_SIZE);

       leftCopy.position(0);
       leftCopy.put(oldRoot);
       leftCopy.position(0);

       leftCopy.put(LeafNode.IS_ROOT_OFFSET, (byte) 0);

       InternalNode.initializeInternalNode(oldRoot,true);

       InternalNode.setNumKeys(oldRoot,1);
       InternalNode.setChild(oldRoot,0,leftCopyPageNum);

       int leftMax = LeafNode.getKey(leftCopy, LeafNode.getNumCells(leftCopy)-1);
       InternalNode.setKey(oldRoot, 0, leftMax);
       InternalNode.setRightChild(oldRoot, rightChildPageNum);

       table.setRootPageNum(oldRootPageNum);
    }

    private static void writeString(ByteBuffer page, String s, int maxLen){
        byte[] data = s.getBytes(StandardCharsets.UTF_8);
        page.put(data);
        if (data.length < maxLen){
            page.put(new byte[maxLen - data.length]);
        }
    }

    private static String readString(ByteBuffer page, int maxLen){
        byte[] data = new byte[maxLen];
        page.get(data);
        return new String(data, StandardCharsets.UTF_8).trim();
    }

    public static int findInsertPosition(ByteBuffer page, int key){
        int numCells = getNumCells(page);

        int low = 0;
        int high = numCells;

        while (low < high){
            int mid = (low+high)/2;
            int keyAtMid = getKey(page,mid);

            if (key == keyAtMid){
                return mid;
            }
            if (key < keyAtMid){
                high = mid;
            }else{
                low = mid + 1;
            }
        }
        return low;
    }

    public static Cursor leafNodeFind(
            Table table,
            int pageNum,
            int key
    )throws Exception{
        Pager pager = table.getPager();
        ByteBuffer page = pager.getPage(pageNum);

        int numCells = getNumCells(page);

        int left = 0;
        int right = numCells;

        while (left < right){
            int mid = (left + right) / 2;
            int keyAtMid = getKey(page, mid);

            if (key == keyAtMid){
                return new Cursor(table, pageNum, mid);
            }

            if (key < keyAtMid){
                right = mid;
            }else{
                left = mid + 1;
            }
        }
        return new Cursor(table, pageNum, left);

    }
}
