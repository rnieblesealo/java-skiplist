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
            this.isTail = false;

            this.above = null;
            this.below = null;
            this.next = null;
            this.prev = null;
        }
    }

    // No need for <T> after iterator class name because it's already defined in SkipListSet name
    public class SkipListSetIterator implements Iterator<T> {
        private SkipListSetItem<T> curr;

        public SkipListSetIterator(){
            // Start iterator at lowest level
            curr = lowHead.next;
        }

        @Override
        public boolean hasNext() {
            // Iterator stops once we reach the tail
            if (curr.isTail){
                return false;
            }

            return true;
        }

        @Override
        public T next() {
            // If no next, we can't keep going
            if (!hasNext()){
                throw new NoSuchElementException();
            }

            // Give back the key of curr and move on
            T currentKey = curr.key;

            curr = curr.next;

            return currentKey;
        }

        @Override
        public void remove() {
            // TODO!
        }
    }

    private SkipListSetItem<T> topHead;
    private SkipListSetItem<T> topTail;

    private SkipListSetItem<T> lowHead;
    private SkipListSetItem<T> lowTail;

    private int height; // Height of list
    private int size; // Amount of unique elements in list

    public Random random = new Random();

    private final Comparator<? super T> comparator;

    public SkipListSet(){
        size = 0;
        height = 0;

        // Assign the comparator
        this.comparator = null;

        // Initialize sentinel items
        topHead = new SkipListSetItem<>();
        topTail = new SkipListSetItem<>();

        // Mark them as head and tail
        topHead.isHead = true;
        topTail.isTail = true;

        // Make the list wrap around (?)
        topHead.next = topTail;
        topTail.prev = topHead;

        // Because it's our first level, the top head and tail are same
        lowHead = topHead;
        lowTail = topTail;
    }

    public SkipListSet(Comparator<? super T> comparator){
        // Assign the comparator
        this.comparator = comparator;

        // Do everything else the exact same
        size = 0;
        height = 0;

        topHead = new SkipListSetItem<>();
        topTail = new SkipListSetItem<>();

        topHead.isHead = true;
        topTail.isTail = true;

        topHead.next = topTail;
        topTail.prev = topHead;

        lowHead = topHead;
        lowTail = topTail;
    }

    /** AUX: Compare existing list elements */
    private int compare(SkipListSetItem<T> e1, SkipListSetItem<T> e2){
        /*

        (This assumes both comparison items could be sentinels...)

        We might need to reach the end, but we can't compare with it...

        [h] -> [3] -> [t]

        h is less than t
        t is greater than anything

        -1 if first value < second
         1 if first value > second
         0 if equal

        e1 = first
        e2 = second

        if both are head or tail
            return 0

        if e1 is head, e2 is any
            return -1

        [h]->[3]

        if e1 is tail, e2 is any (if e1 is tail, next can only be head)
            return 1

        [t]->[h]

        And flip...

        if e1 is any, e2 is head (head can only be really pointed to by tail)
            return 1

        [t]->[h]

        if e1 is any, e2 is tail
            return -1

        [3]->[t]

        Any other comparisons don't involve sentinels and can therefore be done by key

        */

        if (e1.isHead && e2.isHead){
            return 0;
        } else if (e1.isTail && e2.isTail){
            return 0;
        } else if (e1.isHead){
            return -1;
        } else if (e1.isTail){
            return 1;
        } else if (e2.isHead){
            return 1;
        } else if (e2.isTail){
            return -1;
        } else {
            T e1Key = e1.key;
            T e2Key = e2.key;

            return compare(e1Key, e2Key);
        }
    }

    private int compare(SkipListSetItem<T> e1, T e2){
        if (e1.isHead){
            return -1;
        } else if (e1.isTail){
            return 1;
        } else {
            T e1Key = e1.key;

            return compare(e1Key, e2);
        }
    }

    private int compare(T e1, T e2){
        if (this.comparator == null){
            // Cast should be OK, our skip list uses comparable values
            Comparable<? super T> e1KeyAsComparable = (Comparable<? super T>)e1;
            return e1KeyAsComparable.compareTo(e2);
        } else {
            return this.comparator.compare(e1, e2);
        }
    }

    /** AUX: Check if node is sentinel (head or tail) */
    private boolean isSentinel(SkipListSetItem<T> item){
        return item.isHead || item.isTail;
    }

    /** Returns the comparator */
    public Comparator<? super T> comparator(){
        return comparator;
    }

    /** Return iterator */
    @Override
    public Iterator<T> iterator(){
        return new SkipListSetIterator();
    }

    /** Add to skip list */
    @Override
    public boolean add(T key){
        SkipListSetItem<T> insertPos = search(key);
        SkipListSetItem<T> q;

        int heightOfNewKey = -1;

        // Don't modify the set on a duplicate key
        if (compare(insertPos, key) == 0){
            return false;
        }

        do {
            heightOfNewKey++;

            increaseHeightIfTaller(heightOfNewKey);

            q = insertPos;

            while (insertPos.above == null){
                insertPos = insertPos.prev;
            }

            insertPos = insertPos.above;

            insertAfterAbove(insertPos, q, key);
        } while (random.nextBoolean());

        // Set was modified if we reach this point
        size++;
        return true;
    }

    /** AUX: Retrieves skip list item with specific key */
    private SkipListSetItem<T> search(T key){
        SkipListSetItem<T> curr = topHead;

        // While we aren't at the bottom level...
        while (curr.below != null){
            curr = curr.below;

            // Go across while the next key is greater and we're not about to wrap around
            while (!isSentinel(curr.next) && compare(key, curr.next.key) >= 0){
                curr = curr.next;
            }

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

    /** AUX: Perform necessary re-links for insertion */
    private void insertAfterAbove(SkipListSetItem<T> position, SkipListSetItem<T> q, T key){
        SkipListSetItem<T> newItem = new SkipListSetItem<>(key);
        SkipListSetItem<T> itemBeforeNew = position.below.below;

        setBeforeAndAfterReferences(q, newItem);
        setAboveAndBelowReferences(position, key, newItem, itemBeforeNew);
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
                if (compare(itemBeforeNew.next, key) != 0){
                    itemBeforeNew = itemBeforeNew.next;
                } else {
                    break;
                }
            }

            newItem.below = itemBeforeNew.next;
            itemBeforeNew.next.above = newItem;
        }

        if (position != null){
            if (compare(position.next, key) == 0){
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
        newHead.below = topHead;

        // New tail's previous is the new head; its below is the old tail
        newTail.prev = newHead;
        newTail.below = topTail;

        // Make references persistent for head and tail as well
        topHead.above = newHead;
        topTail.above = newTail;

        // Update current head and tail
        topHead = newHead;
        topTail = newTail;
    }

    /** Remove from skip list */
    @Override
    public boolean remove(Object o){
        // Cast should be OK
        T key = (T)o;

        SkipListSetItem<T> itemToBeRemoved = search(key);

        // Can't remove key that doesn't exist!
        if (compare(itemToBeRemoved, key) != 0){
            return false;
        }

        removeReferencesToItem(itemToBeRemoved);

        while (true){
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
        return lowHead.next.key;
    }

    /** Return last element in skip list */
    @Override
    public T last(){
        return lowTail.prev.key;
    }

    /** Return skip list size */
    @Override
    public int size(){
        return size;
    }

    /** Return whether skip list is empty */
    @Override
    public boolean isEmpty(){
        if (size == 0){
            return true;
        }

        return false;
    }

    /** Return whether o exists in the skip list */
    @Override
    public boolean contains(Object o){
        T key = (T)o;
        
        SkipListSetItem<T> searchResult = search(key);
        
        if (compare(searchResult, key) == 0){
            return true;
        }
        
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
        for (Object o : c){
            if (!contains(o)){
                return false;
            }
        }

        return true;
    }

    /** Add a set of elements to the skip list */
    @Override
    public boolean addAll(Collection<? extends T> c){
        boolean modified = false;

        for (T key : c){
            if (add(key)){
                modified = true;
            }
        }

        return modified;
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
        boolean modified = false;

        for (Object o : c){
            if (remove(o)){
                modified = true;
            }
        }

        return modified;
    }

    /** Clears the skip list */
    @Override
    public void clear(){
        // Reset everything; overwritten stuff will be garbage collected
        size = 0;
        height = 0;

        topHead = new SkipListSetItem<>();
        topTail = new SkipListSetItem<>();

        topHead.isHead = true;
        topTail.isTail = true;

        topHead.next = topTail;
        topTail.prev = topHead;

        lowHead = topHead;
        lowTail = topTail;
    }

    /** Re-balances the skip list, randomizing all probabilities again by re-adding... */
    private void reBalance(){
       // TODO!
    }

    public void printSkipList(){
       StringBuilder sb = new StringBuilder();
       sb.append("\nSkipList starting with top-left most item.\n");

       SkipListSetItem<T> curr = topHead;
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

       System.out.println(sb);
    }
}