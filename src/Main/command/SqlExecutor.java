package command;

import model.Table;

public class SqlExecutor {

    private final Table table = new Table();

    public void execute(SqlCommand cmd){
        if (cmd instanceof InsertCommand insertCommand){
            table.insertRow(insertCommand.getRow());
            System.out.println("Executed insert");
        }else if (cmd instanceof SelectCommand selectCommand){
            table.getAllRows().forEach(System.out::println);
        }else{
            System.out.println("Unknown command type.");
        }
    }
}
