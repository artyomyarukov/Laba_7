package server.utility;

public class IdCounter {
    private static long idCounter;
    public static void setId(long id){
        idCounter = id;
    }
    public static long getNextId(){
        idCounter++;
        return idCounter;
    }
    public static long getId(){
        idCounter++;
        return idCounter;
    }
}