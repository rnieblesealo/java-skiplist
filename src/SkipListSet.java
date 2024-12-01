import java.util.*;

public class SkipListSet<T> implements SortedSet<T> {
    public static class SkipListSetItem<T> {
        public SkipListSetItem<T> above;
        public SkipListSetItem<T> below;
        public SkipListSetItem<T> next;
        public SkipListSetItem<T> prev;

        public boolean isHead;
        public boolean isTail;

        public T key;

        /** Used to create regular items */
        public SkipListSetItem(T key){
            this.key = key;

            this.isHead = false;
            this.isTail = false;

            this.above = null;
            this.below = null;
            this.next = null;
            this.prev = null;
        }

        /** Used to create empty/sentinel item */
        public SkipListSetItem(){
            this.key = null;

            this.isHead = false;
            this.isTail = true;

            this.above = null;
            this.below = null;
            this.next = null;
            this.prev = null;
        }
    }

    public static class SkipListSetIterator<T> implements Iterator<T> {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public T next() {
            return null;
        }

        @Override
        public void remove() {
            Iterator.super.remove();
        }
    }

    private SkipListSetItem<T> head;
    private SkipListSetItem<T> tail;

    private int height; // Height of list
    private int size; // Amount of unique elements in list

    public Random random = new Random();

    private final Comparator<? super T> comparator;

    public SkipListSet(Comparator<? super T> comparator){
        size = 0;

        // Assign the comparator
        this.comparator = comparator;

        // Initialize sentinel items
        head = new SkipListSetItem<>();
        tail = new SkipListSetItem<>();

        // Mark them as head and tail
        head.isHead = true;
        tail.isTail = true;

        // Make the list wrap around (?)
        head.next = tail;
        tail.prev = head;
    }

    /** Helper method to compare list elements */
    private int compare(T e1, T e2){
        // Try to cast e1 to a comparable to use if there's no available comparator
        Comparable<? super T> e1AsComparable = (Comparable<? super T>) e1;

        if (this.comparator == null)
            return e1AsComparable.compareTo(e2);
        else
            return this.comparator.compare(e1, e2);
    }

    /** Returns the comparator */
    public Comparator<? super T> comparator(){
        return comparator;
    }

    /** Return iterator */
    @Override
    public Iterator<T> iterator(){
        // TODO!
        return null;
    }

    /** Add to skip list */
    @Override
    public boolean add(T key){
        SkipListSetItem<T> position = search(key);
        SkipListSetItem<T> q;

        int heightOfNewKey = -1;

        // Don't modify the set on a duplicate key
        if (compare(position.key, key) == 0){
            return false;
        }

        // Perform insertion, TODO: logarithmic leveling!
        // Do this while we "flip" true
        do {
            // Height of key goes up
            heightOfNewKey++;

            // If it exceeds the current list height, increase it
            // TODO: Duplicate height instead of increasing it by 1
            increaseHeightIfTaller(heightOfNewKey);

            q = position;

            while (position.above == null){
                position = position.prev;
            }

            position = position.above;

            q = insertAfterAbove(position, q, key);
        } while (random.nextBoolean() == true);

        // Set was modified if we reach this point
        size++;
        return true;
    }

    /** AUX: Retrieves skip list item with specific key */
    private SkipListSetItem<T> search(T key){
        SkipListSetItem<T> curr = head;

        // While we aren't at the bottom level...
        while (curr.below != null){
            // Go to the level below (the topmost level is always empty)
            curr = curr.below;

            // Go across while the next key is greater and we're not about to wrap around
            while (compare(curr.key, curr.next.key) >= 0 && !curr.next.isTail){
                curr = curr.next;
            }

            // Eventually, we'll reach the bottom level, and thus our target
            // If the key doesn't exist, we'll get the key [at its position?]
        }

        // Give back that target
        return curr;
    }

    /** AUX: Add empty level if passed one exceeds the current height */
    private void increaseHeightIfTaller(int level){
        if (level >= height){
            height++;
            addEmptyLevel();
        }
    }

    /** AUX: Perform necessary relinks for insertion */
    private SkipListSetItem<T> insertAfterAbove (SkipListSetItem<T> position, SkipListSetItem<T> q, T key){
        SkipListSetItem<T> newItem = new SkipListSetItem<>(key);
        SkipListSetItem<T> itemBeforeNew = position.below.below;

        setBeforeAndAfterReferences(q, newItem);
        setAboveAndBelowReferences(position, key, newItem, itemBeforeNew);

        return newItem;
    }

    /** AUX: Redoes horizontal links for insertion */
    private void setBeforeAndAfterReferences(SkipListSetItem<T> q, SkipListSetItem<T> newItem){
        newItem.next = q.next;
        newItem.prev = q;

        q.next.prev = newItem;
        q.next = newItem;
    }

    /** AUX: Redoes vertical links for insertion */
    private void setAboveAndBelowReferences(SkipListSetItem<T> position, T key, SkipListSetItem<T> newItem, SkipListSetItem<T> itemBeforeNew){
        if (itemBeforeNew != null){
            while (true){
                if (compare(itemBeforeNew.next.key, key) != 0){
                    itemBeforeNew = itemBeforeNew.next;
                } else {
                    break;
                }
            }

            newItem.below = itemBeforeNew.next;
            itemBeforeNew.next.above = itemBeforeNew;
        }

        if (position != null){
            if (compare(position.next.key, key) == 0){
                newItem.above = position.next;
            }
        }
    }

    /** AUX: Redoes links so that we have a new empty level at the top */
    private void addEmptyLevel(){
        SkipListSetItem<T> newHead = new SkipListSetItem<>();
        SkipListSetItem<T> newTail = new SkipListSetItem<>();

        newHead.isHead = true;
        newTail.isTail = true;

        // New head's next is new tail; old head is below it
        newHead.next = newTail;
        newHead.below = head;

        // New tail's previous is the new head; its below is the old tail
        newTail.prev = newHead;
        newTail.below = tail;

        // Make references persistent for head and tail as well
        head.above = newHead;
        tail.above = newTail;

        // Update current head and tail
        head = newHead;
        tail = newTail;
    }

    /** Remove from skip list */
    @Override
    public boolean remove(Object o){
        T key = (T)o;

        SkipListSetItem<T> itemToBeRemoved = search(key);

        // Can't remove key that doesn't exist!
        if (compare(itemToBeRemoved.key, key) != 0){
            return false;
        }

        removeReferencesToItem(itemToBeRemoved);

        while (itemToBeRemoved != null){
            removeReferencesToItem(itemToBeRemoved);

            if (itemToBeRemoved.above != null){
                itemToBeRemoved = itemToBeRemoved.above;
            } else {
                break;
            }
        }

        // Set was modified at this point
        size--;
        return true;
    }

    /** AUX: Remove links to a target removal item from the skip list */
    private void removeReferencesToItem(SkipListSetItem<T> itemToBeRemoved){
        SkipListSetItem<T> afterItemToBeRemoved = itemToBeRemoved.next;
        SkipListSetItem<T> beforeItemToBeRemoved = itemToBeRemoved.prev;

        beforeItemToBeRemoved.next = afterItemToBeRemoved;
        afterItemToBeRemoved.prev = beforeItemToBeRemoved;
    }

    /** Return subset of skip list, including start and excluding end */
    @Override
    public SkipListSet<T> subSet(T fromElement, T toElement){
        // TODO!
        return null;
    }

    /** Return subset including start element and up to the end */
    @Override
    public SkipListSet<T> headSet(T fromElement){
       // TODO!
        return null;
    }

    /** Return subset up to and excluding an element */
    @Override
    public SkipListSet<T> tailSet(T toElement){
        // TODO!
        return null;
    }

    /** Return first element in skip list */
    @Override
    public T first(){
        // TODO!
        return null;
    }

    /** Return last element in skip list */
    @Override
    public T last(){
        // TODO!
        return null;
    }

    /** Return skip list size */
    @Override
    public int size(){
        return size;
    }

    /** Return whether skip list is empty */
    @Override
    public boolean isEmpty(){
        // TODO!
        return true;
    }

    /** Return whether o exists in the skip list */
    @Override
    public boolean contains(Object o){
        // TODO!
        return false;
    }

    /** Convert skip list to array of objects */
    @Override
    public Object[] toArray(){
        // TODO!
        return null;
    }

    /** Paste skip list elements to array of generic type */
    @Override
    public <T> T[] toArray(T[] a){
        // TODO!
        return null;
    }

    /** Check if skip list contains range of values */
    @Override
    public boolean containsAll(Collection<?> c){
        // TODO!
        return false;
    }

    /** Add a set of elements to the skip list */
    @Override
    public boolean addAll(Collection<? extends T> c){
        // TODO!
        return false;
    }

    /** Remove all elements from the skip list except those in c */
    @Override
    public boolean retainAll(Collection<?> c){
        // TODO!
        return false;
    }

    /** Remove a set of elements from skip list */
    @Override
    public boolean removeAll(Collection<?> c){
        // TODO!
        return false;
    }

    /** Clears the skip list */
    @Override
    public void clear(){
        // TODO!
    }

    /** Re-balances the skip list, randomizing all probabilities again by re-adding... */
    private void rebalance(){
       // TODO!
    }

    public void printSkipList(){
       StringBuilder sb = new StringBuilder();
       sb.append("\nSkipList starting with top-left most item.\n");

       SkipListSetItem<T> curr = head;
       SkipListSetItem<T> highestLevel = curr;
       
       int level = height;

       while (highestLevel != null){
          sb.append("\nLevel: ").append(level).append("\n");

          while (curr!= null){
             sb.append(curr.key);

             if (curr.next != null){
                 sb.append(" : ");
             }

             curr = curr.next;
          }

          highestLevel = highestLevel.below;
          curr = highestLevel;
          level--;
       }

       System.out.println(sb.toString());
    }
}