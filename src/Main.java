public class Main {
    public static void main(String[] args){
        SkipList skipList = new SkipList();

        skipList.skipInsert(3);
        skipList.skipInsert(5);
        skipList.skipInsert(12);
        skipList.skipInsert(8);

        skipList.printSkipList();

        skipList.remove(12);
        skipList.remove(8);

        skipList.printSkipList();
    }
}
