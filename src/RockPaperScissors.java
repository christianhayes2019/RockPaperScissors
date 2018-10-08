import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class RockPaperScissors {

    public enum Item {
        ROCK, PAPER, SCISSORS;


        public List<Item> losesTo;


        public boolean losesTo(Item other) {
            return losesTo.contains(other);
        }
        static {
            SCISSORS.losesTo = Arrays.asList(ROCK);
            ROCK.losesTo = Arrays.asList(PAPER);
            PAPER.losesTo = Arrays.asList(SCISSORS);
        }
    }
    private static DecimalFormat DECIMAL_FORMATTER = new DecimalFormat(".##");
    public static final Random RANDOM = new Random();
    //stats for the game (you win / tie/ computer win)
    private int[] stats = new int[] {0, 0, 0};
    //Markov Chain used as AI for the computer
    private int [][] markovChain; //used for the prev to current throws prediction
    private int nbThrows = 0;
    private Item last = null;





    public static void main(String[] args) {
        RockPaperScissors rps = new RockPaperScissors();
        rps.play();
    }

    public void play() {
        init();
        System.out.println("Type \"STOP\" to quit game at any time.");
        Scanner in = new Scanner(System.in);
        System.out.print("Make your choice : ");

        while (in.hasNextLine()){
            String input = in.nextLine();

            if ("STOP".equals(input))
                break;

            //read user choice
            Item choice;

            try {
                choice = Item.valueOf(input.toUpperCase());
            } catch (Exception e) {
                System.out.println("Invalid Choice");
                continue;
            }
           // choice = Item.valueOf(input.toUpperCase());
            Item aiChoice = nextMove(last);
            nbThrows++;

            //update Markov Chain
            if (last != null){
                updateMarkovChain(last, choice);
            }

            last = choice;

            System.out.println("Computer choice :" +aiChoice);

            if (aiChoice.equals(choice)) {
                System.out.println(" ==> Tie !\n");
                stats[1]++;
            } else if(aiChoice.losesTo(choice)){
                System.out.println(" ==> You win !\n");
                stats[0]++;
            } else {
                System.out.println(" ==> You lose !\n");
                stats[2]++;
            }
            System.out.print("Make your choice : ");
        }

        in.close();

        //display stats
        System.out.println("\n");
        System.out.println("Win Stats");
        int total = stats[0] + stats[1] + stats[2];
        System.out.println("You : " + stats[0] + " - " +
                DECIMAL_FORMATTER.format(stats[0] / (float) total * 100f) + "%");
        System.out.println("Tie : " + stats[1] + " - " +
                DECIMAL_FORMATTER.format(stats[1] / (float) total * 100f) + "%");
        System.out.println("Computer : " + stats[2] + " - " +
                DECIMAL_FORMATTER.format(stats[2] / (float) total * 100f) + "%");
    }

    private void init(){
        int length = Item.values().length;
        markovChain = new int[length][length];


        //setting all of the values in the double array to zero
        for (int i = 0; i < length; i++){
            for (int j = 0; j < length; j++){
                markovChain[i][j] = 0;
            }
        }
    }

    private void updateMarkovChain(Item prev, Item next){
        markovChain[prev.ordinal()][next.ordinal()]++;
    }

    private Item nextMove(Item prev) {
        if (nbThrows < 1) {
            //first move => we can't use Markov Chain prediction
            //so we use a random on the Item list
            return Item.values()[RANDOM.nextInt(Item.values().length)];
        }

        System.out.println("in NextMove");
        System.out.println("Previous throw ordinal: "+ prev.ordinal());
        //we try to predict next Item chosen by the user by reading data in Markov Chain
        //for the prev entry in the array
        int nextIndex = 0;
        System.out.println("next index: "+ nextIndex);
        for (int i = 0; i < Item.values().length; i++){
            int prevIndex = prev.ordinal();

            if (markovChain[prevIndex][i] > markovChain[prevIndex][nextIndex]){

                System.out.println("in for loop: markov chain check: "+ markovChain[prevIndex][i]+ " greater than "+ markovChain[prevIndex][nextIndex]+ " is "+(markovChain[prevIndex][i] > markovChain[prevIndex][nextIndex]) );
                nextIndex = i;
            }
            System.out.println("in for loop:  index: "+ i);
            System.out.println("in for loop: nextindex: "+ nextIndex);
        }
        //Item probably played by the user is in nextIndex
        Item predictedNext = Item.values()[nextIndex];

        //we choose amongst item for which this probably item loses
        List<Item> losesTo = predictedNext.losesTo;
        System.out.println("prediction: "+ predictedNext);
        System.out.println("prediction to win: "+ predictedNext.losesTo);
        return losesTo.get(RANDOM.nextInt(losesTo.size()));
    }



}
