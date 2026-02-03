package cli;

import command.SqlExecutor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Console {

    private final InputHandler handler;

    public Console(SqlExecutor executor){
        this.handler = new InputHandler(executor);
    }

    public void run(){
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))){

            boolean running = true;
            while(running){
                System.out.print("db> ");
                String line = reader.readLine();

                if (line == null){
                    break;
                }
                running = handler.handle(line.trim());
            }

        }catch (IOException io){
            System.out.println(io.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
