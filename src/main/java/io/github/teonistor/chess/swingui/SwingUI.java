package io.github.teonistor.chess.swingui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SwingUI extends JFrame {

    public SwingUI() throws HeadlessException {
        super("Chess");

        final MenuBar bar = new MenuBar();
        final Menu menu = new Menu("File");
        addMenuItem(menu, "New", this::todo);
        addMenuItem(menu, "Load", this::todo);
        addMenuItem(menu, "Save", this::todo);
        menu.addSeparator();
        addMenuItem(menu, "Exit", this::exit);
        bar.add(menu);
        setMenuBar(bar);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(640, 480));
        setVisible(true);
    }

    private void addMenuItem(Menu menu, String label, ActionListener listener) {
        final MenuItem item = new MenuItem(label);
        item.addActionListener(listener);
        menu.add(item);
    }

    private void todo(ActionEvent e) {

    }

    private void exit(ActionEvent ignore) {
        dispose();
    }

    public static void main(String[] args) {
        new SwingUI();
    }
}
