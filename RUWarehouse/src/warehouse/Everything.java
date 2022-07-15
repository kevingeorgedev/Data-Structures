package warehouse;

/*
 * Use this class to put it all together.
 */ 
public class Everything {
    public static void main(String[] args) {
        StdIn.setFile(args[0]);
        StdOut.setFile(args[1]);
        Warehouse warehouse = new Warehouse();
        int len = StdIn.readInt();
        for(int i = 0; i < len; ++i){
            String action = StdIn.readString();
            switch (action){
                case "add":
                    int day     = StdIn.readInt();
                    int id      = StdIn.readInt();
                    String name = StdIn.readString();
                    int stock   = StdIn.readInt();
                    int demand  = StdIn.readInt();
                    warehouse.addProduct(id, name, stock, day, demand);
                    break;
                case "purchase":
                    int day2    = StdIn.readInt();  
                    int id2     = StdIn.readInt();
                    int amount  = StdIn.readInt();
                    warehouse.purchaseProduct(id2, day2, amount);
                    break;
                case "delete":
                    int id3     = StdIn.readInt();
                    warehouse.deleteProduct(id3);
                    break;
                case "restock":
                    int id4     = StdIn.readInt();
                    int amount2 = StdIn.readInt();
                    warehouse.restockProduct(id4, amount2);
                    break;
            }
        }
        StdOut.print(warehouse.toString());
    }
}
