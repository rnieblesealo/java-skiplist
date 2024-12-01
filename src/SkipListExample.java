import java.util.*;

class SkipList {
    static class Node {
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

        System.out.println("got insertpos: " + insertPos.key);

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

            System.out.println(insertPos.key);

            q = insertAfterAbove(insertPos, q, key);
        } while (random.nextBoolean());

        return q;
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

    public Node remove(int key){
        Node toRemove = search(key);

        if (toRemove.key != key){
            return null;
        }

        removeRefsToNode(toRemove);

        while (toRemove != null){
            removeRefsToNode(toRemove);

            if (toRemove.above != null){
                toRemove = toRemove.above;
            } else {
                break;
            }
        }

        return toRemove;
    }

    private void removeRefsToNode(Node toRemove){
        Node afterToRemove = toRemove.next;
        Node beforeToRemove = toRemove.prev;

        beforeToRemove.next = afterToRemove;
        afterToRemove.prev = beforeToRemove;
    }

    public void print(){
        StringBuilder sb = new StringBuilder();

        Node starting = head;
        Node highest = starting;

        int level = height;

        while (highest != null){
           sb.append("\nLevel: " + level + "\n");

           while (starting != null){
               sb.append(starting.key);

               if (starting.next != null){
                   sb.append(" : ");
               }

               starting = starting.next;
           }

           highest = highest.below;
           starting = highest;
           level--;
        }

        System.out.println(sb);
    }
}

public class SkipListExample {
    public static void main(String[] args){
        SkipList list = new SkipList();

        list.insert(3);
        list.insert(4);

        list.print();
    }
}
