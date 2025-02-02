import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.Timer;
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

    // Rastgele renk üretici
    private String generateRandomColor() {
        Random rand = new Random();
        int r = rand.nextInt(256); // 0-255 arası kırmızı
        int g = rand.nextInt(256); // 0-255 arası yeşil
        int b = rand.nextInt(256); // 0-255 arası mavi
        return String.format("#%02X%02X%02X", r, g, b); // Hex formatına dönüştür
    }

    public MemoryCardGame(int satir, int sutun) {
        this.satir = satir;
        this.sutun = sutun;
        this.frame = new JFrame("Hafıza Kartları");
        this.kartlar = new JButton[satir][sutun];
        this.renkler = new String[satir][sutun];
        initializeGame();
    }

    private void initializeGame() {
        frame.setLayout(new GridLayout(satir, sutun));

        // Bölge sayısını belirleyelim
        int regionWidth = sutun / 3; // 3 eşit bölgeye ayırma

        // Sütun * satır / 3 kadar renk üret
        Set<String> renkSeti = new HashSet<>();
        int totalColors = (sutun * satir) / 3;
        while (renkSeti.size() < totalColors) {
            renkSeti.add(generateRandomColor()); // Farklı renkler ekleniyor
        }
        List<String> renkListesi = new ArrayList<>(renkSeti);

        // Renklerin her bölgeye benzersiz dağılması için bölge başına renkler yerleştirilecek
        List<String> allRenkler = new ArrayList<>(renkListesi);
        Collections.shuffle(allRenkler);  // Renkleri karıştır

        // Her bölgeye yerleştirilmek üzere renkler ayrılacak
        List<String> region1Colors = new ArrayList<>(allRenkler.subList(0, totalColors / 3));
        List<String> region2Colors = new ArrayList<>(allRenkler.subList(1, 2 * totalColors / 3));
        List<String> region3Colors = new ArrayList<>(allRenkler.subList(2 , totalColors));

        // Her bölgeye renkleri yerleştir
        for (int region = 0; region < 3; region++) {
            List<String> regionColors = new ArrayList<>();
            if (region == 0) {
                regionColors = region1Colors;
            } else if (region == 1) {
                regionColors = region2Colors;
            } else {
                regionColors = region3Colors;
            }

            // Bölgedeki kartlara renkleri eşit şekilde yerleştir
            int colorIndex = 0;
            for (int i = 0; i < satir; i++) {
                for (int j = region * regionWidth; j < (region + 1) * regionWidth; j++) {
                    renkler[i][j] = regionColors.get(colorIndex); // Renkleri her bölgeye benzersiz bir şekilde atıyoruz
                    colorIndex = (colorIndex + 1) % regionColors.size();
                }
            }
        }

        // Kartları oluştur ve yerleştir
        for (int i = 0; i < satir; i++) {
            for (int j = 0; j < sutun; j++) {
                JButton kart = new JButton();
                kart.setBackground(Color.LIGHT_GRAY);
                kartKonumlari.put(kart, new Point(i, j));
                kart.addActionListener(this::kartAc);
                kartlar[i][j] = kart;
                frame.add(kart);
            }
        }

        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void kartAc(ActionEvent e) {
        JButton tiklananKart = (JButton) e.getSource();
        Point konum = kartKonumlari.get(tiklananKart);
        if (konum == null || secilenKartlar.contains(tiklananKart)) {
            return;
        }

        // Kartın arka planını renk koduna göre değiştir
        tiklananKart.setBackground(Color.decode(renkler[konum.x][konum.y]));
        secilenKartlar.add(tiklananKart);

        if (secilenKartlar.size() == 3) {
            kontrolEt();
        }
    }

    private void kontrolEt() {
        if (secilenKartlar.size() < 3) return;
        JButton kart1 = secilenKartlar.get(0);
        JButton kart2 = secilenKartlar.get(1);
        JButton kart3 = secilenKartlar.get(2);

        Point p1 = kartKonumlari.get(kart1);
        Point p2 = kartKonumlari.get(kart2);
        Point p3 = kartKonumlari.get(kart3);

        if (p1 == null || p2 == null || p3 == null) return;

        // Eğer 3 kart eşleşiyorsa
        if (renkler[p1.x][p1.y].equals(renkler[p2.x][p2.y]) && renkler[p1.x][p1.y].equals(renkler[p3.x][p3.y])) {
            kart1.setEnabled(false);
            kart2.setEnabled(false);
            kart3.setEnabled(false);
        } else {
            Timer timer = new Timer(500, e -> {
                kart1.setBackground(Color.LIGHT_GRAY);
                kart2.setBackground(Color.LIGHT_GRAY);
                kart3.setBackground(Color.LIGHT_GRAY);
            });
            timer.setRepeats(false);
            timer.start();
        }
        secilenKartlar.clear();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int satir = Integer.parseInt(JOptionPane.showInputDialog("Satır sayısını giriniz:"));
            int sutun = Integer.parseInt(JOptionPane.showInputDialog("Sütun sayısını giriniz (3 ün katı olmalı):"));
            while (sutun % 3 != 0) {
                try {
                    sutun = Integer.parseInt(JOptionPane.showInputDialog("Sütun sayısını giriniz (3'e bölünebilir):"));
                    if (sutun % 3 != 0) {
                        JOptionPane.showMessageDialog(null, "Hata! Sütun sayısı 3'e bölünebilir olmalıdır.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Lütfen geçerli bir sayı giriniz.");
                }
            }
            new MemoryCardGame(satir, sutun);
        });
    }
}
