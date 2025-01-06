import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class BlackjackGUI extends JFrame implements ActionListener {
    private JPanel playerPanel, dealerPanel, buttonPanel;
    private JButton hitButton, standButton, restartButton;
    private JLabel statusLabel;

    private List<String> deck;
    private List<String> playerHand;
    private List<String> dealerHand;

    public BlackjackGUI() {
        setTitle("Blackjack Game");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize panels
        dealerPanel = new JPanel();
        dealerPanel.setLayout(new FlowLayout());
        dealerPanel.setBorder(BorderFactory.createTitledBorder("Dealer's Hand"));

        playerPanel = new JPanel();
        playerPanel.setLayout(new FlowLayout());
        playerPanel.setBorder(BorderFactory.createTitledBorder("Player's Hand"));

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        // Initialize buttons
        hitButton = new JButton("Hit");
        standButton = new JButton("Stand");
        restartButton = new JButton("Restart");
        hitButton.addActionListener(this);
        standButton.addActionListener(this);
        restartButton.addActionListener(this);

        // Add buttons to the panel
        buttonPanel.add(hitButton);
        buttonPanel.add(standButton);
        buttonPanel.add(restartButton);

        // Status label
        statusLabel = new JLabel("Welcome to Blackjack!");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Add panels to the frame
        add(dealerPanel, BorderLayout.NORTH);
        add(playerPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(statusLabel, BorderLayout.PAGE_END);

        initializeGame();
    }

    private void initializeGame() {
        deck = createDeck();
        Collections.shuffle(deck);

        playerHand = new ArrayList<>();
        dealerHand = new ArrayList<>();

        // Deal initial cards
        playerHand.add(deck.remove(0));
        playerHand.add(deck.remove(0));
        dealerHand.add(deck.remove(0));
        dealerHand.add(deck.remove(0));

        updatePanels();
        statusLabel.setText("Your turn: Hit or Stand?");
    }

    private List<String> createDeck() {
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        String[] suits = {"hearts", "diamonds", "clubs", "spades"};
        List<String> deck = new ArrayList<>();

        for (String rank : ranks) {
            for (String suit : suits) {
                deck.add(rank + "_of_" + suit);
            }
        }
        return deck;
    }

    private int calculateHandValue(List<String> hand) {
        int total = 0;
        int aces = 0;

        for (String card : hand) {
            String rank = card.split("_")[0];
            if (rank.matches("\\d+")) {
                total += Integer.parseInt(rank);
            } else if (rank.equals("A")) {
                total += 11;
                aces++;
            } else {
                total += 10;
            }
        }

        while (total > 21 && aces > 0) {
            total -= 10;
            aces--;
        }

        return total;
    }

    private void updatePanels() {
        dealerPanel.removeAll();
        for (int i = 0; i < dealerHand.size(); i++) {
            String card = dealerHand.get(i);
            if (i == 0 && dealerPanel.getComponentCount() == 0) {
                dealerPanel.add(new JLabel(new ImageIcon("images/back_of_card.png")));
            } else {
                dealerPanel.add(new JLabel(new ImageIcon("images/" + card + ".png")));
            }
        }

        playerPanel.removeAll();
        for (String card : playerHand) {
            playerPanel.add(new JLabel(new ImageIcon("images/" + card + ".png")));
        }

        revalidate();
        repaint();
    }

    private void dealerTurn() {
        while (calculateHandValue(dealerHand) < 17) {
            dealerHand.add(deck.remove(0));
        }
        updatePanels();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == hitButton) {
            playerHand.add(deck.remove(0));
            updatePanels();

            if (calculateHandValue(playerHand) > 21) {
                statusLabel.setText("You busted! Dealer wins.");
                hitButton.setEnabled(false);
                standButton.setEnabled(false);
            }
        } else if (e.getSource() == standButton) {
            dealerTurn();
            int playerTotal = calculateHandValue(playerHand);
            int dealerTotal = calculateHandValue(dealerHand);

            if (dealerTotal > 21 || playerTotal > dealerTotal) {
                statusLabel.setText("You win!");
            } else if (playerTotal < dealerTotal) {
                statusLabel.setText("Dealer wins!");
            } else {
                statusLabel.setText("It's a tie!");
            }

            hitButton.setEnabled(false);
            standButton.setEnabled(false);
        } else if (e.getSource() == restartButton) {
            hitButton.setEnabled(true);
            standButton.setEnabled(true);
            initializeGame();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BlackjackGUI game = new BlackjackGUI();
            game.setVisible(true);
        });
    }
}
