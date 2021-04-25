package org.example;


/**
 *  Main class
 * @author BrahianVT
 * */
public class Main {
    static int ELEMENTS_PER_SECOND = 2;
    static int BUFFER_SIZE_SHELF = 10;
    static int BUFFER_SIZE_GENERIC_SHELF = 15;
    public static void main(String[] args) {
        if(args.length == 1)
            ELEMENTS_PER_SECOND = Integer.parseInt(args[0]);
        if(args.length == 2)
            BUFFER_SIZE_SHELF = Integer.parseInt(args[1]);
        if(args.length == 3)
            BUFFER_SIZE_GENERIC_SHELF = Integer.parseInt(args[1]);

        Run r = new Run();
        r.run();
    }
}
