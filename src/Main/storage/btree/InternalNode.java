package storage.btree;

import storage.Pager;

import java.nio.ByteBuffer;
/*
    An internal node:
    Does NOT store row data
    Stores:
        - keys
        - child page numbers
    Used for navigation only
 */

public class InternalNode {
    private InternalNode(){

    }

    // The common header (node type + isRoot + numCells) is the same
    public static final int NODE_TYPE_SIZE = 1;
    public static final int IS_ROOT_SIZE = 1;
    public static final int NUM_KEYS_SIZE = Integer.BYTES;

    public static final int NODE_TYPE_OFFSET = 0;
    public static final int IS_ROOT_OFFSET = NODE_TYPE_OFFSET + NODE_TYPE_SIZE;
    public static final int NUM_KEYS_OFFSET = IS_ROOT_OFFSET + IS_ROOT_SIZE;

    public static final int INTERNAL_NODE_HEADER_SIZE = NODE_TYPE_SIZE + IS_ROOT_SIZE + NUM_KEYS_SIZE;

    // child and key sizes
    public static final int INTERNAL_NODE_CHILD_SIZE = Integer.BYTES;
    public static final int INTERNAL_NODE_KEY_SIZE = Integer.BYTES;
    public static final int INTERNAL_NODE_CELL_SIZE = INTERNAL_NODE_CHILD_SIZE + INTERNAL_NODE_KEY_SIZE;

    //space for cells
    public static final int INTERNAL_NODE_SPACE_FOR_CELLS = Pager.PAGE_SIZE - INTERNAL_NODE_HEADER_SIZE;

    public static final int INTERNAL_NODE_MAX_CELLS = INTERNAL_NODE_SPACE_FOR_CELLS / INTERNAL_NODE_CELL_SIZE;

    //offset of the first body cell
    public static final int INTERNAL_NODE_CELL_OFFSET = INTERNAL_NODE_HEADER_SIZE;

    //------------------Helpers--------------------------

    // Number of keys in this internal node
    public static int getNumKeys(ByteBuffer page){
        return page.getInt(NUM_KEYS_OFFSET);
    }

    public static void setNumKeys(ByteBuffer page, int numKeys){
        page.putInt(NUM_KEYS_OFFSET,numKeys);
    }

    //child pointer for rightmost child
    public static int getRightChild(ByteBuffer page){
        int numKeys = getNumKeys(page);
        int offset = INTERNAL_NODE_CELL_OFFSET + (numKeys * INTERNAL_NODE_CELL_SIZE);
        return page.getInt(offset);
    }

    public static void setRightChild(ByteBuffer page, int childPageNum){
        int numKeys = getNumKeys(page);
        int offset = INTERNAL_NODE_CELL_OFFSET + (numKeys * INTERNAL_NODE_CELL_SIZE);
        page.putInt(offset,childPageNum);
    }

    //cell position helpers
    private static int internalNodeCellOffset(int cellNum){
        return INTERNAL_NODE_CELL_OFFSET + cellNum * INTERNAL_NODE_CELL_SIZE;
    }

    public static int getChild(ByteBuffer page, int childIndex){
        if (childIndex == getNumKeys(page)){
            return getRightChild(page);
        }
        return page.getInt(internalNodeCellOffset(childIndex));
    }

    public static void setChild(ByteBuffer page, int childIndex, int childPageNum){
        page.putInt(internalNodeCellOffset(childIndex), childPageNum);
    }

    public static int getKey(ByteBuffer page, int keyIndex){
        return page.getInt(internalNodeCellOffset(keyIndex) + INTERNAL_NODE_CHILD_SIZE);
    }

    public static void setKey(ByteBuffer page, int keyIndex, int key){
        page.putInt(internalNodeCellOffset(keyIndex) + INTERNAL_NODE_CHILD_SIZE,key);
    }

    public static void initializeInternalNode(ByteBuffer page, boolean isRoot){
        page.put(NODE_TYPE_OFFSET,(byte) 2);
        page.put(IS_ROOT_OFFSET, (byte) (isRoot ? 1:0));
        setNumKeys(page,0);
        setRightChild(page,0);
    }

}
