import java.util.ArrayList;

public class MyHashMap {

    private static class Node {
        String key;
        Object value;
        Node next;

        Node(String key, Object value, Node next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    private static final int INITIAL_CAPACITY = 1024; // High initial capacity to reduce rehashing
    private static final double LOAD_FACTOR = 0.75;

    private Node[] table;
    private int size;
    private int threshold;

    public MyHashMap() {
        this.table = new Node[INITIAL_CAPACITY];
        this.threshold = (int) (INITIAL_CAPACITY * LOAD_FACTOR);
        this.size = 0;
    }

    private int indexFor(String key, int length) {
        int h = key.hashCode();
        return (h & 0x7fffffff) % length; // Handle negative hash codes
    }

    public boolean containsKey(String key) {
        if (key == null) return false;
        int idx = indexFor(key, table.length);
        Node curr = table[idx];
        while (curr != null) {
            if (curr.key.equals(key)) {
                return true;
            }
            curr = curr.next;
        }
        return false;
    }

    public Object get(String key) {
        if (key == null) return null;
        int idx = indexFor(key, table.length);
        Node curr = table[idx];
        while (curr != null) {
            if (curr.key.equals(key)) {
                return curr.value;
            }
            curr = curr.next;
        }
        return null;
    }

    public void put(String key, Object value) {
        if (key == null) return;

        int idx = indexFor(key, table.length);
        Node curr = table[idx];

        // Update value if key exists
        while (curr != null) {
            if (curr.key.equals(key)) {
                curr.value = value;
                return;
            }
            curr = curr.next;
        }

        // Insert new node
        Node newNode = new Node(key, value, table[idx]);
        table[idx] = newNode;
        size++;

        if (size >= threshold) {
            resize();
        }
    }

    public void remove(String key) {
        if (key == null) return;
        int idx = indexFor(key, table.length);
        Node curr = table[idx];
        Node prev = null;

        while (curr != null) {
            if (curr.key.equals(key)) {
                if (prev == null) {
                    table[idx] = curr.next;
                } else {
                    prev.next = curr.next;
                }
                size--;
                return;
            }
            prev = curr;
            curr = curr.next;
        }
    }

    public ArrayList<Object> values() {
        ArrayList<Object> list = new ArrayList<>();
        for (int i = 0; i < table.length; i++) {
            Node curr = table[i];
            while (curr != null) {
                list.add(curr.value);
                curr = curr.next;
            }
        }
        return list;
    }

    public int size() {
        return size;
    }

    private void resize() {
        int newCapacity = table.length * 2;
        Node[] newTable = new Node[newCapacity];

        for (int i = 0; i < table.length; i++) {
            Node curr = table[i];
            while (curr != null) {
                Node next = curr.next;
                int idx = indexFor(curr.key, newCapacity);
                curr.next = newTable[idx];
                newTable[idx] = curr;
                curr = next;
            }
        }

        table = newTable;
        threshold = (int) (newCapacity * LOAD_FACTOR);
    }
}