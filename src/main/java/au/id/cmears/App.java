package au.id.cmears;

public class App 
{
    public static void main( String[] args )
    {
        //int[] faces = {9,7,6,11,8,13,4,11,9,8,3,5,6, 9,4,7,12,13,4,2,10,8,3,7,11,4, 6,2,9,2,6,1,13,3,5,12,13,1,11, 2,12,10,10,10,7,12,1,8,1,3,5,5};
        int[] faces = {13,7,11,2,13,9,3,3,7,9,5,5,1, 1,10,12,6,8,4,11,2,2,12,12,12,9, 5,7,11,3,2,6,13,8,6,10,8,11,1, 6,5,10,3,4,8,13,7,9,4,1,4,10};
        Search search = new Search(new Deal(faces));
        while (true) {
            search.probe(search.initialState());
        }
    }
}
