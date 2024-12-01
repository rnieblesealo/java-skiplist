public class Main {
    public static void main(String[] args){
        SkipListSet<Integer> mySkipList = new SkipListSet<>();

        mySkipList.add(1);
        mySkipList.add(2);
        mySkipList.add(3);
        mySkipList.add(4);

        mySkipList.printSkipList();
    }
}
