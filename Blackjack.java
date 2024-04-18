import java.util.ArrayList;
import java.util.Scanner;

public class Blackjack {
    public static void main(String[] args) {
        System.out.println("\n'99% of gamblers quit before they win big' -Ben Franklin\n");
        Scanner s = new Scanner(System.in);

        int[] creditCardInfo = {0,0,0};
        while (creditCardInfo[0]+creditCardInfo[1]+creditCardInfo[2] != 3) {
            if (creditCardInfo[0] == 0) {
                System.out.printf("Credit card number (16 digits): ");
                creditCardInfo[0] = s.nextLine().length() == 16 ? 1 : 0;
            }
            if (creditCardInfo[1] == 0) {
                System.out.printf("Expiration date (3 digits): ");
                creditCardInfo[1] = s.nextLine().length() == 3 ? 1 : 0;
            }
            if (creditCardInfo[2] == 0) {
                System.out.printf("CVV (3 digits): ");
                creditCardInfo[2] = s.nextLine().length() == 3 ? 1 : 0;
            }
        }

        Game game;
        boolean playing = true;
        int profit = 0;
        while (playing) {
            int wager = 0;
            while (wager < 10) {
                System.out.printf("Your Wager (at least $10): $");
                try {
                    wager = Integer.parseInt(s.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number");
                }
            }

            game = new Game();
            String move;
            int sum;
            ArrayList<Card> playerHand = game.getHand("player");
            Card dealerFirstCard = game.getHand("dealer").get(0);
            while (true) {
                sum = game.sumHand(playerHand);
                System.out.printf("\nYour hand: %s\nTotal: %d\n\n", playerHand, sum);
                System.out.printf("Dealer Hand: [%s, unknown]\n\n", dealerFirstCard);

                if (sum > 21) {
                    System.out.println("You Busted!");
                    break;
                }

                System.out.printf("Your Move (Hit/Stand): ");
                move = s.nextLine();
                
                if (move.equalsIgnoreCase("hit")) {
                    System.out.printf("You got the %s\n", game.addCard(playerHand));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (move.equalsIgnoreCase("stand")) {
                    System.out.println("\nDealers Turn");
                    game.dealerMove();
                    break;
                }
            }

            profit += game.determineWinner()*wager;

            System.out.printf("\nCurrent profit: %s$%d\u001B[0m\n", profit < 0 ? "\u001B[31m-" : "\u001B[32m", Math.abs(profit));
            System.out.printf("Play Again? (y/N): ");
            String playAgain;
            while (!"yn".contains(playAgain = s.nextLine().toLowerCase())) {
                System.out.printf("Play Again? (y/N): ");
            }
            playing = playAgain.equals("y");
        }

        s.close();
        System.out.printf("\nFinal profit: %s$%d\u001B[0m\n", profit < 0 ? "\u001B[31m-" : "\u001B[32m", Math.abs(profit));
    }
}

class Game {
    private ArrayList<Card> deck;
    private ArrayList<Card> playerHand;
    private ArrayList<Card> dealerHand;

    public Game() {
        deck = new ArrayList<Card>();
        playerHand = new ArrayList<Card>();
        dealerHand = new ArrayList<Card>();

        String[] values = {"King", "Queen", "Jack", "Ace"};
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < suits.length; j++) {
                Card card;
                if (i < 9) {
                    card = new Card(suits[j], String.format("%d", i+2));
                } else {
                    card = new FaceCard(suits[j], values[i-9]);
                }
                deck.add(card);
            }
        }

        for (int i = 0; i < 2; i++) {
            addCard(playerHand);
            addCard(dealerHand);
        }
    }

    public Card chooseRandomCard() {
        int randomCardIndex = (int) Math.floor(Math.random()*deck.size());
        Card randomCard = deck.get(randomCardIndex);
        deck.remove(randomCardIndex);
        return randomCard;
    }

    public Card addCard(ArrayList<Card> hand) {
        Card card = chooseRandomCard();
        hand.add(card);
        return card;
    }

    public ArrayList<Card> getHand(String hand) {
        if (hand.equalsIgnoreCase("player")) {
            return playerHand;
        }
        if (hand.equalsIgnoreCase("dealer")) {
            return dealerHand;
        }
        return null;
    }

    public int sumHand(ArrayList<Card> hand) {
        int total = 0;
        int value = 0;
        for (Card c: hand) {
            value = c.getValue();
            total += value != 11 ? value : total + 11 > 21 ? 1 : 11;
        }
        return total;
    }

    public int determineWinner() {
        int playerSum = sumHand(playerHand);
        int dealerSum = sumHand(dealerHand);
        if ((playerSum < dealerSum && dealerSum <= 21) || playerSum > 21) {
            System.out.println("\u001B[31mYou Lose! (We are stealing from your account)\u001B[0m");
            return -1;
        }
        if (playerSum > dealerSum || dealerSum > 21) {
            System.out.println("\u001B[32mYou Win! (You get the money you wagered)\u001B[0m");
            return 1;
        }
        if (playerSum == dealerSum) {
            System.out.println("Its a Tie!");
        }
        return 0;

    }

    public void dealerMove() {
        int dealerSum = sumHand(dealerHand);
        System.out.printf("Dealer Hand: %s\nTotal: %d\n\n", dealerHand, dealerSum);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (dealerSum < 17) {
            System.out.println("Dealer Move: Hit");
            System.out.printf("Dealer got the %s\n\n", addCard(dealerHand));
            dealerMove();
        } else if (dealerSum <= 21){
            System.out.println("Dealer Move: Stand");
        } else {
            System.out.println("Dealer Busted!");
        }
    }
}

class Card {
    private String suit;
    private String color;
    protected String name;

    public Card(String suit, String name) {
        this.suit = suit;
        this.name = name;
        this.color = "SpadesClubs".contains(suit) ? "\u001B[30m" : "\u001B[31m";
    }

    public int getValue() {
        return Integer.parseInt(this.name);
    }

    public String toString() {
        return String.format("%s%s of %s\u001B[0m", color, name, suit);
    }
}

class FaceCard extends Card {
    private int value;
    public FaceCard(String suit, String name) {
        super(suit, name);
        this.value = name.equalsIgnoreCase("Ace") ? 11 : 10;
    }

    public int getValue() {
        return value;
    }
}