package command;

public class SelectCommand implements SqlCommand{

    @Override
    public void execute(){
        System.out.println("This is where we would do a select");
    }
}
