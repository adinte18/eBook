package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Scene2Controller implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private TextArea text_area;

    @FXML
    private ToggleButton italic_font;

    @FXML
    private ToggleButton bold_font;

    @FXML
    private ComboBox<String> selectFont;

    @FXML
    private ComboBox<Integer> font_size;

    @FXML
    private TableView<Commentaire> tab_view;

    @FXML
    private TableColumn<Commentaire, String> citat;

    @FXML
    private TableColumn<Commentaire, String> comm;

    @FXML
    private TextArea commentaire;

    @FXML
    private Button add;

    @FXML
    private Button delete;

    ObservableList<String> fonts = FXCollections.observableArrayList(Font.getFontNames());
    ObservableList<Integer> size = FXCollections.observableArrayList();

    public void addCommentaire()
    {
        String citation = text_area.getSelectedText();
        String comm = commentaire.getText();
        Commentaire c = new Commentaire(comm, citation);
        tab_view.getItems().add(c);
    }

    public void deleteCommentaire()
    {
        tab_view.getItems().removeAll(tab_view.getSelectionModel().getSelectedItem());
    }

    public ObservableList<Integer> populate()
    {
        for (int i = 0; i < 100; i++)
        {
            size.add(i);
        }
        return size;
    }

    public void changeFont(ActionEvent event)
    {
        text_area.getText();

        text_area.setFont(new Font(selectFont.getValue(), font_size.getValue()));
    }

    public void changeFontSize(ActionEvent event)
    {
        text_area.getText();
        text_area.setStyle("-fx-font-size: " + font_size.getValue());
    }

    public void writeTA(String t)
    {
        text_area.setText(t);
    }

    public void switchToMainMenu(ActionEvent event) throws IOException
    {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void setFontSize()
    {
        font_size.setItems(populate());
        font_size.getSelectionModel().select(14);
    }

    public void bold_italic(ActionEvent event)
    {
        if (text_area.getText().isEmpty())
        {
            System.out.println("No text");
            ObservableList<String> fonts = FXCollections.observableArrayList(Font.getFontNames());
            System.out.println(fonts.toString());
        }
        else
        {
            if (italic_font.isSelected() && bold_font.isSelected())
            {
                text_area.setStyle("-fx-font-weight: bold; -fx-font-style: italic");
                text_area.setFont(new Font(selectFont.getValue(), font_size.getValue()));
            }
            else if (italic_font.isSelected() && !bold_font.isSelected())
            {
                text_area.setStyle("-fx-font-style: italic");
                System.out.println("Italic " + selectFont.getValue());
                System.out.println(font_size.getValue());
                text_area.setFont(new Font(selectFont.getValue(), font_size.getValue()));
            }
            else if(bold_font.isSelected() && !italic_font.isSelected())
            {
                text_area.setStyle("-fx-font-weight: bold");
                System.out.println("Bold " + selectFont.getValue());
                System.out.println(font_size.getValue());
                text_area.setFont(new Font(selectFont.getValue(), font_size.getValue()));
            }
            else text_area.setStyle("-fx-font-weight: regular");
        }
    }

    public void setFonts()
    {
        text_area.setWrapText(true);
        text_area.setEditable(false);
        selectFont.setItems(fonts);
        selectFont.getSelectionModel().selectFirst();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {

        citat.setCellValueFactory(new PropertyValueFactory<Commentaire, String>("citat"));
        comm.setCellValueFactory(new PropertyValueFactory<Commentaire, String>("comm"));
        setFonts();
        setFontSize();
    }
}
