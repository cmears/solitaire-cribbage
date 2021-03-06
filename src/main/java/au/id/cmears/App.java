package au.id.cmears;

public class App 
{
    public static void main( String[] args )
    {
        //int[] faces = {9,7,6,11,8,13,4,11,9,8,3,5,6, 9,4,7,12,13,4,2,10,8,3,7,11,4, 6,2,9,2,6,1,13,3,5,12,13,1,11, 2,12,10,10,10,7,12,1,8,1,3,5,5};
        //int[] faces = {13,7,11,2,13,9,3,3,7,9,5,5,1, 1,10,12,6,8,4,11,2,2,12,12,12,9, 5,7,11,3,2,6,13,8,6,10,8,11,1, 6,5,10,3,4,8,13,7,9,4,1,4,10};
        // int[] faces = {7,7,6,1,5,11,3,13,13,2,4,5,12, 3,7,11,8,6,10,7,10,5,2,13,6,1, 11,13,9,3,10,4,8,9,1,3,11,5,12, 4,12,4,8,9,8,12,2,9,6,1,10,2};
        // int[] faces = {1,3,7,3,5,11,8,6,11,12,9,3,6, 13,6,9,9,2,12,12,1,7,13,4,11,13, 7,8,2,10,4,5,8,1,6,8,10,13,4, 3,10,4,2,7,2,12,1,9,5,5,10,11};
        int[] faces = {4,6,12,13,2,5,11,4,13,7,10,1,13, 10,2,4,1,7,4,11,5,6,2,1,7,10, 6,6,3,9,13,11,1,8,2,8,8,9,12, 11,8,3,7,3,5,9,10,9,3,5,12,12};

        // The quick-and-dirty method.
        // Search search = new Search(new Deal(faces));
        // while (true) {
        //     search.probe(search.initialState());
        // }

        // The dynamic programming method.
        Dynamic d = new Dynamic(new Deal(faces));
        d.solve();
    }
}
