package common.utility;



import common.collection.City;
import common.collection.Government;
import common.collection.Human;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CityItemAssembler {
    private static final int FIELDS_COUNT = 10;
    private static final List<String> allPrompts = new ArrayList<>();
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    static {
        allPrompts.add("Введите название города (не может быть пустым)");
        allPrompts.add("Введите координату X (float, не null)");
        allPrompts.add("Введите координату Y (float, не null, макс. 986)");
        allPrompts.add("Введите площадь города (дробное число > 0)");
        allPrompts.add("Введите население (целое число > 0)");
        allPrompts.add("Введите высоту над уровнем моря (целое число)");
        allPrompts.add("Введите телефонный код (null или число 1-100000)");
        allPrompts.add("Введите размер агломерации (null или дробное число)");
        allPrompts.add("Выберите тип правительства: " + Arrays.toString(Government.values()));
        allPrompts.add("Введите дату рождения губернатора (yyyy-MM-ddTHH:mm:ss или null)");
    }

    private int fieldNumber = 0;
    private final boolean interactive;
    private final City.Builder builder;

    public CityItemAssembler(boolean interactive) {
        this.interactive = interactive;
        this.builder = new City.Builder();
        promptCurrentField();
    }

    public void addNextLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            handleNullInput();
            return;
        }

        try {
            switch (fieldNumber) {
                case 0: // name
                    builder.setName(line);
                    break;
                case 1: // coordinate x
                    Float x = Float.parseFloat(line);
                    builder.setCoordinateX(x);
                    break;
                case 2: // coordinate y
                    float y = Float.parseFloat(line);
                    builder.setCoordinateY(y); // Теперь используем отдельный метод
                    break;
                case 3: // area
                    Long area = Long.parseLong(line);
                    if (area <= 0) throw new IllegalArgumentException("Площадь должна быть > 0");
                    builder.setArea(area);
                    break;
                case 4: // population
                    int population = Integer.parseInt(line);
                    if (population <= 0) throw new IllegalArgumentException("Население должно быть > 0");
                    builder.setPopulation(population);
                    break;
                case 5: // metersAboveSeaLevel
                    builder.setMetersAboveSeaLevel(Integer.parseInt(line));
                    break;
                case 6: // telephoneCode
                    if ("null".equalsIgnoreCase(line)) {
                        builder.setTelephoneCode(null);
                    } else {
                        long code = Long.parseLong(line);
                        if (code <= 0 || code > 100000) {
                            throw new IllegalArgumentException("Телефонный код должен быть 1-100000");
                        }
                        builder.setTelephoneCode(code);
                    }
                    break;
                case 7: // agglomeration

                    builder.setAgglomeration(Long.parseLong(line));

                    break;
                case 8: // government
                    builder.setGovernment(Government.valueOf(line));
                    break;
                case 9: // governor birthday
                    if ("null".equalsIgnoreCase(line)) {
                        builder.setGovernor(null);
                    } else {
                        LocalDate birthday = LocalDate.parse(line, DATE_FORMATTER);
                        builder.setGovernor(new Human(birthday));
                    }
                    break;
            }
            fieldNumber++;
            promptCurrentField();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Неверный числовой формат", e);
        } catch (IllegalArgumentException | DateTimeParseException e) {
            throw new IllegalArgumentException("Ошибка ввода: " + e.getMessage(), e);
        }
    }

    private void handleNullInput() {
        if (fieldNumber == 6 || fieldNumber == 9) {
            // Поля, которые могут быть null
            switch (fieldNumber) {
                case 6:
                    builder.setTelephoneCode(null);
                    break;

                case 9:
                    builder.setGovernor(null);
                    break;
            }
            fieldNumber++;
            promptCurrentField();
        } else {
            throw new IllegalArgumentException("Поле не может быть null или пустым");
        }
    }

    private void promptCurrentField() {
        if (interactive && fieldNumber < FIELDS_COUNT) {
            System.out.println(allPrompts.get(fieldNumber));
        }
    }

    public City getCity() {
        if (fieldNumber < FIELDS_COUNT) {
            throw new IllegalStateException("Не все поля заполнены");
        }
        return builder.build();
    }

    public boolean isFinished() {
        return fieldNumber >= FIELDS_COUNT;
    }
}