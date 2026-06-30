package screens;

import java.awt.GridBagLayout;

import javax.swing.JPanel;

import ui.Theme;
import ui.UiHelper;

/*
 * Temporary screen we show for parts of the app that arent built yet, so the
 * navigation already works while we fill the rest in. Gets removed once every
 * real screen exists.
 */
public class Placeholder extends JPanel {

    public Placeholder(String name) {
        setBackground(Theme.BG);
        setLayout(new GridBagLayout()); // centers the single child
        add(UiHelper.title(name + " — coming soon", 24));
    }
}
