import java.util.Random;

public class Main {
    public static void main(String[] args){
        SkipListSet<Integer> skipList = new SkipListSet<>();
        Random random = new Random();

        for (int i = 0; i < 8; ++i){
            skipList.add(Math.abs(random.nextInt()) % 32);
        }

        skipList.printSkipList();

        System.out.println("First: " + skipList.first());
        System.out.println("Last: " + skipList.last());

        System.out.println("Iterator test (foreach loop):");

        skipList.clear();

        for (Integer num : skipList){
            System.out.println(num);
        }
    }
}
