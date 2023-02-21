package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class gutindexParser {

    public gutindexParser()
    {

    }

    public String readFile(HttpURLConnection httpcon) throws IOException {
        InputStream inputStream = httpcon.getInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder sb = new StringBuilder();
        String line = reader.readLine();

        while (line != null)
        {
            sb.append(line);
            sb.append("\n");
            line = reader.readLine();
        }

        return sb.toString();
    }

    public String readFile(String filepath) throws IOException {
        String data = "";

        data = new String(Files.readAllBytes(Paths.get(filepath)));

        return data;
    }

    public HttpURLConnection connect(URL url) throws IOException {
        HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
        httpcon.setRequestMethod("GET");

        int status = httpcon.getResponseCode();

        if (status == 200)
        {
            System.out.println("Connected");
            return httpcon;
        }
        else System.out.println("Nope");
        return httpcon;
    }

    public URL setURL(String id) throws IOException {
        URL url = new URL("https://www.gutenberg.org/files/" + id + "/" + id + ".txt");
        HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
        if (httpcon.getResponseCode() != 200)
        {
            url = new URL("https://www.gutenberg.org/files/" + id + "/" + id + "-0" + ".txt");

            HttpURLConnection httpcon2 = (HttpURLConnection) url.openConnection();

            if (httpcon2.getResponseCode() != 200)
            {
                url = new URL("https://www.gutenberg.org/files/" + id + "/" + id + "-8" + ".txt");
                return url;
            }

            return url;
        }
        return url;
    }

    public String getTtile(ListView<String> list_view) throws IOException {

        final String id_title = "(?<=TITLE: )(.*?)(?=, )";

        String extracted_data = list_view.getSelectionModel().getSelectedItem();

        Pattern pattern = Pattern.compile(id_title, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(extracted_data);

        String extracted_title;

        while(matcher.find())
        {
            extracted_title = matcher.group();
            return extracted_title;
        }

        System.out.println("not found");
        return null;
    }

    public ObservableList<String> getFiles()
    {
        ObservableList<String> p = FXCollections.observableArrayList();

        File f = new File("downloads");

        if (f.exists())
        {
            File[] files = f.listFiles();

            for (File file : files)
            {
                if (file.isFile())
                {
                    p.add(file.getName().replaceFirst("[.][^.]+$", ""));
                }
            }
        }
        else
        {
            f.mkdir();
        }
        return p;
    }

    public ObservableList<String> getAllID(HttpURLConnection httpcon) throws IOException {
        ObservableList<String> id = FXCollections.observableArrayList();
        //reading GUTINDEX.ALL
        String result = readFile(httpcon);

        //regex for parsing the file
        final String regex = ".+?(?=\\s\\s(\\d+)$)";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(result);

        //matching
        while (matcher.find()) {
            String res;

            for (int i = 1; i <= matcher.groupCount(); i++) {
                res = "ID : " + matcher.group(i) + " TITLE: " + matcher.group(0);
                id.add(res);
            }
        }

        return id;
    }
}
