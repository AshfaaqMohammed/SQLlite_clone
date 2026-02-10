package command;

import storage.PageDumper;
import storage.Table;

import java.nio.ByteBuffer;

public class SqlExecutor {

    private final Table table;
    public SqlExecutor(Table table){
        this.table = table;
    }

    public void execute(SqlCommand cmd) throws Exception {
        if (cmd instanceof InsertCommand insertCommand){
            table.insertRow(insertCommand.getRow());
        }else if (cmd instanceof SelectCommand selectCommand){
            table.getAllRows().forEach(System.out::println);
        }else{
            System.out.println("Unknown command type.");
        }
    }
    public void printBtree() throws Exception {
        table.printBTree();
    }
    public void printHextree() throws Exception{
        ByteBuffer page = table.getPager().getPage(1);
        PageDumper.dumpPage(page);
    }
}
