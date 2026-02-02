package cli;

import command.MetaCommandProcessor;
import command.SqlCommand;
import command.SqlCompiler;
import command.SqlExecutor;

import java.util.Optional;

public class InputHandler {
    private final MetaCommandProcessor metaProcessor = new MetaCommandProcessor();
    private final SqlCompiler compiler = new SqlCompiler();
    private final SqlExecutor executor = new SqlExecutor();

    public void handle(String input){
        if (input.isEmpty()){
            return;
        }

        if (input.startsWith(".")){
            if (!metaProcessor.process(input)){
                System.out.printf("Unrecognized command '%s' %n",input);
            }
            return;
        }

        Optional<SqlCommand> statementOpt = compiler.compile(input);
        if (statementOpt.isEmpty()){
            System.out.printf("Unrecognized statement '%s' %n",input);
            return;
        }
        executor.execute(statementOpt.get());
    }

}
