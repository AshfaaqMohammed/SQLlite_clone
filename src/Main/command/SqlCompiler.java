package command;

import java.util.Optional;

public class SqlCompiler {
    public Optional<SqlCommand> compile(String input){

        // Recognize insert
        if (input.startsWith("insert")){
            return Optional.of(new InsertCommand());
        }

        // Recognize select
        if (input.equals("select")){
            return Optional.of(new SelectCommand());
        }
        return Optional.empty();
    }
}
