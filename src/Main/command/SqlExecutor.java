package command;

import storage.Table;

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
}
