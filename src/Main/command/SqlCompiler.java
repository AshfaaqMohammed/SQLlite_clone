package command;

import java.util.Optional;
import java.util.OptionalLong;

public class SqlCompiler {
    private static final int MAX_USERNAME_LENGTH = 32;
    private static final int MAX_EMAIL_LENGTH = 255;

    public Optional<SqlCommand> compile(String input){

        String[] tokens = input.split("\\s+");
        if (tokens.length == 0){
            return Optional.empty();
        }

        // Recognize insert
        if (tokens[0].equalsIgnoreCase("insert")){
            if (tokens.length != 4){
                System.out.println("Syntax error. could not parse statement.");
                return Optional.empty();
            }

            try{
                int id = Integer.parseInt(tokens[1]);

                if (id < 0){
                    System.out.println("ID must be positive");
                    return Optional.empty();
                }
                String username = tokens[2];
                String email = tokens[3];

                if (username.length() > MAX_USERNAME_LENGTH || email.length() > MAX_EMAIL_LENGTH){
                    System.out.println("String is too long.");
                    return Optional.empty();
                }

                return Optional.of(new InsertCommand(id,username,email));
            }catch (NumberFormatException e){
                System.out.println("Syntax error. Could not parse statement.");
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
