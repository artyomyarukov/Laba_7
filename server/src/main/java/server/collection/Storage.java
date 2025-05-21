package server.collection;
import common.collection.City;
import lombok.Data;


import java.util.Hashtable;

/*Класс хранилеще для получения элемента коллеккции по id и
  работы с ним
 */

@Data
public class Storage {
    private final Hashtable<String, City> map;

    public Storage(Hashtable<String, City> map) {
        this.map = map;
    }

    public Storage() {
        this.map = new Hashtable<>();
    }



    public void put(String key, City lab) {
        map.put(key, lab);
    }

    public void remove(String key) {
        map.remove(key);
    }

    public City read(String key) {
        return map.get(key);
    }

    public int length() {
        return map.size();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (String key : map.keySet()) {
            City city = map.get(key);
            result.append(key).append(" = ").append(city).append("\n");
        }
        return result.toString();
    }

    public void clear() {
        map.clear();
    }

}
