public class Node {
    public Node above;
    public Node below;
    public Node next;
    public Node prev;

    public int key;

    public Node(int key){
        this.key = key;

        this.above = null;
        this.below = null;
        this.next = null;
        this.prev = null;
    }
}
