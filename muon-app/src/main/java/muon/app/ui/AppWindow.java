/**
 *
 */
package muon.app.ui;

import muon.app.App;
import muon.app.ui.components.session.NewSessionDlg;
import muon.app.ui.components.session.SessionContentPanel;
import muon.app.ui.components.session.SessionInfo;
import muon.app.ui.components.session.SessionListPanel;
import muon.app.ui.components.session.files.transfer.BackgroundFileTransfer;
import muon.app.ui.components.session.files.transfer.BackgroundTransferPanel;
import muon.app.ui.components.settings.SettingsDialog;
import muon.app.ui.components.settings.SettingsPageName;
import muon.app.updater.UpdateChecker;
import util.FontAwesomeContants;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static muon.app.App.bundle;
import static util.Constants.*;

/**
 * @author subhro
 *
 */
public class AppWindow extends JFrame {
    private final CardLayout sessionCard;
    private final JPanel cardPanel;
    private final BackgroundTransferPanel uploadPanel;
    private final BackgroundTransferPanel downloadPanel;
    private final Component bottomPanel;
    private SessionListPanel sessionListPanel;
    private JLabel lblUploadCount, lblDownloadCount;
    private JPopupMenu popup;
    private JLabel lblUpdate, lblUpdateText;

    /**
     *
     */
    public AppWindow() {
        super(APPLICATION_NAME);
        try {
            this.setIconImage(ImageIO.read(AppWindow.class.getResource("/muon.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Insets inset = Toolkit.getDefaultToolkit().getScreenInsets(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());

        Dimension screenD = Toolkit.getDefaultToolkit().getScreenSize();

        int screenWidth = screenD.width - inset.left - inset.right;
        int screenHeight = screenD.height - inset.top - inset.bottom;

        if (screenWidth < 1024 || screenHeight < 650 || App.getGlobalSettings().isStartMaximized()) {
            setSize(screenWidth, screenHeight);
        } else {
            int width = (screenWidth * 80) / 100;
            int height = (screenHeight * 80) / 100;
            setSize(width, height);
        }

        this.setLocationRelativeTo(null);

        this.sessionCard = new CardLayout();
        this.cardPanel = new JPanel(this.sessionCard, true);
        this.cardPanel.setDoubleBuffered(true);

        this.add(createSessionPanel(), BorderLayout.WEST);
        this.add(this.cardPanel);


        this.bottomPanel = createBottomPanel();
        this.add(this.bottomPanel, BorderLayout.SOUTH);

        this.uploadPanel = new BackgroundTransferPanel(count -> {
            SwingUtilities.invokeLater(() -> {
                lblUploadCount.setText(count + "");
            });
        });

        this.downloadPanel = new BackgroundTransferPanel(count -> {
            SwingUtilities.invokeLater(() -> {
                lblDownloadCount.setText(count + "");
            });
        });

        new Thread(() -> {
            if (UpdateChecker.isNewUpdateAvailable()) {
                lblUpdate.setText(FontAwesomeContants.FA_DOWNLOAD);
                lblUpdateText.setText("Update available");
            }
        }).start();
    }

    public void createFirstSessionPanel() {
        SessionInfo info = new NewSessionDlg(this).newSession();
        if (info != null) {
            sessionListPanel.createSession(info);
        }
    }

    public void createSession(SessionInfo info) {
        if (info != null) {
            sessionListPanel.createSession(info);
        }
    }

    private JPanel createSessionPanel() {
        JLabel lblSession = new JLabel(bundle.getString("sessions"));
        lblSession.setFont(App.SKIN.getDefaultFont().deriveFont(14.0f));
        JButton btnNew = new JButton(bundle.getString("add"));
        btnNew.setFont(App.SKIN.getDefaultFont().deriveFont(12.0f));
        btnNew.addActionListener(e -> {
            this.createFirstSessionPanel();
        });

        JButton btnSettings = new JButton();
        btnSettings.setFont(App.SKIN.getIconFont().deriveFont(12.0f));
        btnSettings.setText(FontAwesomeContants.FA_COG);
        btnSettings.addActionListener(e -> {
            openSettings(null);
        });

        Box topBox = Box.createHorizontalBox();
        topBox.setBorder(new EmptyBorder(10, 10, 10, 10));
        topBox.add(lblSession);
        topBox.add(Box.createRigidArea(new Dimension(50, 0)));
        topBox.add(Box.createHorizontalGlue());
        topBox.add(btnNew);
        topBox.add(Box.createRigidArea(new Dimension(5, 0)));
        topBox.add(btnSettings);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new MatteBorder(0, 0, 0, 1, App.SKIN.getDefaultBorderColor()));
        panel.add(topBox, BorderLayout.NORTH);

        sessionListPanel = new SessionListPanel(this);
        panel.add(sessionListPanel);

        return panel;

    }

    /**
     * @param sessionContentPanel
     */
    public void showSession(SessionContentPanel sessionContentPanel) {
        cardPanel.add(sessionContentPanel, sessionContentPanel.hashCode() + "");
        sessionCard.show(cardPanel, sessionContentPanel.hashCode() + "");
        revalidate();
        repaint();
    }

    /**
     * @return the sessionListPanel
     */
    public SessionListPanel getSessionListPanel() {
        return sessionListPanel;
    }

    /**
     * @param sessionContentPanel
     */
    public void removeSession(SessionContentPanel sessionContentPanel) {
        cardPanel.remove(sessionContentPanel);
        revalidate();
        repaint();
    }

    private Component createBottomPanel() {
        popup = new JPopupMenu();
        popup.setBorder(new LineBorder(App.SKIN.getDefaultBorderColor(), 1));
        popup.setPreferredSize(new Dimension(400, 500));

        Box b1 = Box.createHorizontalBox();
        b1.setOpaque(true);
        b1.setBackground(App.SKIN.getTableBackgroundColor());
        b1.setBorder(new CompoundBorder(new MatteBorder(1, 0, 0, 0, App.SKIN.getDefaultBorderColor()),
                new EmptyBorder(5, 5, 5, 5)));
        b1.add(Box.createRigidArea(new Dimension(10, 10)));

        MouseListener ml = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(new URI(REPOSITORY_URL));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } catch (URISyntaxException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };


        JLabel lblBrand = new JLabel(APPLICATION_NAME + " " + APPLICATION_VERSION);
        lblBrand.addMouseListener(ml);
        lblBrand.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblBrand.setVerticalAlignment(JLabel.CENTER);
        b1.add(lblBrand);
        b1.add(Box.createRigidArea(new Dimension(10, 10)));

        JLabel lblUrl = new JLabel(REPOSITORY_URL);
        lblUrl.addMouseListener(ml);
        lblUrl.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b1.add(lblUrl);

        b1.add(Box.createHorizontalGlue());

        JLabel lblUpload = new JLabel();
        lblUpload.setFont(App.SKIN.getIconFont().deriveFont(16.0f));
        lblUpload.setText(FontAwesomeContants.FA_CLOUD_UPLOAD);
        b1.add(lblUpload);
        b1.add(Box.createRigidArea(new Dimension(5, 10)));
        lblUploadCount = new JLabel("0");
        b1.add(lblUploadCount);

        lblUpload.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showPopup(uploadPanel, lblUpload);
            }
        });

        lblUploadCount.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showPopup(uploadPanel, lblUpload);
            }
        });

        b1.add(Box.createRigidArea(new Dimension(10, 10)));

        JLabel lblDownload = new JLabel();
        lblDownload.setFont(App.SKIN.getIconFont().deriveFont(16.0f));
        lblDownload.setText(FontAwesomeContants.FA_CLOUD_DOWNLOAD);
        b1.add(lblDownload);
        b1.add(Box.createRigidArea(new Dimension(5, 10)));
        lblDownloadCount = new JLabel("0");
        b1.add(lblDownloadCount);

        lblDownload.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showPopup(downloadPanel, lblDownload);
            }
        });

        lblDownloadCount.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showPopup(downloadPanel, lblDownload);
            }
        });

        b1.add(Box.createRigidArea(new Dimension(30, 10)));

        JLabel lblHelp = new JLabel();
        lblHelp.setFont(App.SKIN.getIconFont().deriveFont(16.0f));

        lblHelp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(new URI(HELP_URL));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } catch (URISyntaxException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        lblHelp.setText(FontAwesomeContants.FA_QUESTION_CIRCLE);
        lblHelp.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b1.add(lblHelp);
        b1.add(Box.createRigidArea(new Dimension(10, 10)));

        lblUpdate = new JLabel();
        lblUpdate.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblUpdate.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openUpdateURL();
            }
        });

        lblUpdate.setFont(App.SKIN.getIconFont().deriveFont(16.0f));
        lblUpdate.setText(FontAwesomeContants.FA_REFRESH);
        b1.add(lblUpdate);

        b1.add(Box.createRigidArea(new Dimension(5, 10)));

        lblUpdateText = new JLabel(bundle.getString("chk_update"));
        lblUpdateText.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblUpdateText.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openUpdateURL();
            }
        });

        b1.add(lblUpdateText);

        b1.add(Box.createRigidArea(new Dimension(10, 10)));

        return b1;
    }

    protected void openUpdateURL() {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(App.UPDATE_URL2));
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void addUpload(BackgroundFileTransfer transfer) {
        this.uploadPanel.addNewBackgroundTransfer(transfer);
    }

    public void addDownload(BackgroundFileTransfer transfer) {
        this.downloadPanel.addNewBackgroundTransfer(transfer);
    }

    private void showPopup(Component panel, Component invoker) {
        popup.removeAll();
        popup.add(panel);
        popup.setInvoker(bottomPanel);

        popup.show(bottomPanel, bottomPanel.getWidth() - popup.getPreferredSize().width,
                -popup.getPreferredSize().height);
    }

    public void removePendingTransfers(int sessionId) {
        this.uploadPanel.removePendingTransfers(sessionId);
        this.downloadPanel.removePendingTransfers(sessionId);
    }

    public void openSettings(SettingsPageName page) {
        SettingsDialog settingsDialog = new SettingsDialog(this);
        settingsDialog.showDialog(this, page);
    }
}
