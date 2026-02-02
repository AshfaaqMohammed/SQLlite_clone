package command;

public class SqlExecutor {
    public void execute(SqlCommand cmd){
        cmd.execute();
        System.out.println("Executed.");
    }
}
