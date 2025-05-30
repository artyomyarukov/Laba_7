package common.utility;

/**
 * Абстрактный класс, представляющий элемент, который можно сравнивать и проверять на валидность.
 */

public abstract class Element implements Comparable<Element>, Validatable {
    abstract public Long getId();
}