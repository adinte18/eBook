package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//FOR THIS, USE JAVAFX SCENE BUILDER - MORE EASIER TO DO THE DESIGN AND TO SET HANDLERS
public class Controller extends gutindexParser implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private Button add_button;

    @FXML
    private ListView<String> list_view;

    @FXML
    private ListView<String> mes_livres;

    @FXML
    private Button button_download;

    @FXML
    private TextField searchBar;

    ObservableList<String> id;
    ObservableList<String> files;

    //this gets data when we click an item from our list
    //we extract the ID and use it for URL creation
    @FXML public URL handleMouseClick() throws IOException {

        if (list_view.getSelectionModel().getSelectedItem() != null)
        {
            button_download.setDisable(false);

            final String id_regex = "\\d+(?= TITLE)";

            String extracted_data = list_view.getSelectionModel().getSelectedItem();

            Pattern pattern = Pattern.compile(id_regex, Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(extracted_data);

            String extracted_id;

            while(matcher.find())
            {
                extracted_id = matcher.group();
                return setURL(extracted_id);
            }

            System.out.println("not found");
            return null;
        }
        return null;
    }

    @FXML public File getBook() {
        String data;
        if (mes_livres.getSelectionModel().getSelectedItem() != null)
        {
            add_button.setDisable(false);
            data = mes_livres.getSelectionModel().getSelectedItem();
            return new File("downloads/" + data + ".txt");
        }
        return null;
    }

    public void switchToScene1(ActionEvent event) throws IOException
    {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("scene2.fxml"));
        Parent root = loader.load();

        Scene2Controller controller = loader.getController();

        controller.writeTA(writeToTextArea(getBook()));

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);

        stage.show();
    }

    //this method is the handler for Download button
    //it downloads the text to a TXT file, and stores it on users PC
    public void downloadButton(ActionEvent event) throws IOException {
        URL url = handleMouseClick();
        InputStream inputStream = url.openStream();
        String title = getTtile(list_view);

        File text = new File("downloads/" + title + ".txt");
        if (text.createNewFile())
        {
            gutindexParser g = new gutindexParser();
            System.out.println("Created new file!");
            ObservableList<String> upd = g.getFiles();
            mes_livres.setItems(upd);
        }
        else System.out.println("File exists");

        String pathname = "downloads/" + text.getName();
        Files.copy(inputStream, Paths.get(pathname), StandardCopyOption.REPLACE_EXISTING);
    }

    //filter search
    public void search(String oldVal, String newVal)
    {
        if (oldVal == null || newVal.length() < oldVal.length())
        {
            list_view.setItems(id);
        }
        else
        {
            newVal = newVal.toUpperCase();
            ObservableList<String> subentries = FXCollections.observableArrayList();
            for (Object entry : list_view.getItems())
            {
                String entryText = (String) entry;
                if (entryText.toUpperCase().contains(newVal))
                {
                    subentries.add(entryText);
                }
            }
            list_view.setItems(subentries);
        }
    }

    public String writeToTextArea(File file) throws IOException {
        String SOURCE_FILE = file.getAbsolutePath();

        if (file != null) {
            return readFile(SOURCE_FILE);
        } else {
            System.out.print("Not valid file");
        }
        return null;
    }

    private void loadBooks()
    {
        URL gutindex = null;
        try {
            gutindex = new URL("https://www.gutenberg.org/dirs/GUTINDEX.ALL");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        gutindexParser g = new gutindexParser();
        try {
            id = g.getAllID(g.connect(gutindex));
        } catch (IOException e) {
            e.printStackTrace();
        }
        list_view.setItems(id);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        gutindexParser g = new gutindexParser();
        mes_livres.setItems(g.getFiles());

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                return null;
            }

            @Override
            public void run() {
                loadBooks();
            }
        };

        Thread backgroundThread = new Thread(task);
        backgroundThread.setDaemon(true);

        backgroundThread.start();

        File dir = new File("downloads");
        dir.mkdir();
        add_button.setDisable(true);
        button_download.setDisable(true);

        searchBar.setPromptText("Search");
        searchBar.textProperty().addListener(new ChangeListener()
        {
            @Override
            public void changed(ObservableValue observableValue, Object o, Object t1) {
                search((String)o , (String)t1);
            }
        });
    }
}
