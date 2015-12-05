package albsig.geonotes.dbModels;

/**
 * Created by Ihsan on 05.12.2015.
 */
public class Filter {
    private int id;
    private String description;

    public Filter(int id, String description){
        this.id = id;
        this.description = description;
    }

    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getDescription(){
        return this.description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String toString(){
        return "ID: " + this.id + " Description: " + this.description;
    }
}
