package app;

import cli.Console;
import command.SqlExecutor;
import storage.Pager;
import storage.Table;

import java.util.Objects;

public class Main {
    public static void main(String[] args) {

        if (args.length < 1){
            System.out.println("Usage: pass db_file as argument");
            System.exit(1);
        }
        String filename = args[0];

        try{
            Pager pager = new Pager(filename);
            Table table = new Table(pager);

            SqlExecutor executor = new SqlExecutor(table);

            Console console = new Console(executor);
            console.run();

            table.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
