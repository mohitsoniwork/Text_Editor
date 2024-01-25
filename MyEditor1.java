import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;

class MyEditor1 implements ActionListener {

    TextArea ta;
    Frame f1, findFrame;
    Label findLabel;
    TextField findTextField;
    Button findButton, nextButton, prevButton;
    MenuBar mb;
    Menu m1, m2, m3;
    MenuItem nw, opn, sve, ext, fnd, fndrep, fon;
    CheckboxMenuItem bld, itlc;
    HashMap<String, String> findReplaceMap;

    int currentIndex = -1;

    public MyEditor1() {
        f1 = new Frame();
        f1.setSize(400, 400);
        mb = new MenuBar();
        m1 = new Menu("File");
        m2 = new Menu("Edit");
        m3 = new Menu("Others");

        nw = new MenuItem("New");
        opn = new MenuItem("Open");
        sve = new MenuItem("Save");
        ext = new MenuItem("Exit");
        fnd = new MenuItem("Find");
        fndrep = new MenuItem("Find & Replace");
        fon = new MenuItem("Font");

        nw.addActionListener(this);
        opn.addActionListener(this);
        sve.addActionListener(this);
        ext.addActionListener(this);
        fnd.addActionListener(this);
        fndrep.addActionListener(this);
        fon.addActionListener(this);

        bld = new CheckboxMenuItem("Bold");
        itlc = new CheckboxMenuItem("Italic");
        itlc.setState(true);

        m3.add(fnd);
        m3.add(fndrep);
        m3.add(fon);
        m3.addSeparator();
        m2.add(bld);
        m2.add(itlc);
        m2.addSeparator();
        m2.add(m3);
        m1.add(nw);
        m1.add(opn);
        m1.add(sve);
        m1.addSeparator();
        m1.add(ext);
        mb.add(m1);
        mb.add(m2);
        f1.setMenuBar(mb);
        f1.setVisible(true);

        ta = new TextArea();
        Font font = new Font("Arial", Font.PLAIN, 25);
        ta.setFont(font);
        f1.add(ta);

        findReplaceMap = new HashMap<>();
    }

    public void actionPerformed(ActionEvent e) {
        String str = e.getActionCommand();
        if (str.equals("New")) {
            ta.setText(""); // Clear existing text
        } else if (str.equals("Open")) {
            FileDialog fd = new FileDialog(f1, "Open", FileDialog.LOAD);
            fd.setVisible(true);
            String directory = fd.getDirectory();
            String filename = fd.getFile();
            if (directory != null && filename != null) {
                try (FileInputStream fis = new FileInputStream(directory + filename)) {
                    int fileSize = (int) new File(directory + filename).length();
                    byte[] contentBytes = new byte[fileSize];
                    fis.read(contentBytes);
                    String content = new String(contentBytes);
                    ta.setText(content);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } else if (str.equals("Save")) {
            FileDialog fd = new FileDialog(f1, "Save", FileDialog.SAVE);
            fd.setVisible(true);
            String directory = fd.getDirectory();
            String filename = fd.getFile();
            if (directory != null && filename != null) {
                try (FileOutputStream fos = new FileOutputStream(directory + filename)) {
                    String content = ta.getText();
                    byte[] contentBytes = content.getBytes();
                    fos.write(contentBytes);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } else if (str.equals("Exit")) {
            System.exit(0);
        } else if (str.equals("Find")) {
            createFindFrame();
        } else if (str.equals("Find & Replace")) {
            // Implement find and replace functionality
            String findText = ta.getSelectedText();
            if (findText != null && !findText.isEmpty()) {
                String replaceText = findReplaceMap.get(findText);
                if (replaceText != null) {
                    ta.replaceRange(replaceText, ta.getSelectionStart(), ta.getSelectionEnd());
                }
            }
        }
    }

    private void createFindFrame() {
        findFrame = new Frame("Find");
        findFrame.setSize(300, 150);

        findLabel = new Label("Find:");
        findTextField = new TextField(20);
        findButton = new Button("Find");
        nextButton = new Button("Next");
        prevButton = new Button("Previous");

        findButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                findText();
            }
        });

        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                findNext();
            }
        });

        prevButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                findPrevious();
            }
        });

        Panel panel = new Panel();
        panel.add(findLabel);
        panel.add(findTextField);
        panel.add(findButton);
        panel.add(nextButton);
        panel.add(prevButton);

        findFrame.add(panel);
        findFrame.setVisible(true);
    }

    private void findText() {
        String searchText = findTextField.getText();
        if (searchText != null && !searchText.isEmpty()) {
            String content = ta.getText();
            currentIndex = content.indexOf(searchText);
            if (currentIndex != -1) {
                ta.select(currentIndex, currentIndex + searchText.length());
            } else {
                currentIndex = -1; // Reset index if not found
            }
        }
    }

    private void findNext() {
        if (currentIndex != -1) {
            String searchText = findTextField.getText();
            String content = ta.getText();
            currentIndex = content.indexOf(searchText, currentIndex + 1);
            if (currentIndex != -1) {
                ta.select(currentIndex, currentIndex + searchText.length());
            }
        }
    }

    private void findPrevious() {
        if (currentIndex != -1) {
            String searchText = findTextField.getText();
            String content = ta.getText();
            currentIndex = content.lastIndexOf(searchText, currentIndex - 1);
            if (currentIndex != -1) {
                ta.select(currentIndex, currentIndex + searchText.length());
            }
        }
    }

    public static void main(String args[]) {
        MyEditor1 me = new MyEditor1();
        WindowCloser wc = new WindowCloser();
        me.f1.addWindowListener(wc);
    }
}

class WindowCloser extends WindowAdapter {
    public void windowClosing(WindowEvent e) {
        Window w = e.getWindow();
        w.setVisible(false);
        w.dispose();
        System.exit(0);
    }
}
