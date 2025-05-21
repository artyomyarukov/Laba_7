package common.collection;

import java.io.Serializable;

/**
 * Класс человека
 */

public class Human implements Comparable<Human>, Serializable {
    private java.time.LocalDate birthday;

    /**
     * Конструктор для создания объекта
     * @param birthday дата рождения
     */

    public Human(java.time.LocalDate birthday) {
        this.birthday = birthday;
    }

    /**
     * Геттер, чтобы получать дату рождения
     * @return дата рождения
     */

    public java.time.LocalDate getBirthday() {
        return birthday;
    }

    /**
     * Сеттер, чтобы устанавливать дату рождения
     * @param birthday дата рождения
     */

    public void setBirthday(java.time.LocalDate birthday) {
        this.birthday = birthday;
    }
    /**
     * Сравнивает текущий объект Human с другим объектом Human по дате рождения.
     * @param o объект Human для сравнения
     * @return -1, если текущая дата меньше,
     *         1, если текущая дата больше,
     *         0, если даты равны или обе null
     * @throws NullPointerException если параметр o равен null
     */
    @Override
    public int compareTo(Human o) {
        if (this.birthday == null && o.birthday == null) {
            return 0;
        }
        if (o == null) {
            return 1;
        }
        if (this.birthday == null) {
            return -1;
        }
        return this.birthday.compareTo(o.birthday);
    }

    /**
     * Возвращает строковое представление объекта Human.
     * @return строковое представление даты рождения или сообщение об ее отсутствии
     */

    @Override
    public String toString() {
        if (birthday != null) {
            return birthday.toString();
        } else {
            return ("Дата рождения губернатора не указана.");
        }
    }
}
