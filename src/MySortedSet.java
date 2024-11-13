import java.util.*;

// This is a skeleton implementation of a sorted set, which our SkipListSet will look like
public class MySortedSet<T> implements SortedSet<T> {
    // The comparator used for T
    private final Comparator<? super T> comparator;

    // The elements in the set themselves
    private final List<T> elements;

    public MySortedSet(Comparator<? super T> comparator) {
        this.comparator = comparator;

        // I assume this'd be where we handle our set as a SkipList
        this.elements = new ArrayList<T>();
    }

    // Helper method; it doesn't override anything
    private int compare(T e1, T e2) {
        // If no comparator, try to cast e1 into a comparable (this might fail); otherwise, use the comparator
        Comparable<? super T> e1Comparable = (Comparable<? super T>) e1;
        return comparator == null ? e1Comparable.compareTo(e2) : this.comparator.compare(e1, e2);
    }

    // These are methods we need to implement from SortedSet!

    // Returns the comparator for the SkipList's elements; is just a getter
    public Comparator<? super T> comparator() {
        return comparator;
    }

    // Return the iterator for this set's elements
    // Is also just a getter
    // I think we'll make our own for the skiplist
    @Override
    public Iterator<T> iterator() {
        return this.elements.iterator();
    }

    // Add; this is what would add to our skiplist; the logic here is placeholder
    @Override
    public boolean add(T e) {
        // Avoid dupes
        if (contains(e)) {
            return false;
        }

        // Find pos to add
        int pos = Collections.binarySearch(elements, e, this::compare);
        if (pos < 0) {
            pos = -pos - 1;
        }

        elements.add(pos, e);

        return true;
    }

    // Remove: This is what would remove from our list; handle this like a skiplist as well
    // Note that the param. must be an object for some reason
    @Override
    public boolean remove(Object o) {
        // Find deletion position
        int pos = Collections.binarySearch(elements, (T) o, this::compare);
        if (pos >= 0) {
            elements.remove(pos);
            return true;
        }

        return false;
    }

    // Gives us back a SortedSet of this same type, but only between two elements
    @Override
    public MySortedSet<T> subSet(T fromElement, T toElement) {
        int start = Collections.binarySearch(elements, fromElement, this::compare);
        int end = Collections.binarySearch(elements, toElement, this::compare);

        start = start < 0 ? -start - 1 : start;
        end = end < 0 ? -end - 1 : end;

        return (MySortedSet<T>) elements.subList(start, end);
    }

    // Head set; return subset of everything up to an element, excluding it
    @Override
    public MySortedSet<T> headSet(T toElement) {
        int end = Collections.binarySearch(elements, toElement, this::compare);
        end = end < 0 ? -end - 1 : end;
        return (MySortedSet<T>) elements.subList(0, end);
    }

    // Tail set: return subset of everything including and after a certain element
    @Override
    public MySortedSet<T> tailSet(T fromElement) {
        int start = Collections.binarySearch(elements, fromElement, this::compare);
        start = start < 0 ? -start - 1 : start;
        return (MySortedSet<T>) elements.subList(start, elements.size());
    }

    // First: Return the first element in the set
    @Override
    public T first() {
        if (isEmpty()) throw new NoSuchElementException("Set is empty!");
        return elements.get(0);
    }

    // Last: Return final element in the set
    @Override
    public T last() {
        if (isEmpty()) throw new NoSuchElementException("Set is empty!");
        return elements.get(elements.size() - 1);
    }

    // Size: get amount of elements in sorted set
    @Override
    public int size() {
        return elements.size();
    }

    // Empty: is this sorted set empty?
    @Override
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    // Check if o exists in this first; might want to cast to T
    @Override
    public boolean contains(Object o) {
        return elements.contains((T) o);
    }

    // Convert skip list to a new object array
    @Override
    public Object[] toArray() {
        return elements.toArray();
    }

    // Same thing, but paste the elements into T[] a instead
    // If no space in a, a new array of T is created and returned
    // This version is more type-safe
    // [Still a bit confused on this, come back to it later]
    @Override
    public <T> T[] toArray(T[] a) {
        return elements.toArray(a);
    }

    // Checks if skip list contains everything in c
    @Override
    public boolean containsAll(Collection<?> c) {
        return elements.containsAll(c);
    }

    // Adds a set of things to our skiplist
    // In the placeholder logic, we check if add returns true, meaning that the element didn't exist in the list before and thus was added
    // If c contains at least 1 new element, addAll will return true
    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean modified = false;
        for (T e : c) {
            if (add(e)) modified = true;
        }
        return modified;
    }

    // Remove all elements from the skip list except those in c
    @Override
    public boolean retainAll(Collection<?> c) {
        return elements.retainAll(c);
    }

    // Removes a set of elements from the skip list
    @Override
    public boolean removeAll(Collection<?> c) {
        return elements.removeAll(c);
    }

    // Clear everything in the skip list
    @Override
    public void clear() {
        elements.clear();
    }
}