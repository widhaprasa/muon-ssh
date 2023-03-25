/**
 *
 */
package com.jediterm.terminal.ui;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author subhro
 *
 */
public class SearchTextField extends JTextField {
    /**
     *
     */
    public SearchTextField() {
        super();
        installPopUp();
    }

    public SearchTextField(int cols) {
        super(cols);
        installPopUp();
    }

    private void installPopUp() {
        this.putClientProperty("flat.popup", createPopup());
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Right click on text field");
                if (e.getButton() == MouseEvent.BUTTON3 || e.isPopupTrigger()) {

                    JPopupMenu pop = (JPopupMenu) SearchTextField.this
                            .getClientProperty("flat.popup");
                    if (pop != null) {
                        pop.show(SearchTextField.this, e.getX(), e.getY());
                    }
                }
            }
        });
    }

    private JPopupMenu createPopup() {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem mCut = new JMenuItem("Cut");
        JMenuItem mCopy = new JMenuItem("Copy");
        JMenuItem mPaste = new JMenuItem("Paste");
        JMenuItem mSelect = new JMenuItem("Select All");

        popup.add(mCut);
        popup.add(mCopy);
        popup.add(mPaste);
        popup.add(mSelect);

        mCut.addActionListener(e -> {
            cut();
        });

        mCopy.addActionListener(e -> {
            copy();
        });

        mPaste.addActionListener(e -> {
            paste();
        });

        mSelect.addActionListener(e -> {
            selectAll();
        });

        return popup;
    }
}
