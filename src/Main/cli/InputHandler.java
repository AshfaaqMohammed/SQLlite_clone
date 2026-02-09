package cli;

import command.SqlCommand;
import command.SqlCompiler;
import command.SqlExecutor;

import java.util.Optional;

public class InputHandler {
    private final SqlCompiler compiler = new SqlCompiler();
    private final SqlExecutor executor;

    public InputHandler(SqlExecutor executor){
        this.executor = executor;
    }

    public boolean handle(String input) throws Exception {
        if (input.isEmpty()){
            return true;
        }

        if (input.startsWith(".")){
            if (input.equals(".exit")){
                System.out.println("Exiting...");
            }else{
                System.out.printf("Unrecognized command '%s' %n",input);
            }
            return false;
        }

        Optional<SqlCommand> statementOpt = compiler.compile(input);
        if (statementOpt.isEmpty()){
            System.out.printf("Unrecognized statement '%s' %n",input);
            return true;
        }
        executor.execute(statementOpt.get());
        return true;
    }

}
