package command;

public class MetaCommandProcessor {
    public boolean process(String input){
        if (input.equals(".exit")){
            System.out.println("Exiting..");
            System.exit(0);
        }
        return false;
    }
}
