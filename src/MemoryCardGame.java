import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.Timer;

public class MemoryCardGame {//hatalı ama düzeltilecek
    private static final int MAX_TIME = 60;  // Maksimum süre
    private int timeLeft;
    private int score;
    private int matchCount;
    private int firstCardIndex = -1;
    private int secondCardIndex = -1;
    private int thirdCardIndex = -1;
    private final List<JButton> buttons = new ArrayList<>();
    private final Color[] colors = new Color[16]; // Örnek kart renkleri
    private final boolean[] cardFlipped = new boolean[16]; // Kartların çevrilip çevrilmediğini tutacak dizi
    private final int[] cardValues = new int[16]; // Kartların değerlerini tutacak dizi
    private javax.swing.Timer timer; // Burada Timer'ı javax.swing.Timer olarak belirtiyoruz.
    private int gridRows = 3;  // Grid satır sayısı
    private int gridColumns = 3;  // Grid sütun sayısı

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MemoryCardGame::new);
    }

    public MemoryCardGame() {
        JFrame frame = new JFrame("Memory Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(gridRows, gridColumns));

        // Kartları oluştur
        for (int i = 0; i < gridRows * gridColumns; i++) {
            JButton button = new JButton();
            button.setBackground(Color.BLACK);
            button.addActionListener(new CardClickListener(i));
            buttons.add(button);
            frame.add(button);
        }

        // Kartları karıştır
        initializeGame();

        // Oyunu başlat
        startTimer();

        frame.pack();
        frame.setVisible(true);
    }

    private void initializeGame() {
        // Kartları ve değerlerini belirle (örneğin 8 eşleşme, toplamda 16 kart)
        List<Integer> values = new ArrayList<>();
        for (int i = 0; i < gridRows * gridColumns / 2; i++) {
            values.add(i);
            values.add(i);
        }
        Collections.shuffle(values);

        // Kart değerlerini atama
        for (int i = 0; i < gridRows * gridColumns; i++) {
            cardValues[i] = values.get(i);
            colors[i] = new Color((int) (Math.random() * 0x1000000));  // Rastgele renkler
            cardFlipped[i] = false;
        }
    }

    private void startTimer() {
        timeLeft = MAX_TIME;
        score = 0;
        matchCount = 0;

        // Başlangıçta timer'ı başlat
        timer = new javax.swing.Timer(1000, e -> {
            timeLeft--;
            if (timeLeft <= 0) {
                endGame();
            }
        });
        timer.start();
    }

    private void endGame() {
        JOptionPane.showMessageDialog(null, "Game Over! Your score: " + score);
        timer.stop();
    }

    private class CardClickListener implements ActionListener {
        private final int index;

        public CardClickListener(int index) {
            this.index = index;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (cardFlipped[index]) return; // Kart zaten çevrilmiş

            // Kartı çevir
            buttons.get(index).setBackground(colors[index]);
            cardFlipped[index] = true;

            // Eşleşme kontrolü için kartları seç
            if (firstCardIndex == -1) {
                firstCardIndex = index;
            } else if (secondCardIndex == -1) {
                secondCardIndex = index;
            } else if (thirdCardIndex == -1) {
                thirdCardIndex = index;
                checkMatch();  // Üçüncü kartı seçtikten sonra eşleşme kontrolü yap
            }
        }
    }

    private void checkMatch() {
        if (firstCardIndex == -1 || secondCardIndex == -1 || thirdCardIndex == -1) {
            return;  // Üç kart seçilmeden işlem yapılmaz
        }

        // Üç kartın da aynı değeri taşıması durumunda eşleşme kabul edilir
        if (cardValues[firstCardIndex] == cardValues[secondCardIndex] &&
                cardValues[firstCardIndex] == cardValues[thirdCardIndex]) {

            // Doğru eşleşme
            score += 10;  // Puan artır
            matchCount++; // Eşleşme sayısını artır

            // Kartları beyaz yaparak eşleştiğini belirt
            buttons.get(firstCardIndex).setBackground(Color.WHITE);
            buttons.get(secondCardIndex).setBackground(Color.WHITE);
            buttons.get(thirdCardIndex).setBackground(Color.WHITE);

            // Kartları doğru olarak işaretle
            cardFlipped[firstCardIndex] = true;
            cardFlipped[secondCardIndex] = true;
            cardFlipped[thirdCardIndex] = true;
        } else {
            // Yanlış eşleşme
            score -= 3;  // Puanı düşür

// Kartları geri çevirme
            javax.swing.Timer pauseTimer = new javax.swing.Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    buttons.get(firstCardIndex).setBackground(Color.BLACK);
                    buttons.get(secondCardIndex).setBackground(Color.BLACK);
                    buttons.get(thirdCardIndex).setBackground(Color.BLACK);
                    cardFlipped[firstCardIndex] = false;
                    cardFlipped[secondCardIndex] = false;
                    cardFlipped[thirdCardIndex] = false;
                }
            });
            pauseTimer.setRepeats(false);
            pauseTimer.start();

        }

        // Kartları sıfırla
        firstCardIndex = -1;
        secondCardIndex = -1;
        thirdCardIndex = -1;

        // Tüm eşleşmeler tamamlandığında oyunu bitir
        if (matchCount == (gridRows * gridColumns) / 3) {
            endGame();
        }
    }
}
