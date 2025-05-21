package common.collection;

/**
 * Класс правительства
 */

public enum Government {
    KLEPTOCRACY,
    MERITOCRACY,
    REPUBLIC,
    TELLUROCRACY,
    JUNTA;

    /**
     * Возвращает все типы правительств в строковом виде
     * @return все типы правительств в строковом виде
     */

    public static String names() {
        StringBuilder nameList = new StringBuilder();
        for (var governmentType : values()) {
            nameList.append(governmentType.name()).append(", ");
        }
        return nameList.substring(0, nameList.length() - 2);
    }
}
