package ru.track.list;

import java.util.NoSuchElementException;

/**
 * Должен наследовать List
 * Односвязный список
 */
public class MyLinkedList extends List implements Queue, Stack {
    private int size;
    private Node tail;
    private Node head;
    /**
     * private - используется для сокрытия этого класса от других.
     * Класс доступен только изнутри того, где он объявлен
     * <p>
     * static - позволяет использовать Node без создания экземпляра внешнего класса
     */
    private static class Node {
        Node prev;
        Node next;
        int val;

        Node(Node prev, Node next, int val) {
            this.prev = prev;
            this.next = next;
            this.val = val;
        }
    }

    @Override
    void add(int item) {
        if (size == 0){
            Node node = new Node(null, null, item);
            tail = head = node;
            size++;
            return;
        }
        Node node = new Node(head, null, item);
        head.next = node;
        head = node;
        size++;
        return;
    }

    @Override
    int remove(int idx) throws NoSuchElementException {
        if (idx >= size || idx < 0){
            throw new NoSuchElementException();
        }
        Node node =  this.tail;
        for (int i = 0; i < idx; i++){
            node = node.next;
        }
        if (node == tail && node == head){
            tail = null;
            head =null;
            size--;
        } else if (node == tail){
            tail = tail.next;
            size--;
        } else if (node == head){
            head = head.prev;
            size--;
        } else {
            node.next.prev = node.prev;
            node.prev.next = node.next;
            size--;
        }
        return node.val;
    }

    @Override
    int get(int idx) throws NoSuchElementException {
        if (idx >= size){
            throw new NoSuchElementException();
        }
        Node node =  this.tail;
        for (int i = 0; i < idx; i++){
            node = node.next;
        }
        return node.val;
    }

    @Override
    int size() {
        return size;
    }

    public void enqueue(int value){
        if (size == 0){
            add(value);
            return;
        }
        Node node = new Node(null, tail, value);
        tail.prev = node;
        tail = node;
        size++;
        return;
    }

    public int dequeue() throws NoSuchElementException{
        return remove(size - 1);
    }

    public void push(int value){
        add(value);
    }

    public int pop() throws NoSuchElementException{
        return remove(size - 1);
    }
}
