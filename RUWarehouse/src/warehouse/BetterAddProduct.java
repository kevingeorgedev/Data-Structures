package warehouse;

/*
 * Use this class to test the betterAddProduct method.
 */ 
public class BetterAddProduct {
    public static void main(String[] args) {
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
            warehouse.betterAddProduct(id, name, stock, day, demand);
        }
        StdOut.print(warehouse.toString());
    }
}
