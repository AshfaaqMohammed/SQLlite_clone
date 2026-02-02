package command;

public class InsertCommand implements SqlCommand{

    @Override
    public void execute(){
        System.out.println("This is where we would do an insert.");
    }
}
