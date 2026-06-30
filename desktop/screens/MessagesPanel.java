package screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import data.MessageDao;
import data.UserDao;
import model.Message;
import model.User;
import ui.RoundedButton;
import ui.RoundedPanel;
import ui.Theme;
import ui.UiHelper;

/*
 * Direct messages, 1 on 1. Left side lists the people you've talked to, right
 * side is the open conversation with a box to type in. Not real time, it just
 * reloads when you send something.
 */
public class MessagesPanel extends JPanel {

    private MainWindow main;
    private User user;
    private MessageDao messageDao = new MessageDao();
    private UserDao userDao = new UserDao();

    private String selectedPartner = null;
    private JPanel centerHolder;
    private JPanel partnersHolder;
    private JTextField input = new JTextField();

    public MessagesPanel(MainWindow main, User user) {
        this.main = main;
        this.user = user;

        setLayout(new BorderLayout());
        setBackground(Theme.BG);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 24));

        add(buildLeft(), BorderLayout.WEST);

        centerHolder = new JPanel(new BorderLayout());
        centerHolder.setOpaque(false);
        centerHolder.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 0));
        add(centerHolder, BorderLayout.CENTER);

        refreshPartners();
        showConversation();
    }

    private JPanel buildLeft() {
        JPanel left = new JPanel(new BorderLayout());
        left.setOpaque(false);
        left.setPreferredSize(new Dimension(230, 10));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        top.add(UiHelper.title("Messages", 20), BorderLayout.WEST);
        JLabel plus = new JLabel("＋");
        plus.setFont(Theme.heading(20));
        plus.setForeground(Theme.LILAC_600);
        plus.setCursor(new Cursor(Cursor.HAND_CURSOR));
        plus.setToolTipText("New message");
        plus.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                newConversation();
            }
        });
        top.add(plus, BorderLayout.EAST);
        left.add(top, BorderLayout.NORTH);

        partnersHolder = new JPanel();
        partnersHolder.setOpaque(false);
        partnersHolder.setLayout(new BoxLayout(partnersHolder, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(partnersHolder);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        left.add(scroll, BorderLayout.CENTER);
        return left;
    }

    private void refreshPartners() {
        partnersHolder.removeAll();
        ArrayList<String> partners = messageDao.getPartners(user.getUsername());
        if (partners.isEmpty()) {
            partnersHolder.add(UiHelper.muted("No chats yet. Tap + to start one.", 12));
        }
        for (int i = 0; i < partners.size(); i++) {
            partnersHolder.add(buildPartnerRow(partners.get(i)));
            partnersHolder.add(UiHelper.vgap(6));
        }
        partnersHolder.revalidate();
        partnersHolder.repaint();
    }

    private JPanel buildPartnerRow(final String partner) {
        boolean active = partner.equals(selectedPartner);
        RoundedPanel row = new RoundedPanel(12, active ? Theme.LILAC_100 : Theme.WHITE);
        row.setLayout(new BorderLayout(10, 0));
        row.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        row.setMaximumSize(new Dimension(210, 50));
        row.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel avatar = new JLabel("🙂");
        row.add(avatar, BorderLayout.WEST);
        JLabel name = new JLabel(partner);
        name.setFont(Theme.bodyBold(13));
        name.setForeground(Theme.INK);
        row.add(name, BorderLayout.CENTER);

        row.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                selectedPartner = partner;
                refreshPartners();
                showConversation();
            }
        });
        return row;
    }

    private void showConversation() {
        centerHolder.removeAll();
        if (selectedPartner == null) {
            JPanel hint = new JPanel(new java.awt.GridBagLayout());
            hint.setOpaque(false);
            hint.add(UiHelper.muted("Pick a chat or start a new one to begin.", 14));
            centerHolder.add(hint, BorderLayout.CENTER);
        } else {
            centerHolder.add(buildChatHeader(), BorderLayout.NORTH);
            centerHolder.add(buildBubbles(), BorderLayout.CENTER);
            centerHolder.add(buildInput(), BorderLayout.SOUTH);
        }
        centerHolder.revalidate();
        centerHolder.repaint();
    }

    private JPanel buildChatHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 4, 12, 0));
        JLabel name = UiHelper.title(selectedPartner, 18);
        header.add(name, BorderLayout.WEST);
        return header;
    }

    private JScrollPane buildBubbles() {
        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        ArrayList<Message> msgs = messageDao.getConversation(user.getUsername(), selectedPartner);
        for (int i = 0; i < msgs.size(); i++) {
            Message m = msgs.get(i);
            boolean mine = m.getSender().equals(user.getUsername());
            list.add(buildBubble(m.getBody(), mine));
            list.add(UiHelper.vgap(6));
        }

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JPanel buildBubble(String text, boolean mine) {
        JPanel rowWrap = new JPanel(new FlowLayout(mine ? FlowLayout.RIGHT : FlowLayout.LEFT, 0, 0));
        rowWrap.setOpaque(false);
        rowWrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        RoundedPanel bubble = new RoundedPanel(14, mine ? Theme.LILAC_500 : Theme.WHITE);
        bubble.setLayout(new BorderLayout());
        bubble.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        JLabel label = new JLabel("<html><div style='width:320px'>" + safe(text) + "</div></html>");
        label.setFont(Theme.body(14));
        label.setForeground(mine ? Color.WHITE : Theme.INK);
        bubble.add(label, BorderLayout.CENTER);

        rowWrap.add(bubble);
        return rowWrap;
    }

    private JPanel buildInput() {
        JPanel box = new JPanel(new BorderLayout(10, 0));
        box.setOpaque(false);
        box.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        input.setText("");
        input.setFont(Theme.body(14));
        JPanel fieldBox = UiHelper.field(input);
        box.add(fieldBox, BorderLayout.CENTER);

        RoundedButton send = new RoundedButton("Send", Theme.LILAC_500, Color.WHITE);
        send.setPreferredSize(new Dimension(100, 46));
        send.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        input.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        box.add(send, BorderLayout.EAST);
        return box;
    }

    private void sendMessage() {
        String text = input.getText().trim();
        if (text.isEmpty() || selectedPartner == null) {
            return;
        }
        messageDao.send(new Message(user.getUsername(), selectedPartner, text));
        refreshPartners();
        showConversation();
    }

    private void newConversation() {
        String name = JOptionPane.showInputDialog(this, "Who do you want to message? (username)");
        if (name == null) {
            return;
        }
        name = name.trim();
        if (name.isEmpty()) {
            return;
        }
        if (name.equals(user.getUsername())) {
            JOptionPane.showMessageDialog(this, "You can't message yourself :)");
            return;
        }
        User other = userDao.findByUsername(name);
        if (other == null) {
            JOptionPane.showMessageDialog(this, "No user called '" + name + "'.");
            return;
        }
        selectedPartner = name;
        refreshPartners();
        showConversation();
    }

    private String safe(String s) {
        if (s == null) {
            return "";
        }
        s = s.replace("&", "&amp;");
        s = s.replace("<", "&lt;");
        s = s.replace(">", "&gt;");
        return s;
    }
}
