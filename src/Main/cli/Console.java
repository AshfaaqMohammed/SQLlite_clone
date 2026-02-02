package cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Console {

    private final InputHandler handler = new InputHandler();
    public void run(){
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))){

            while(true){
                System.out.print("db> ");
                String line = reader.readLine();

                if (line == null){
                    break;
                }
                handler.handle(line.trim());
            }

        }catch (IOException io){
            System.out.println(io.getMessage());
        }
    }

}
