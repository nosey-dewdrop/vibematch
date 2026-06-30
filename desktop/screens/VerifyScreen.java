package screens;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import model.User;
import service.AuthService;
import ui.RoundedButton;
import ui.Session;
import ui.Theme;
import ui.UiHelper;
import util.EmailSender;

/*
 * Where the user types the code we emailed them. The expected code is passed in
 * from the register screen. If they get it right we mark them verified and send
 * them into onboarding.
 */
public class VerifyScreen extends JPanel {

    private AppFrame appFrame;
    private AuthService auth = new AuthService();
    private User user;
    private String expectedCode;

    private JTextField codeField = new JTextField();
    private JLabel errorLabel = new JLabel(" ");

    public VerifyScreen(AppFrame appFrame, User user, String code) {
        this.appFrame = appFrame;
        this.user = user;
        this.expectedCode = code;

        JPanel form = new JPanel();
        form.setBackground(Theme.WHITE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        JLabel title = UiHelper.title("Enter your code", 26);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(title);
        form.add(UiHelper.vgap(6));

        JLabel sub = UiHelper.muted("We sent a 6 digit code to " + user.getEmail(), 13);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(sub);
        form.add(UiHelper.vgap(22));

        // the code box. big spaced font so it feels like a code input.
        codeField.setFont(Theme.heading(26));
        codeField.setHorizontalAlignment(JTextField.CENTER);
        JPanel codeBox = UiHelper.field(codeField);
        codeBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        codeBox.setMaximumSize(new Dimension(220, 60));
        form.add(codeBox);
        form.add(UiHelper.vgap(10));

        errorLabel.setFont(Theme.body(12));
        errorLabel.setForeground(new Color(0xC0, 0x39, 0x4B));
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(errorLabel);
        form.add(UiHelper.vgap(8));

        RoundedButton btn = UiHelper.primaryButton("Verify and continue");
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doVerify();
            }
        });
        form.add(btn);
        form.add(UiHelper.vgap(16));

        form.add(buildResend());

        AuthShell shell = new AuthShell(
            "Check your<br>inbox 📬",
            "The code is on its way to your Bilkent mail.",
            new Color(0x5C, 0xC0, 0x9A),
            form);
        setLayout(new java.awt.BorderLayout());
        add(shell);
    }

    private JPanel buildResend() {
        JPanel row = new JPanel();
        row.setBackground(Theme.WHITE);
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(UiHelper.muted("Didn't get it? ", 13));
        JLabel link = new JLabel("Resend code");
        link.setFont(Theme.bodyBold(13));
        link.setForeground(Theme.LILAC_600);
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));
        link.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                resend();
            }
        });
        row.add(link);
        return row;
    }

    private void doVerify() {
        errorLabel.setText(" ");
        String typed = codeField.getText().trim();
        if (typed.equals(expectedCode)) {
            auth.markVerified(user.getUsername());
            user.setVerified(true);
            Session.setUser(user);
            appFrame.routeAfterLogin(user);
        } else {
            errorLabel.setText("That code isn't right, check again.");
        }
    }

    private void resend() {
        expectedCode = auth.generateVerificationCode();
        boolean sent = EmailSender.sendVerificationCode(user.getEmail(), expectedCode);
        if (!sent) {
            JOptionPane.showMessageDialog(this,
                "Here is your new code:\n\n   " + expectedCode,
                "Verification code",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
