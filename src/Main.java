import java.util.*;

public class Main {
    public static void main(String[] args){
        SkipListSet<Integer> skipList = new SkipListSet<>();
        SkipListSet<Integer> otherSkipList = new SkipListSet<>();

        Random random = new Random();

        for (int i = 0; i < 8; ++i){
            skipList.add(Math.abs(random.nextInt()) % 32);
        }

        System.out.println("Initial list: ");

        skipList.printSkipList();

        for (int i = 0; i < 8; ++i){
            System.out.println("Re-balancing...");
            skipList.reBalance();
            skipList.printSkipList();
        }
    }
}
