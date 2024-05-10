package LFU;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LFUCache<K, V> {
    private Map<K, Node<K, V>> cache;
    private Map<Integer, Set<Node<K, V>>> frequencyMap;
    private int capacity;
    private int minFrequency;

    LFUCache(int capacity) {
        this.capacity = capacity;
        cache = new HashMap<>();
        frequencyMap = new HashMap<>();
        minFrequency = 0;
    }

    private static class Node<K, V> {
        K key;
        V value;
        int frequency;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.frequency = 1; // Newly added node has frequency 1
        }
    }

    public V get(K key) {
        if (!cache.containsKey(key)) {
            return null;
        }
        Node<K, V> node = cache.get(key);
        updateFrequency(node);
        return node.value;
    }

    public void put(K key, V value) {
        if (capacity == 0) {
            return;
        }
        if (cache.containsKey(key)) {
            Node<K, V> node = cache.get(key);
            node.value = value;
            updateFrequency(node);
        } else {
            if (cache.size() >= capacity) {
                evict();
            }
            Node<K, V> newNode = new Node<>(key, value);
            cache.put(key, newNode);
            addToFrequencyMap(newNode);
            minFrequency = 1; // New node added, so minFrequency becomes 1
        }
    }

    private void updateFrequency(Node<K, V> node) {
        int frequency = node.frequency;
        Set<Node<K, V>> set = frequencyMap.get(frequency);
        set.remove(node);
        if (set.isEmpty() && frequency == minFrequency) {
            minFrequency++;
        }
        node.frequency++;
        addToFrequencyMap(node);
    }

    private void addToFrequencyMap(Node<K, V> node) {
        int frequency = node.frequency;
        Set<Node<K, V>> set = frequencyMap.computeIfAbsent(frequency, k -> new HashSet<>());
        set.add(node);
    }

    private void evict() {
        Set<Node<K, V>> set = frequencyMap.get(minFrequency);
        Node<K, V> evictNode = set.iterator().next();
        set.remove(evictNode);
        cache.remove(evictNode.key);
    }
}
