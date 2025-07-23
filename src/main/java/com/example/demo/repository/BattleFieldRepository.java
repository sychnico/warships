package com.example.demo.repository;

import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Repository
public class BattleFieldRepository {
    private static final HashMap<String, int[][]> fields = new HashMap<>();
    private static final int FIELD_SIZE = 10;

    /**
     * Сохраняет игровое поле для указанного пользователя
     * @param userName имя игрока
     * @param field игровое поле (массив 10x10)
     * @throws IllegalArgumentException если поле не соответствует размеру 10x10
     */
    public void saveField(String userName, int[][] field) {
        if (field == null || field.length != FIELD_SIZE || field[0].length != FIELD_SIZE) {
            throw new IllegalArgumentException("Field must be 10x10 array");
        }
        fields.put(userName, field);
    }

    /**
     * Получает значение клетки поля игрока
     * @param userName имя игрока
     * @param x координата X (0-9)
     * @param y координата Y (0-9)
     * @return значение клетки
     * @throws IllegalArgumentException если координаты выходят за пределы поля
     * @throws IllegalStateException если поле для игрока не найдено
     */
    public int get(String userName, int x, int y) {
        validateCoordinates(x, y);
        int[][] field = fields.get(userName);
        if (field == null) {
            throw new IllegalStateException("Field not found for user: " + userName);
        }
        return field[y][x]; // Обратите внимание на порядок y и x (строка, затем столбец)
    }

    /**
     * Устанавливает значение клетки поля игрока
     * @param userName имя игрока
     * @param x координата X (0-9)
     * @param y координата Y (0-9)
     * @param value значение для установки
     * @throws IllegalArgumentException если координаты выходят за пределы поля
     * @throws IllegalStateException если поле для игрока не найдено
     */
    public void set(String userName, int x, int y, int value) {
        validateCoordinates(x, y);
        int[][] field = fields.get(userName);
        if (field == null) {
            throw new IllegalStateException("Field not found for user: " + userName);
        }
        field[y][x] = value; // Обратите внимание на порядок y и x (строка, затем столбец)
    }

    /**
     * Проверяет валидность координат
     * @param x координата X
     * @param y координата Y
     * @throws IllegalArgumentException если координаты невалидны
     */
    private void validateCoordinates(int x, int y) {
        if (x < 0 || x >= FIELD_SIZE || y < 0 || y >= FIELD_SIZE) {
            throw new IllegalArgumentException(
                    String.format("Coordinates (%d, %d) are out of bounds (0-%d)", x, y, FIELD_SIZE - 1)
            );
        }
    }

    /**
     * Получает копию всего поля игрока
     * @param userName имя игрока
     * @return копия игрового поля
     * @throws IllegalStateException если поле для игрока не найдено
     */
    public int[][] getFieldCopy(String userName) {
        int[][] field = fields.get(userName);
        if (field == null) {
            throw new IllegalStateException("Field not found for user: " + userName);
        }

        // Создаем глубокую копию массива
        int[][] copy = new int[FIELD_SIZE][FIELD_SIZE];
        for (int i = 0; i < FIELD_SIZE; i++) {
            System.arraycopy(field[i], 0, copy[i], 0, FIELD_SIZE);
        }
        return copy;
    }
}
