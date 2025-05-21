package common.collection;

import common.utility.Validatable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Класс координат
 */

public class Coordinates implements Serializable, Validatable {
    private Float x; //Поле не может быть null
    private float y; //Максимальное значение поля: 986

    /**
     * Коснтруктор для создания объекта Coordinates
     * @param x - Координата x
     * @param y - Координата y
     */

    public Coordinates(Float x, Float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Возвращает координату x
     * @return координата x
     */

    public Float getX() {
        return x;
    }

    /**
     * Устанавливает координату x
     */

    public void setX(Float x) {
        this.x = x;
    }

    /**
     * Возвращает координату y
     * @return координата y
     */

    public float getY() {
        return y;
    }

    /**
     * Устанавливает координату y
     */

    public void setY(Float y) {
        this.y = y;
    }

    /**
     * Метод сравнивающий координаты
     * @param o объект для сравнения
     * @return 0 - равны, 1 - объект у которого вызвается метод имеет большие координаты, -1 - с которым сравнивается текущий объект, имеет большие координаты
     */

    public int compareTo(Coordinates o) {
        if (this.x == null && o.x == null) {
            return 0;
        }
        if (o == null) {
            return 1;
        }
        if (this.x == null) {
            return -1;
        }
        int result = Float.compare(this.x, o.x);
        if (result == 0)
            return Double.compare(this.y, o.y);
        return result;
    }

    /**
     * Возвращает хэш код объекта координат
     * @return хэш код объекта
     */

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    /**
     * Возвращает строковое представление координат
     * @return строковое представление координат
     */

    @Override
    public String toString() {
        return "Coordinates: " +
                "x = " + x +
                ", y = " + y;
    }

    /**
     * Проверяет координаты на валидность
     * @return true если все координаты валидны, false в противном случае
     */

    @Override
    public boolean validate() {
        if (x == null) return false;
        return y <= 986;
    }

}
