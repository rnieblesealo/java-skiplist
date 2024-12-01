import java.util.*;

class SkipList {
    private static class Node {
        public Node above;
        public Node below;
        public Node prev;
        public Node next;

        public int key;

        public Node(int key){
            this.key = key;

            this.above = null;
            this.below = null;
            this.prev = null;
            this.next = null;
        }
    }

    private Node head;
    private Node tail;

    private int height;

    private final int NEG_INFINITY = Integer.MIN_VALUE;
    private final int POS_INFINITY = Integer.MAX_VALUE;

    public Random random = new Random();

    public SkipList(){
        head = new Node(NEG_INFINITY);
        tail = new Node(POS_INFINITY);

        head.next = tail;
        tail.prev = head;
    }

    public Node search(int key){
        // Returns largest node at lowest level such that it is <= search key

        Node curr = head;

        while (curr.below != null){
            curr = curr.below;

            while (key >= curr.next.key){
                curr = curr.next;
            }
        }

        return curr;
    }

    public Node insert(int key){
        Node insertPos = search(key);
        Node q;

        int heightOfNewKey = -1;

        // Handle duplicate
        if (insertPos.key == key){
            return insertPos;
        }

        do {
            heightOfNewKey++;

            increaseHeightIfTaller(heightOfNewKey);

            q = insertPos;

            while (insertPos.above == null){
                insertPos = insertPos.prev;
            }

            insertPos = insertPos.above;

            q = insertAfterAbove(insertPos, q, key);
        } while (random.nextBoolean());

        return q;
    }

    public Node remoe(int key){

    }

    private void increaseHeightIfTaller(int heightOfNewKey){
        if (heightOfNewKey >= this.height){
            this.height++;
            addEmptyLevel();
        }
    }

    private void addEmptyLevel(){
        Node newHead = new Node(NEG_INFINITY);
        Node newTail = new Node(POS_INFINITY);

        newHead.next = newTail;
        newHead.below = head;

        newTail.prev = newHead;
        newTail.below = tail;

        head.above = newHead;
        tail.above = newTail;

        head = newHead;
        tail = newTail;
    }

    private Node insertAfterAbove(Node position, Node q, int key){
        Node newNode = new Node(key);
        Node nodeBeforeNew = position.below.below;

        setBeforeAndAfterRefs(q, newNode);
        setAboveAndBelowRefs(position, key, newNode, nodeBeforeNew);

        return newNode;
    }

    private void setBeforeAndAfterRefs(Node q, Node newNode){
        // Do new node horizontal links, similar to linked list insertion
        newNode.next = q.next;
        newNode.prev = q;

        q.next.prev = newNode;
        q.next = newNode;
    }

    private void setAboveAndBelowRefs(Node position, int key, Node newNode, Node nodeBeforeNew){
        // Do vertical refs, which requires a bit more work due to the height property
        if (nodeBeforeNew != null){
            while (true){
                if (nodeBeforeNew.next.key != key){
                    nodeBeforeNew = nodeBeforeNew.next;
                } else {
                    break;
                }
            }

            newNode.below = nodeBeforeNew.next;
            nodeBeforeNew.next.above = newNode;
        }

        if (position != null){
            if (position.next.key == key){
                newNode.above = position.next;
            }
        }
    }
}

public class SkipListExample {

}
