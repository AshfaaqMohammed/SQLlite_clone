package command;

import java.util.Optional;

public class SqlCompiler {
    public Optional<SqlCommand> compile(String input){

        String[] tokens = input.split("\\s+");
        if (tokens.length == 0){
            return Optional.empty();
        }

        // Recognize insert
        if (tokens[0].equalsIgnoreCase("insert")){
            if (tokens.length != 4){
                return Optional.empty();
            }

            try{
                int id = Integer.parseInt(tokens[1]);
                String username = tokens[2];
                String email = tokens[3];
                return Optional.of(new InsertCommand(id,username,email));
            }catch (NumberFormatException e){
                return Optional.empty();
            }
        }

        // Recognize select
        if (tokens[0].equalsIgnoreCase("select")){
            return Optional.of(new SelectCommand());
        }
        return Optional.empty();
    }
}
