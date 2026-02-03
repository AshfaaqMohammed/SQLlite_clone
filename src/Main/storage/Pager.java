package storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

public class Pager {
    public static final int PAGE_SIZE = 4096;

    private final RandomAccessFile dbFile;
    private final FileChannel channel;
    private final Map<Integer, ByteBuffer> cache = new HashMap<>();

    public Pager(String filename) throws Exception{
        File file = new File(filename);
        dbFile = new RandomAccessFile(file,"rw");
        channel = dbFile.getChannel();
        System.out.println("DB File size: " + channel.size());
    }

    public ByteBuffer getPage(int pageNum) throws Exception{
        if (cache.containsKey(pageNum)){
            return cache.get(pageNum);
        }

        //load from disk if exists
        long offset = (long) pageNum * PAGE_SIZE;
        ByteBuffer buffer = ByteBuffer.allocate(PAGE_SIZE);

        //channel.size ---> the current size of the underlying file, in bytes
        if (channel.size() >= offset+PAGE_SIZE){
            // this condition check if page is present on disk or not
            channel.position(offset); // if present move the pointer to offset
            channel.read(buffer); // read the page contents and copy to buffer
        }

        buffer.position(0);
        cache.put(pageNum,buffer);
        return buffer;
    }

    public void flushAll() throws Exception{
        for (Map.Entry<Integer,ByteBuffer> entry : cache.entrySet()){
            int pageNum = entry.getKey();
            ByteBuffer buffer = entry.getValue();

            buffer.position(0);
            long offset = (long) pageNum * PAGE_SIZE;
            channel.position(offset);
            channel.write(buffer);
        }
        channel.force(true);
    }

    public void close() throws Exception{
        flushAll();
        channel.close();
        dbFile.close();
    }

    public FileChannel getChannel(){
        return this.channel;
    }
}
