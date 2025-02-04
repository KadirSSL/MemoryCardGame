import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;

public class MemoryCardGame {
    private final int satir;
    private final int sutun;
    private final JFrame frame;
    private final JButton[][] kartlar;
    private final Map<JButton, Point> kartKonumlari = new HashMap<>();
    private final String[][] renkler;
    private final List<JButton> secilenKartlar = new ArrayList<>();
    private static final Set<String> usedColors = new HashSet<>();
    private static final Random rand = new Random();
    private  JLabel zamanLabel;
    private Timer oyunTimer;
    private int kalanSure;
    private int puan = 0;
    private int dogruEslesme = 0;
    private int yanlisEslesme = 0;

    public MemoryCardGame(int satir, int sutun) {
        this.satir = satir;
        this.sutun = sutun;
        this.frame = new JFrame("Hafıza Kartları");
        this.kartlar = new JButton[satir][sutun];
        this.renkler = new String[satir][sutun];

        int oyunAlani = satir * sutun;

        if (oyunAlani < 99) kalanSure = 300;
        else if (oyunAlani < 150) kalanSure = 600;
        else kalanSure = 900;

        zamanLabel = new JLabel("Süre: " + formatTime(kalanSure));

        frame.setLayout(new BorderLayout());
        JPanel ustPanel = new JPanel();
        ustPanel.add(zamanLabel);
        frame.add(ustPanel, BorderLayout.NORTH);

        JPanel oyunPanel = new JPanel(new GridLayout(satir, sutun));
        frame.add(oyunPanel, BorderLayout.CENTER);

        initializeGame(oyunPanel);

        oyunTimer = new Timer(1000, e -> updateTimer());
        oyunTimer.start();

        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void initializeGame(JPanel panel) {
        List<String> renkListesi = generateColors();
        int index = 0;
        for (int i = 0; i < satir; i++) {
            for (int j = 0; j < sutun; j++) {
                renkler[i][j] = renkListesi.get(index++ % renkListesi.size());
                JButton kart = new JButton();
                kart.setBackground(Color.BLACK);
                kartKonumlari.put(kart, new Point(i, j));
                kart.addActionListener(this::kartAc);
                kartlar[i][j] = kart;
                panel.add(kart);
            }
        }
    }

    private List<String> generateColors() {
        Set<String> renkSeti = new HashSet<>();
        while (renkSeti.size() < (satir * sutun) / 3) {
            renkSeti.add(generateRandomColor());
        }
        List<String> renkListesi = new ArrayList<>(renkSeti);
        Collections.shuffle(renkListesi);
        return renkListesi;
    }

    private String generateRandomColor() {
        String color;
        do {
            color = String.format("#%02X%02X%02X", rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
        } while (color.equals("#000000") || color.equals("#FFFFFF") || !usedColors.add(color));
        return color;
    }

    private void kartAc(ActionEvent e) {
        JButton tiklananKart = (JButton) e.getSource();
        Point konum = kartKonumlari.get(tiklananKart);
        if (konum == null || secilenKartlar.contains(tiklananKart)) return;

        tiklananKart.setBackground(Color.decode(renkler[konum.x][konum.y]));
        secilenKartlar.add(tiklananKart);

        if (secilenKartlar.size() == 3) kontrolEt();
    }

    private void kontrolEt() {
        JButton kart1 = secilenKartlar.get(0);
        JButton kart2 = secilenKartlar.get(1);
        JButton kart3 = secilenKartlar.get(2);

        Point p1 = kartKonumlari.get(kart1);
        Point p2 = kartKonumlari.get(kart2);
        Point p3 = kartKonumlari.get(kart3);

        if (renkler[p1.x][p1.y].equals(renkler[p2.x][p2.y]) && renkler[p1.x][p1.y].equals(renkler[p3.x][p3.y])) {
            dogruEslesme++;
            puan += 10;
            Timer timer = new Timer(500, e -> {
                kart1.setBackground(Color.WHITE);
                kart2.setBackground(Color.WHITE);
                kart3.setBackground(Color.WHITE);
            });
            timer.setRepeats(false);
            timer.start();

            kart1.setEnabled(false);
            kart2.setEnabled(false);
            kart3.setEnabled(false);
        } else {
            yanlisEslesme++;
            puan -= 3;
            Timer timer = new Timer(500, e -> {
                kart1.setBackground(Color.BLACK);
                kart2.setBackground(Color.BLACK);
                kart3.setBackground(Color.BLACK);
            });
            timer.setRepeats(false);
            timer.start();
        }
        secilenKartlar.clear();
        checkGameOver();
    }

    private void updateTimer() {
        kalanSure--;
        zamanLabel.setText("Süre: " + formatTime(kalanSure));
        if (kalanSure <= 0) {
            oyunTimer.stop();
            showGameResult("Süre Bitti!");
        }
    }

    private void checkGameOver() {
        boolean oyunBitti = Arrays.stream(kartlar).flatMap(Arrays::stream).allMatch(k -> !k.isEnabled());
        if (oyunBitti) {
            oyunTimer.stop();
            showGameResult("Oyun Bitti!");
        }
    }

    private void showGameResult(String baslik) {
        int bonus = kalanSure / 10;
        puan += bonus;

        int gecenSure = (satir * sutun < 99) ? 300 - kalanSure : (satir * sutun < 150) ? 600 - kalanSure : 900 - kalanSure;

        JOptionPane.showMessageDialog(null, baslik + "\nGeçen Süre: " + formatTime(gecenSure) + "\nPuan: " + puan + "\nDoğru Eşleşmeler: " + dogruEslesme + "\nYanlış Eşleşmeler: " + yanlisEslesme);

        frame.dispose();
    }

    private String formatTime(int seconds) {
        return (seconds / 60) + " dk " + (seconds % 60) + " sn";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int satir, sutun;

            // Kullanıcıdan geçerli satır ve sütun değerlerini alana kadar tekrar sor
            while (true) {
                satir = Integer.parseInt(JOptionPane.showInputDialog("Satır sayısını giriniz:"));
                sutun = Integer.parseInt(JOptionPane.showInputDialog("Sütun sayısını giriniz (3'ün katı olmalı):"));

                // Hatalı girişleri kontrol et
                if (sutun % 3 != 0) {
                    JOptionPane.showMessageDialog(null, "Hata! Sütun sayısı 3'e bölünebilir olmalıdır.");
                    continue; // Döngünün başına dön ve tekrar giriş iste
                }
                if (satir * sutun > 210) {
                    JOptionPane.showMessageDialog(null, "Hata! Oyun alanı 210 karttan fazla olamaz.");
                    continue; // Döngünün başına dön ve tekrar giriş iste
                }

                // Eğer geçerli değerler girildiyse döngüden çık
                break;
            }

            // Oyun başlat
            new MemoryCardGame(satir, sutun);
        });
    }

}
