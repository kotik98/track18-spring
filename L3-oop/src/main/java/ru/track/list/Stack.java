package ru.track.list;

import java.util.NoSuchElementException;

interface Stack {
    void push(int value); // положить значение наверх стека
    int pop() throws NoSuchElementException; // вытащить верхнее значение со стека
}