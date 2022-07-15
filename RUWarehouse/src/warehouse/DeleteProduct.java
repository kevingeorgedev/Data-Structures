package warehouse;

/*
 * Use this class to test the deleteProduct method.
 */ 
public class DeleteProduct {
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
                case "delete":
                    int id2     = StdIn.readInt();
                    warehouse.deleteProduct(id2);
                    break;
            }
        }
        StdOut.print(warehouse.toString());
    }
}
