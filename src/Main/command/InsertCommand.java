package command;

import model.Row;

public class InsertCommand implements SqlCommand{

    private final Row row;

    public InsertCommand(int id, String username, String email){
        this.row = new Row(id,username,email);
    }

    public Row getRow(){
        return row;
    }

    @Override
    public void execute(){
        System.out.println("INSERT "+ row);
    }
}
