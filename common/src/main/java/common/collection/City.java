package common.collection;


import common.collection.Coordinates;
import common.utility.Element;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;



/**
 * Класс представляющий Город
 */

public class City extends Element implements Serializable {
    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.time.LocalDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Long area; //Значение поля должно быть больше 0, Поле не может быть null
    private int population; //Значение поля должно быть больше 0
    private Integer metersAboveSeaLevel;
    private Long telephoneCode; //Поле может быть null, Значение поля должно быть больше 0, Максимальное значение поля: 100000
    private long agglomeration;
    private Government government; //Поле не может быть null
    private Human governor; //Поле может быть null

    /**
     * Основной конструктор для создания объекта City.
     * @param id уникальный идентификатор города (не null, >0)
     * @param name название города (не null, не пустое)
     * @param coordinates координаты города (не null)
     * @param creationDate дата создания записи (не null)
     * @param area площадь города (не null, >0)
     * @param population население города (>0)
     * @param metersAboveSeaLevel высота над уровнем моря
     * @param telephoneCode телефонный код города (может быть null, >0, <=100000)
     * @param agglomeration размер агломерации
     * @param government форма правления (не null)
     * @param governor губернатор города (может быть null)
     */

    public City(Long id, String name, Coordinates coordinates, LocalDateTime creationDate, Long area, int population, Integer metersAboveSeaLevel, Long telephoneCode, long agglomeration, Government government, Human governor) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.area = area;
        this.population = population;
        this.metersAboveSeaLevel = metersAboveSeaLevel;
        this.telephoneCode = telephoneCode;
        this.agglomeration = agglomeration;
        this.government = government;
        this.governor = governor;
    }

    /**
     * Альтернативный конструктор, автоматически устанавливающий текущую дату создания.
     *
     * @param id уникальный идентификатор города
     * @param name название города
     * @param coordinates координаты города
     * @param area площадь города
     * @param population население города
     * @param metersAboveSeaLevel высота над уровнем моря
     * @param telephoneCode телефонный код города
     * @param agglomeration размер агломерации
     * @param government форма правления
     * @param governor губернатор города
     */

    public City(Long id, String name, Coordinates coordinates, Long area, int population, Integer metersAboveSeaLevel, Long telephoneCode, long agglomeration, Government government, Human governor) {
        this(id, name, coordinates, LocalDateTime.now(), area, population, metersAboveSeaLevel, telephoneCode, agglomeration, government, governor);
    }

    /**
     * Возвращает идентификатор города.
     * @return идентификатор города
     */

    public Long getId() {
        return id;
    }

    /**
     * Устанавливает идентификатор города.
     * @param id новый идентификатор (не null, >0)
     */

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Возвращает название города.
     * @return название города
     */

    public String getName() {
        return name;
    }

    /**
     * Устанавливает название города.
     * @param name новое название (не null, не пустое)
     */

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Возвращает координаты города.
     * @return объект Coordinates
     */

    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     * Устанавливает координаты города.
     * @param coordinates новые координаты (не null)
     */

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * Возвращает дату создания записи о городе.
     * @return дата создания
     */

    public java.time.LocalDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * Устанавливает дату создания записи.
     * @param creationDate новая дата создания (не null)
     */

    public void setCreationDate(java.time.LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Возвращает площадь города.
     * @return площадь города
     */

    public Long getArea() {
        return area;
    }

    /**
     * Устанавливает площадь города.
     * @param area новая площадь (не null, >0)
     */

    public void setArea(Long area) {
        this.area = area;
    }

    /**
     * Возвращает население города.
     * @return численность населения
     */

    public int getPopulation() {
        return population;
    }

    /**
     * Устанавливает население города.
     * @param population новое значение населения (>0)
     */

    public void setPopulation(int population) {
        this.population = population;
    }

    /**
     * Возвращает высоту над уровнем моря.
     * @return высота в метрах (может быть null)
     */

    public Integer getMetersAboveSeaLevel() {
        return metersAboveSeaLevel;
    }

    /**
     * Возвращает телефонный код города.
     * @return телефонный код (может быть null)
     */

    public Long getTelephoneCode() {
        return telephoneCode;
    }

    /**
     * Устанавливает телефонный код города.
     * @param telephoneCode новый телефонный код (может быть null, >0, <=100000)
     */

    public void setTelephoneCode(Long telephoneCode) {
        this.telephoneCode = telephoneCode;
    }

    /**
     * Возвращает размер агломерации.
     * @return размер агломерации
     */

    public long getAgglomeration() {
        return agglomeration;
    }


    /**
     * Устанавливает размер агломерации.
     * @param agglomeration новый размер агломерации
     */

    public void setAgglomeration(long agglomeration) {
        this.agglomeration = agglomeration;
    }

    /**
     * Возвращает форму правления в городе.
     * @return форма правления
     */

    public Government getGovernment() {
        return government;
    }

    /**
     * Устанавливает форму правления.
     * @param government новая форма правления (не null)
     */

    public void setGovernment(Government government) {
        this.government = government;
    }

    /**
     * Возвращает губернатора города.
     * @return объект Human (может быть null)
     */

    public Human getGovernor() {
        return governor;
    }

    /**
     * Устанавливает губернатора города.
     * @param governor новый губернатор (может быть null)
     */

    public void setGovernor(Human governor) {
        this.governor = governor;
    }

    /**
     * Проверяет корректность полей объекта City.
     * @return true если все поля валидны, false в противном случае
     */

    @Override
    public boolean validate() {
        if (id == null || id <= 0) return false;
        if (name == null || name.isEmpty()) return false;
        if (coordinates == null || !coordinates.validate()) return false;
        if (creationDate == null) return false;
        if (area == null || area <= 0) return false;
        if (population <= 0) return false;
        if (telephoneCode != null && (telephoneCode <= 0 || telephoneCode >= 100000)) return false;
        if (government == null) return false;

        return true;
    }

    public static class Builder {
        private Long id;
        private String name;
        private Coordinates coordinates;
        private LocalDateTime creationDate = LocalDateTime.now();
        private Long area;
        private int population;
        private Integer metersAboveSeaLevel;
        private Long telephoneCode;
        private long agglomeration;
        private Government government;
        private Human governor;

        public Builder() {}

        public Builder setId(Long id) {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID должен быть положительным числом");
            }
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Название города не может быть пустым");
            }
            this.name = name;
            return this;
        }

        /**
         * Устанавливает координату X
         * @param x целое число (не null)
         */
        public Builder setCoordinateX(Float x) {
            if (x == null) {
                throw new IllegalArgumentException("Координата X не может быть null");
            }
            this.coordinates.setX(x);
            return this;
        }

        public Builder setCoordinateY(Float y) {
            if (y == null) {
                throw new IllegalArgumentException("Координата Y не может быть null");
            }
            if (y > 986) {
                throw new IllegalArgumentException("Координата Y должна быть ≤ 986");
            }
            this.coordinates.setY(y);
            return this;
        }

        // Альтернативный метод для установки сразу обеих координат
        public Builder setCoordinates(Float x, Float y) {
            return this.setCoordinateX(x).setCoordinateY(y);
        }


        public Builder setCreationDate(LocalDateTime creationDate) {
            if (creationDate == null) {
                throw new IllegalArgumentException("Дата создания не может быть null");
            }
            this.creationDate = creationDate;
            return this;
        }

        public Builder setArea(Long area) {
            if (area == null || area <= 0) {
                throw new IllegalArgumentException("Площадь должна быть положительным числом");
            }
            this.area = area;
            return this;
        }

        public Builder setPopulation(int population) {
            if (population <= 0) {
                throw new IllegalArgumentException("Население должно быть положительным числом");
            }
            this.population = population;
            return this;
        }

        public Builder setMetersAboveSeaLevel(Integer metersAboveSeaLevel) {
            this.metersAboveSeaLevel = metersAboveSeaLevel;
            return this;
        }

        public Builder setTelephoneCode(Long telephoneCode) {
            if (telephoneCode != null && (telephoneCode <= 0 || telephoneCode > 100000)) {
                throw new IllegalArgumentException("Телефонный код должен быть от 1 до 100000");
            }
            this.telephoneCode = telephoneCode;
            return this;
        }

        public Builder setAgglomeration(long agglomeration) {
            this.agglomeration = agglomeration;
            return this;
        }

        public Builder setGovernment(Government government) {
            if (government == null) {
                throw new IllegalArgumentException("Форма правления не может быть null");
            }
            this.government = government;
            return this;
        }

        public Builder setGovernor(Human governor) {
            this.governor = governor;
            return this;
        }

        public City build() {
            // Проверка обязательных полей
            if (id == null) throw new IllegalStateException("ID не установлен");
            if (name == null) throw new IllegalStateException("Название не установлено");
            if (coordinates == null) throw new IllegalStateException("Координаты не установлены");
            if (area == null) throw new IllegalStateException("Площадь не установлена");
            if (government == null) throw new IllegalStateException("Форма правления не установлена");

            return new City(
                    id,
                    name,
                    coordinates,
                    creationDate,
                    area,
                    population,
                    metersAboveSeaLevel,
                    telephoneCode,
                    agglomeration,
                    government,
                    governor
            );
        }
    }





    /**
     * Сравнивает текущий город с другим элементом по идентификатору.
     * @param o элемент для сравнения
     * @return разница между идентификаторами
     */

    @Override
    public int compareTo(Element o) {
        return (int) (this.id - o.getId());
    }

    /**
     * Возвращает строковое представление объекта City.
     * @return строковое описание города
     */


    @Override
    public String toString() {
        String t_code;
        String s_governor;
        if (telephoneCode == null) {
            t_code = "не указан";
        } else {
            t_code = telephoneCode.toString();
        }
        if (governor == null) {
            s_governor = "губренатор не указан";
        } else {
            s_governor = governor.toString();
        }

        return "City [id=" + id + ", name=" + name + ", coordinates=" + coordinates + ", creationDate = " + creationDate.format(DateTimeFormatter.ISO_DATE_TIME) + ", area=" + area +
                ", population=" + population + ", metersAboveSeaLevel=" + metersAboveSeaLevel + ", telephoneCode=" + t_code +
                ", agglomeration=" + agglomeration + ", government=" + government + ", governor=" + s_governor + "]";
    }

    /**
     * Проверяет равенство объектов City по идентификатору.
     * @param o объект для сравнения
     * @return true если идентификаторы совпадают, false в противном случае
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        City that = (City) o;
        return Objects.equals(id, that.id);
    }

    /**
     * Возвращает хэш-код объекта City на основе всех полей.
     * @return хэш-код объекта
     */

    @Override
    public int hashCode() {
        return Objects.hash(id, name, coordinates, creationDate, area, population, metersAboveSeaLevel, telephoneCode, agglomeration, government, governor);
    }


}
