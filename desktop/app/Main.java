package app;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import data.Db;
import screens.AppFrame;

/*
 * Entry point. Open the database, then show the login window on the swing
 * thread. Thats it, everything else happens from the screens.
 */
public class Main {

    public static void main(String[] args) {
        // open db / create tables up front so the first screen is ready
        Db.connect();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // not a big deal, just use the default look
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                AppFrame frame = new AppFrame();
                frame.showLogin();
                frame.setVisible(true);
            }
        });
    }
}
