import java.io.Serializable;
import java.util.Objects;

public class Product implements Serializable {

    private String IDNum;
    private String name;
    private String description;
    private double cost;
    private int IDSeed;

    public Product(String IDNum, String name, String description, double cost) {
        this.IDNum = IDNum;
        this.name = name;
        this.description = description;
        this.cost = cost;
    }

    public Product(String name, String description, double cost) {
        this.IDNum = this.genIDNum();
        this.name = name;
        this.description = description;
        this.cost = cost;
    }

    //public void setIDSeed(int IDSeed) {Product.IDSeed = IDSeed;}

    /*public int getIDSeed() {
        return IDSeed;
    }*/
    // COMMENTING OUT IDSEED FOR FILESTREAMS ASSIGNMENT


    private String genIDNum() {
        // had some bugs here, so switched to synchronize to eliminate possibility of threading causing issues
        String newID = String.format("%06d", IDSeed);
        IDSeed++; // Increment the static IDSeed for the next ID generation.
        return newID;
    }

    public String getIDNum() {
        return IDNum;
    }

    public void setIDNum(String IDNum) {
        this.IDNum = IDNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    @Override
    public int hashCode() {
        return Objects.hash(IDNum, name, description, cost);
    }

    @Override
    public String toString() {
        return "Product{" +
                "IDNum='" + IDNum + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", cost='" + cost + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Double.compare(product.cost, cost) == 0
                && IDNum.equals(product.IDNum)
                && name.equals(product.name)
                && description.equals(product.description);
    }


    /**
     * Converts the Product object into a JSON-formatted String
     * This includes the IDNum, name, description, and cost.
     * Avoiding null values for now
     *
     * @return a String in JSON format
     */
    public String toJSONRecord()
    {
        String retString = "";
        char DQ = '\u0022';  // Assign the double quote char to a variable
        retString =  "{" + DQ + "IDNum" + DQ + ":" + DQ + this.IDNum + DQ + ",";
        retString += DQ + "name" + DQ + ":" + DQ + this.name + DQ + ",";
        retString += " " + DQ + "description"  + DQ + ":" + DQ + this.description + DQ + ",";
        retString += " " + DQ + "cost" + DQ + ":" + DQ + this.cost + "}";

        return retString;
    }

    /**
     * Converts the Product object into an XML-formatted String
     * Output includes the IDNum, name, description, and , and year of birth (YOB),
     * encapsulated within appropriate XML tags.
     * No support for null values atm for simpler read/write
     *
     * @return an XML formated String
     */
    public String toXMLRecord()
    {
        String retString = "";

        retString = "<Product>" + "<IDNum>" + this.IDNum + "</IDNum>";
        retString += "<name>" + this.name + "</name>";
        retString += "<description>" + this.description + "</description>";
        retString += "<cost>" + this.cost + "</cost></Product>";

        return retString;
    }

    /**
     * Converts the Product object into a CSV-formatted String.
     * Contains the IDNum, name, description, and cost.
     * CSV has the easiest time handling null values, so may add
     * that first, of the three functions. Currently no null support.
     *
     * @return a String in CSV format
     */
    public String toCSVRecord() {
        String div = ",";
        return  this.IDNum + div + this.name + div + this.description + div +
                this.cost;
    }
}
