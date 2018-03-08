package ru.track.list;

import java.util.NoSuchElementException;

/**
 * Должен наследовать List
 *
 * Должен иметь 2 конструктора
 * - без аргументов - создает внутренний массив дефолтного размера на ваш выбор
 * - с аргументом - начальный размер массива
 */
public class MyArrayList extends List {
    private int size;
    private int capacity;
    int arr[];
    public MyArrayList() {
        this.size = 50;
        this.capacity = 0;
        arr = new int[50];
    }

    public MyArrayList(int capacity) {
        if (capacity > 0) {
            this.size = capacity;
            this.capacity = 0;
            arr = new int[capacity];
        } else {
            this.size = 50;
            this.capacity = 0;
            arr = new int[50];
        }
    }

    @Override
    void add(int item) {
        if (capacity >= size){
            int[] temp = new int[size];
            System.arraycopy(arr, 0, temp, 0, size);
            arr = new int[size * 2];
            System.arraycopy(temp, 0, arr, 0, size);
            size *= 2;
        }
        arr[capacity] = item;
        capacity++;
        return;
    }

    @Override
    int remove(int idx) throws NoSuchElementException {
        if (idx >= capacity){
            throw new NoSuchElementException();
        }
        int el = arr[idx];
        System.arraycopy(arr, idx + 1, arr, idx, capacity - idx - 1);
        capacity--;
        return el;
    }

    @Override
    int get(int idx) throws NoSuchElementException {
        if (idx < capacity) {
            return arr[idx];
        }
        throw new NoSuchElementException();
    }

    @Override
    int size() {
        return capacity;
    }
}
