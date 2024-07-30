import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            MyTunes gui = new MyTunes();
            gui.setVisible(true);
        });
    }
}