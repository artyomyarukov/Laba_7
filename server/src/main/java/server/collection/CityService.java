package server.collection;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;
import common.collection.City;
import lombok.AllArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;


/*
 Класс для управления коллекцией объектов
 */
public class CityService {
    private Storage storage;
    private final String filename;

    public CityService(HashMap<String, City> initialState, String filename) {
        this.storage = new Storage(initialState);
        this.filename = filename;
    }

    public Map<String, City> getWholeMap(){
        return storage.getMap();
    }


    public String put(String key, City city) {
        String warning = null;
        if (storage.getMap().containsKey(key)) {
            warning = "ПРЕДУПРЕЖДЕНИЕ: элемент с таким ключом уже существовал, он будет перезаписан, ключ = " + key;
        }
        storage.put(key, city);
        return warning;
    }
    public String getFileName(){
        return filename;
    }
    public void remove(String key) {
        storage.remove(key);
    }



    public int getCollectionSize() {
        return storage.length();
    }

    public void clear_collection() {
        storage.clear();
    }

    public String getCollectionAsXml() {
        // 1 Создаем и настраиваем XStream
        XStream xstream = new XStream(new DomDriver());

        // 2 Разрешаем все типы
        xstream.addPermission(AnyTypePermission.ANY);

        // 3 Настраиваем алиасы для красивых имен тегов
        xstream.alias("storage", Map.class);       // Основной тег
        xstream.alias("city_key", Map.Entry.class);   // Элементы коллекции
        xstream.alias("city_elem", City.class);   // Объекты значений

        // 4 Преобразуем Map в XML
        return xstream.toXML(storage.getMap());
    }


    public void removeGreater(City elem) {
        storage.getMap().entrySet().stream()
                .filter(entry -> entry.getValue().compareTo(elem) > 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList())
                .forEach(storage::remove);
    }

    public static String sortMapAndStringify(Map<String, City> filteredMap) {
        return filteredMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.comparing(City::getName))) // Сортировка по названию
                .map(entry -> entry.getKey() + " = " + entry.getValue())
                .collect(Collectors.joining("\n"));
    }

    public String calculateSumOfMetersAboveSeaLevel() {
        int sum = storage.getMap().values().stream()
                .mapToInt(City::getMetersAboveSeaLevel)
                .sum();
         return String.format("%d м", sum);
    }

    public String getCitiesSortedByPopulation() {
        return storage.getMap().values().stream()
                .sorted(Comparator.comparingInt(City::getPopulation))  // Сортировка по населению
                .map(city -> city.getName() + " (население: " + city.getPopulation() + ")")
                .collect(Collectors.joining("\n"));
    }

    public String getAscending() {
        return storage.getMap().values().stream()
                .sorted(Comparator.comparingInt(City::getPopulation).reversed())  // Обратная сортировка по населению
                .map(city -> city.getName() + " (население: " + city.getPopulation() + ")")
                .collect(Collectors.joining("\n"));
    }


    public String getKeyById(Long id) {
        return storage.getMap().entrySet().stream()
                .filter(entry -> entry.getValue().getId().equals(id))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Нет элемента с таким id"));
    }

    //возвращает максэелемент
    public Optional<Map.Entry<String, City>> findMaxByArea() {
        return storage.getMap().entrySet().stream()
                .max(Comparator.comparingDouble(e -> e.getValue().getArea()));
    }

    public boolean isGreaterThanMax(City city) {
        Optional<Map.Entry<String, City>> maxEntry = findMaxByArea();

        return maxEntry
                .map(entry -> city.getArea() > entry.getValue().getArea())
                .orElse(true); // Если коллекция пуста, новый элемент считается "больше"
    }





    public void updateById(Long id, City city) {
        String key = getKeyById(id);
        city.setId(id);
        storage.put(key, city);
    }
    public String sortedByAreaCollection() {
         return storage.getMap().values().stream()
                .sorted(Comparator.comparingDouble(City::getArea))  // Сортировка по площади
                .map(city -> city.getName() + " (население: " + city.getArea() + ")")
                .collect(Collectors.joining("\n"));
    }

    @AllArgsConstructor
    private static class CityEntry implements Comparable<CityEntry> {
        String key;
        City city;

        @Override
        public int compareTo(CityEntry labWorkEntry) {
            return this.city.getArea().compareTo(labWorkEntry.city.getArea());
        }
    }


}
