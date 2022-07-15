package warehouse;

public class Restock {
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
                case "restock":
                    int id2     = StdIn.readInt();
                    int amount = StdIn.readInt();
                    warehouse.restockProduct(id2, amount);
                    break;
            }
        }
        StdOut.print(warehouse.toString());
    }
}
