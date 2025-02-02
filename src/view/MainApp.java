package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import launcher.Main;
import view.utils.TabFactory;
import viewmodel.FileLoadedVM;
import viewmodel.ManagerVM;

import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;


// IMPLEMENTS THE BASIC FUNCTIONALITY OF ALL BUTTONS
public class MainApp {
    private ArrayList<String> clipImgToDelete = new ArrayList<String>();
    
    private ManagerVM vm = new ManagerVM();

    @FXML
    private GridPane rootVBox;

    @FXML
    private TabPane filesTabPane;

    @FXML
    private ComboBox<String> styleComboBox;

    // For traduction
    @FXML
    private Menu menuFile, menuOptions;
    @FXML
    private MenuItem menuItemNewFile, menuItemOpen, menuItemSave, menuItemSaveAs, menuItemHelp, menuItemExportAs;
    @FXML
    private Button boldBtn, italicBtn, codeBtn, quoteBtn; //strikeThroughBtn
    // -----

    private FileEditorControl getSelectedFileControl() {
        var selectedTab = filesTabPane.getSelectionModel().getSelectedItem();
        return (FileEditorControl)selectedTab.getContent();
    }

    private FileLoadedVM getSelectedFileVM() {
        return getSelectedFileControl().getViewModel();
    }

    @FXML
    private void initialize() throws IOException {
        setLocale(Locale.getDefault().toString());

        updateTabPane();
        vm.fileLoadedVMSProperty().addListener((__, old, newV) -> {
            updateTabPane();
        });
        vm.newFile();
//        styleComboBox.itemsProperty().bind(vm.styleListProperty());
//        styleComboBox.valueProperty().bindBidirectional(vm.seletedStyleProperty());
        vm.loadAllStyles();
    }

    private void updateTabPane() {
        filesTabPane.getTabs().clear();
        for (var fileLoadedVM : vm.fileLoadedVMSProperty()) {
            var tab = TabFactory.createTab(vm, fileLoadedVM);
            filesTabPane.getTabs().add(tab);
        }
    }

    /*** MENU ***/
    @FXML
    private void newFile() {
        vm.newFile();
    }

    @FXML
    private void help() throws IOException {
        Main.openHelpWindow();
    }


    @FXML
    private void openFile() throws IOException {
        File file = Main.openExistingMarkdownFile();
        if (file != null) {
            vm.openFile(file);
        }
    }

    @FXML
    private void save() throws IOException {
        try {
            vm.save(getSelectedFileVM());
        } catch (InvalidPathException e) {
            saveAs();
        }
    }

    @FXML
    private void saveAs() throws IOException {
        var file = Main.openSaveFileDialog();
        var fileLoadedVM = getSelectedFileVM();
        if (file != null) {
            vm.saveAs(fileLoadedVM, file);
        }
    }

    @FXML
    private void exportAs() throws IOException {
        var file = Main.openExportFileDialog();
        var fileLoadedVM = getSelectedFileVM();
        if (file != null) {
            vm.exportAs(fileLoadedVM, file);
        }
    }

    /*** EDIT BUTTONS ***/

    @FXML
    private void bold() {
        var selectedText = getSelectedFileControl().getSelection();
        var caretDifference = getSelectedFileVM().setBold(selectedText.getStart(), selectedText.getEnd());
        getSelectedFileControl().resetSelection(selectedText, caretDifference);
    }

    @FXML
    private void strikeThrough() {
        var selectedText = getSelectedFileControl().getSelection();
        var caretDifference = getSelectedFileVM().setStrikeThrough(selectedText.getStart(), selectedText.getEnd());
        getSelectedFileControl().resetSelection(selectedText, caretDifference);
    }

    @FXML
    private void italic() {
        var selectedText = getSelectedFileControl().getSelection();
        var caretDifference = getSelectedFileVM().setItalic(selectedText.getStart(), selectedText.getEnd());
        getSelectedFileControl().resetSelection(selectedText, caretDifference);
    }

    @FXML
    private void highlight() {
        var selectedText = getSelectedFileControl().getSelection();
        var caretDifference = getSelectedFileVM().setHighlight(selectedText.getStart(), selectedText.getEnd());
        getSelectedFileControl().resetSelection(selectedText, caretDifference);
    }

    @FXML
    private void code() {
        var selectedText = getSelectedFileControl().getSelection();
        var caretDifference = getSelectedFileVM().setCode(selectedText.getStart(), selectedText.getEnd());
        getSelectedFileControl().resetSelection(selectedText, caretDifference);
    }

    @FXML
    private void quote() {
        getSelectedFileVM().setQuote(getCurrentLine());
    }

    @FXML
    private void image() throws IOException {
        var values = Main.openLinkImagePicker("Image not found", true);
        if (values == null) { return; }

        getSelectedFileVM().setImage(getSelectedFileControl().getCaretPosition(), values[0], values[1]);
    }
    
    public ArrayList<String> returnAmount() {
        return clipImgToDelete;
    }
    @FXML
    private void clipboardImage() throws Exception {  
        int index = 0;
        while(new File("/tmp/temporaryImage"+index+".png").exists()) {
            index++;
        }
        String outputfile="/tmp/temporaryImage"+index+".png";
        copyTo(outputfile);
        //var values = Main.openLinkImagePicker("Image not found", true);
        //if (values == null) { return; }
        String replacementText = "Image not found";
        getSelectedFileVM().setImage(getSelectedFileControl().getCaretPosition(), replacementText, "file:///"+outputfile);
    }

    int copyTo(String filename) throws Exception {
        Transferable content = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        if(content==null){
            System.err.println("error: nothing found in clipboard");
            return 1;
        }
        if(!content.isDataFlavorSupported(DataFlavor.imageFlavor)){
            System.err.println("error: incorrect type of data found in clipboard");
            return 2;
        }
        BufferedImage img = (BufferedImage)content.getTransferData(DataFlavor.imageFlavor);
        String ext = ext(filename);
        if(ext==null){
            ext="png";
            filename+="."+ext;
        }
        File outfile=new File(filename);
        ImageIO.write(img,ext,outfile);
        System.err.println("image copied to: " + outfile.getAbsolutePath());
        clipImgToDelete.add(filename);
        return 0;
    }
    static String ext(String filename){
        int pos=filename.lastIndexOf('.')+1;
        if(pos==0 || pos >= filename.length() )return null;
        return filename.substring(pos);
    }
    
    @FXML
    private void link() throws IOException {
        var selectedText = getSelectedFileControl().getSelection();
        var text = getSelectedFileVM().getMarkDownText().substring(selectedText.getStart(), selectedText.getEnd());

        var values = Main.openLinkImagePicker(text, false);
        if (values == null) { return; }

        getSelectedFileVM().setLink(selectedText.getStart(), selectedText.getEnd(), values[0], values[1]);
        getSelectedFileControl().resetCaretPosition(selectedText.getStart());
    }

    @FXML
    private void olList() {
        var caretPosition = getSelectedFileControl().getCaretPosition();
        var caretDifference = getSelectedFileVM().addOlListLine(getCurrentLine());
        caretPosition += caretDifference;
        getSelectedFileControl().resetCaretPosition(caretPosition);
    }

    @FXML
    private void ulList() {
        var caretPosition = getSelectedFileControl().getCaretPosition();
        var caretDifference = getSelectedFileVM().addUlListLine(getCurrentLine());
        caretPosition += caretDifference;
        getSelectedFileControl().resetCaretPosition(caretPosition);
    }

    @FXML
    private void title(ActionEvent event) {
        var caretPosition = getSelectedFileControl().getCaretPosition();
        var node = (Node)event.getSource();
        String data = (String)node.getUserData();
        int number = Integer.parseInt(data);
        getSelectedFileVM().setTitle(getCurrentLine(),  number);
        getSelectedFileControl().resetCaretPosition(caretPosition);
    }

    @FXML
    private void export(ActionEvent event) throws IOException {
        var node = (Node)event.getSource();
        String exportFormat = (String)node.getUserData();

        var file = Main.openExportFileDialog(exportFormat);
        var fileLoadedVM = getSelectedFileVM();
        if (file != null) {
            vm.exportAs(fileLoadedVM, file);
        }
    }


    /*** UTILS ***/

    private int getCaretPosition() {
        return getSelectedFileControl().getCaretPosition();
    }
    private int getCurrentLine() {
        return getSelectedFileControl().getCurrentLine();
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        ResourceBundle bundle = ResourceBundle.getBundle("config.lang", locale);

        menuFile.setText(String.join(" ", "📁", bundle.getString("menuFile")));
        menuItemHelp.setText(String.join(" ", "❔", bundle.getString("menuHelp")));
        menuOptions.setText(String.join(" ", "🔧", bundle.getString("menuOptions")));
        menuItemNewFile.setText(String.join(" ", "📄", bundle.getString("menuItemNewFile")));
        menuItemOpen.setText(String.join(" ", "📂", bundle.getString("menuItemOpen")));
        menuItemSave.setText(String.join(" ", "💾", bundle.getString("menuItemSave")));
        menuItemSaveAs.setText(String.join(" ", "💾", bundle.getString("menuItemSaveAs")));
        menuItemExportAs.setText(String.join(" ", "💾", bundle.getString("menuItemExportAs")));

        boldBtn.setText(bundle.getString("boldBtn"));
        italicBtn.setText(bundle.getString("italicBtn"));
//        strikeThroughBtn.setText(bundle.getString("strikeThrough"));
        codeBtn.setText(bundle.getString("codeBtn"));
        quoteBtn.setText(bundle.getString("quoteBtn"));
    }
}
