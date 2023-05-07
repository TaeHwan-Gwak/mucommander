/*
 * This file is part of muCommander, http://www.mucommander.com
 *
 * muCommander is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * muCommander is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mucommander.ui.dialog.file;

import com.mucommander.commons.file.AbstractFile;
import com.mucommander.commons.file.protocol.search.SearchFile;
import com.mucommander.commons.file.util.FileSet;
import com.mucommander.commons.util.ui.dialog.DialogToolkit;
import com.mucommander.commons.util.ui.layout.YBoxPanel;
import com.mucommander.desktop.ActionType;
import com.mucommander.text.Translator;
import com.mucommander.ui.action.ActionProperties;
import com.mucommander.ui.main.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class CopyDialog extends JobDialog implements ActionListener {
    /**
     * Creates a new <code>CopyDialog</code>.
     *
     * @param mainFrame the main frame that spawned this dialog.
     * @param files files to be copied
     */

    private JButton addButton;
    private final static Dimension MINIMUM_DIALOG_DIMENSION = new Dimension(360,0);

    public CopyDialog(MainFrame mainFrame, FileSet files) {
        super(mainFrame, "Git Add", files);

        // - mainFrame
        this.mainFrame = mainFrame;

        YBoxPanel mainPanel = new YBoxPanel();

        // - the base folder is not an archive
        // - the base folder of the to-be-deleted files is not a trash folder or one of its children
        // - the base folder can be moved to the trash (the eligibility conditions should be the same as the files to-be-deleted)
        AbstractFile baseFolder = files.getBaseFolder();
        if (baseFolder.getURL().getScheme().equals(SearchFile.SCHEMA))
            baseFolder = ((SearchFile) baseFolder.getUnderlyingFileObject()).getSearchPlace();

        // - make Jlabel(has text) and then add to mainPanel
        JLabel informationPane = new JLabel("Are you sure to do 'git add'?");
        mainPanel.add(informationPane);
        mainPanel.addSpace(10);

        JPanel fileDetailsPanel = createFileDetailsPanel();

        // Create file details button and add/cancel button and lay them out a single row
        addButton = new JButton(Translator.get("add"));
        JButton cancelButton = new JButton(Translator.get("cancel"));

        mainPanel.add(createButtonsPanel(createFileDetailsButton(fileDetailsPanel),
                DialogToolkit.createOKCancelPanel(addButton, cancelButton, getRootPane(), this)));

        mainPanel.add(fileDetailsPanel);

        getContentPane().add(mainPanel);

        // Give initial keyboard focus to the 'Delete' button
        setInitialFocusComponent(addButton);

        // Call dispose() when dialog is closed
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Size dialog and show it to the screen
        setMinimumSize(MINIMUM_DIALOG_DIMENSION);
        setResizable(false);
    }

    public void actionPerformed(ActionEvent e) {
        // Start by disposing this dialog
        dispose();

        // case press add button
        if(e.getSource()== addButton) {
            try
            {
                String base = files.elementAt(0).getPath();
                String basefile = files.elementAt(0).getName();
                String path = base.replace(basefile, "");

                String addCommand = "git add ";

                for(int i = 0;i<files.size();i++) {
                    // execute git add command
                    String file = files.elementAt(i).getName();
                    String cmd = "cd "+ path + " && " + addCommand + file;

                    Process p;
                    String[] command = {"/bin/sh","-c", cmd};
                    p = Runtime.getRuntime().exec(command);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
