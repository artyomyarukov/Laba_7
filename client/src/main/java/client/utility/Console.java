package client.utility;

import java.util.Scanner;

/**
 * Консоль для ввода команд и вывода результата
 */
public interface Console {
    /**
     * Выводит объект в стандартный поток вывода
     * @param obj объект для вывода
     */
    void print(Object obj);

    /**
     * Выводит объект в стандартный поток вывода с переводом строки
     * @param obj объект для вывода
     */
    void println(Object obj);

    /**
     * Считывает строку из стандартного потока ввода
     * @return считанная строка
     */
    String readln();


    /**
     * Проверяет можно ли считать файл
     * @return считанная строка
     */
    boolean isCanReadln();

    void printError(Object obj);

    /**
     * Выводит два объекта в виде таблицы.
     * @param obj1 первый объект
     * @param obj2 второй объект
     */

    void printTable(Object obj1, Object obj2);

    /**
     * Выводит приглашение к вводу
     */

    void prompt();

    /**
     * Возвращает приглашение к вводу
     */

    String getPrompt();

    /**
     * Устанавливает чтение из файла
     * @param obj
     */

    void selectFileScanner(Scanner obj);

    /**
     * Возвращает чтение обратно в консольный режим
     */

    void selectConsoleScanner();
}