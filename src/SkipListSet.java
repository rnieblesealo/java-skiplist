import java.util.*;

public class SkipListSet <T extends Comparable<T>> implements SortedSet<T>{
    private class SkipListItem<T extends Comparable<T>>{
        public SkipListItem<T> above;
        public SkipListItem<T> below;
        public SkipListItem<T> next;
        public SkipListItem<T> prev;

        T key;

        // We'll use these instead of infinities to know if we have head or tail for a genericized approach
        public boolean isHead;
        public boolean isTail;

        public SkipListItem(T key){
           this.key = key;

           this.above = null;
           this.below = null;
           this.next = null;
           this.prev = null;

           this.isHead = false;
           this.isTail = false;
        }

        public SkipListItem(){
            this.key = null;

            this.above = null;
            this.below = null;
            this.next = null;
            this.prev = null;

            this.isHead = false;
            this.isTail = false;
        }
    }

    // NOTE: Will use normal randomization for now; implement later as specified in doc
    private Random random = new Random();

    private SkipListItem<T> head;
    private SkipListItem<T> tail;

    private int height;

    public SkipListSet(){
        head = new SkipListItem<>();
        tail = new SkipListItem<>();

        head.isHead = true;
        tail.isTail = true;

        head.next = tail;
        tail.prev = head;
    }

    /** Gets the node after target */
    private SkipListItem<T> skipSearch(T key){
        SkipListItem<T> n = this.head;

        while (n.below != null){
            n = n.below;

            // TODO: n.item is null for heads and tails! Implement correctly
            while (key.compareTo(n.key) == 1){
                n = n.next;
            }
        }

        return n;
    }

    private SkipListItem<T> skipInsert(T key){
        SkipListItem<T> position = skipSearch(key);
        SkipListItem<T> q;

        int level = -1;
        int numberOfHeads = -1;

        // Avoid dupes!
        // TODO: position.item may be null for head/tail! Implement correctly
        if (position.key.compareTo(key) == 0){
            return position;
        }

        do {
            numberOfHeads++;
            level++;

            canIncreaseLevel(level);

            q = position;

            while (position.above == null){
                position = position.prev;
            }

            position = position.above;

            q = insertAfterAbove(position, q, key);
        } while (random.nextBoolean() == true);

        return q;
    }

    private void canIncreaseLevel(int level){
        if (level >= height) {
            height++;
            addEmptyLevel();
        }
    }

    private void addEmptyLevel(){
        // NOTE: Lots of re-refs here! Make sure to check here if failures arise

       SkipListItem<T> newHeadNode = new SkipListItem<>();
       SkipListItem<T> newTailNode = new SkipListItem<>();

       // Ensure we mark the new head/tail as so
       newHeadNode.isHead = true;
       newTailNode.isTail = true;

       newTailNode.prev = newHeadNode;
       newTailNode.below = tail;

       head.above = newHeadNode;
       tail.above = newTailNode;

       // Unmark the old head/tail
       // Is this even necessary?
       head.isHead = false;
       tail.isTail = false;

       head = newHeadNode;
       tail = newTailNode;
    }

    private SkipListItem<T> insertAfterAbove(SkipListItem<T> position, SkipListItem<T> q, T key){
        SkipListItem<T> newNode = new SkipListItem<>(key);
        SkipListItem<T> nodeBeforeNewNode = position.below.below;

        setBeforeAndAfterReferences(q, newNode);
        setAboveAndBelowReferences(position, key, newNode, nodeBeforeNewNode);

        return newNode;
    }

    private void setBeforeAndAfterReferences(SkipListItem<T> q, SkipListItem<T> newNode){
        newNode.next = q.next;
        newNode.prev = q;

        q.next.prev = newNode;
        q.next = newNode;
    }

    private void setAboveAndBelowReferences(SkipListItem<T> position, T key, SkipListItem<T> newNode, SkipListItem<T> nodeBeforeNewNode){
       if (nodeBeforeNewNode != null){
           while (true){
               if (nodeBeforeNewNode.next.key != key){
                   nodeBeforeNewNode = nodeBeforeNewNode.next;
               } else {
                   break;
               }
           }

           newNode.below = nodeBeforeNewNode.next;
           nodeBeforeNewNode.next.above = newNode;
       }

       if (position != null){
           if (position.next.key == key){
               newNode.above = position.next;
           }
       }
    }

    public SkipListItem<T> remove(T key){
        SkipListItem<T> nodeToBeRemoved = skipSearch(key);

        // Can't remove node that doesn't exist!
        if (nodeToBeRemoved.key != key){
            return null;
        }

        removeReferencesToNode(nodeToBeRemoved);

        while (nodeToBeRemoved != null){
            removeReferencesToNode(nodeToBeRemoved);

            if (nodeToBeRemoved.above != null){
               nodeToBeRemoved = nodeToBeRemoved.above;
            } else {
                break;
            }
        }

        return nodeToBeRemoved;
    }

    private void removeReferencesToNode(SkipListItem<T> nodeToBeRemoved){
        SkipListItem<T> afterNodeToBeRemoved = nodeToBeRemoved.next;
        SkipListItem<T> beforeNodeToBeRemoved = nodeToBeRemoved.prev;

        beforeNodeToBeRemoved.next = afterNodeToBeRemoved;
        afterNodeToBeRemoved.prev = beforeNodeToBeRemoved;
    }
}