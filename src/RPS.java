import java.util.List;

public class RPS {

    public enum Item {
        ROCK, PAPER, SCISSORS;


        public List<Item> losesTo;


        public boolean losesTo(Item other) {
            return losesTo.contains(other);
        }
    }
}
