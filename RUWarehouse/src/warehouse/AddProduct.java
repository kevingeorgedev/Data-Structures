package warehouse;

/*
 * Use this class to test to addProduct method.
 */
public class AddProduct {
    public static void main(String[] args) {
        //javac -d bin src/warehouse/*.java
        //java -cp bin warehouse.AddProduct addproduct.in addproduct.out
        StdIn.setFile(args[0]);
        StdOut.setFile(args[1]);
        int len = StdIn.readInt();
        Warehouse warehouse = new Warehouse();
        for(int i = 0; i < len; ++i){
            int day = StdIn.readInt();
            int id  = StdIn.readInt();
            String name = StdIn.readString();
            int stock = StdIn.readInt();
            int demand = StdIn.readInt();
            warehouse.addProduct(id, name, stock, day, demand);
        }
        StdOut.print(warehouse.toString());
    }
}
