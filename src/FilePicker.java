import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;

/**
 * Simple component that combines a JButton, JFileChooser, JTextField, and JLabel to create a standard-looking
 * File picker component. Currently used exclusively for loading files without an extension filter,
 * but could easily have methods added for that functionality.
 * @author Josh Hampton hamptojt@mail.uc.edu
 */

public class FilePicker extends JPanel{
    private String textFieldLabel;
    private String buttonLabel;
    private boolean isFileSelected;
    private Path selectedFilePath;

    private Path filePath;
    private JLabel label;
    private JTextField textField;
    private JButton button;

    private JFileChooser fileChooser;

    private int mode;
    public static final int MODE_OPEN = 1;
    public static final int MODE_SAVE = 2;

    public FilePicker(String textFieldLabel, String buttonLabel) {
        this.textFieldLabel = textFieldLabel;
        this.buttonLabel = buttonLabel;
        isFileSelected = false;

        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        label = new JLabel(textFieldLabel);
        textField = new JTextField(25);
        button = new JButton(buttonLabel);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonActionPerformed(e);
            }
        });

        add(label);
        add(textField);
        add(button);
    }

    private void buttonActionPerformed(ActionEvent e) {

        if (mode == MODE_OPEN) {
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                selectedFilePath = fileChooser.getSelectedFile().toPath();
                isFileSelected = true;
            }
        } else if (mode == MODE_SAVE) {
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                selectedFilePath = fileChooser.getSelectedFile().toPath();
                isFileSelected = true;
            }
        }
    }

    /**
     * Allows classes that use FilePicker to add extension filters, so
     * only files of specified types are accepted. Adds one extension per call. Include the
     * . in the extension String
     * @param extension Allowed extension to be added to filter
     * @param description Description of file extension
     */
    public void addFileTypeFilter(String extension, String description) {
        FileTypeFilter filter = new FileTypeFilter(extension, description);
        fileChooser.addChoosableFileFilter(filter);
    }

    /**
     * Inner class that defines some custom FileFilter behavior
     */
    private class FileTypeFilter extends FileFilter {

        private String extension;
        private String description;

        public FileTypeFilter(String extension, String description) {
            this.extension = extension;
            this.description = description;
        }

        @Override
        public boolean accept(File file) {
            if (file.isDirectory()) {
                return true;
            }
            return file.getName().toLowerCase().endsWith(extension);
        }

        public String getDescription() {
            return description + String.format(" (*%s)", extension);
        }
    }

    public void reset() {
        textField.setText("");
        isFileSelected = false;
        Path selectedFilePath = null;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getTextFieldLabel() {
        return this.textFieldLabel;
    }

    public void setTextFieldLabel(String textFieldLabel) {
        this.textFieldLabel = textFieldLabel;
    }

    public String getTextField() {
        return textField.getText();
    }

    public String getButtonLabel() {
        return this.buttonLabel;
    }

    public void setButtonLabel(String buttonLabel) {
        this.buttonLabel = buttonLabel;
    }

    public boolean getIsFileSelected() {
        return isFileSelected;
    }

    public Path getSelectedFilePath() {
        return selectedFilePath;
    }

    public void setSelectedFilePath(Path selectedFilePath) {
        this.selectedFilePath = selectedFilePath;
        this.textField.setText(selectedFilePath.toString());
        this.isFileSelected = true;
    }

    public String getSelectedFilePathString() {
        if (isFileSelected) {
            return textField.getText();
        }
        return null;
    }

    public JFileChooser getFileChooser() {
        return fileChooser;
    }

}
