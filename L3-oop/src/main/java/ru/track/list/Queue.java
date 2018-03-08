package ru.track.list;

import java.util.NoSuchElementException;

interface Queue {
    void enqueue(int value); // поместить элемент в очередь
    int dequeue() throws NoSuchElementException; // вытащить первый элемент из очереди
}
