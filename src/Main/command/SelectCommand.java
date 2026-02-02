package command;

public class SelectCommand implements SqlCommand{

    @Override
    public void execute(){
        System.out.println("SELECT");
    }
}
