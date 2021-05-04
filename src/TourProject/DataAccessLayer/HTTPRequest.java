package TourProject.DataAccessLayer;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPRequest {

    private String urlString;
    public String response;

    public HTTPRequest (String urlString) {
        this.urlString = urlString;
    }

    public void request(CallbackRequestBody callback, String filePath) {
        URL url = null;
        try {
            url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            //con.setRequestProperty("Cache-Control", "private, no-store, no-cache, must-revalidate");
            //con.setRequestProperty("Pragma", "no-cache");
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:86.0) Gecko/20100101 Firefox/88.0");
            con.setRequestMethod("GET");
            con.setConnectTimeout(30000);
            con.setReadTimeout(0);

            final String[] response = new String[]{null};

            RequestThread runnable = new RequestThread(callback, con, filePath);
            Thread t = new Thread(runnable);
            t.start();

            //System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String writeFullResponse(HttpURLConnection con, String filePath) throws IOException {
        con.connect();
        StringBuilder fullResponseBuilder = new StringBuilder();

        // read response content
        InputStream in = con.getInputStream();
        String inputLine;
        File file = new File(filePath);
        file.createNewFile();
        FileOutputStream outputStream = new FileOutputStream(filePath);

        int bytesRead = -1;
        byte[] buffer = new byte[1024];
        while ((bytesRead = in.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        outputStream.close();
        in.close();

        return filePath;
    }

    public String getFullResponse(HttpURLConnection con) throws IOException {
        con.connect();
        StringBuilder fullResponseBuilder = new StringBuilder();


        // read response content
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            fullResponseBuilder.append(inputLine).append(System.lineSeparator());
        }
        in.close();

        return fullResponseBuilder.toString();
    }

    public String getUrlString() {
        return urlString;
    }

    public void setUrlString(String urlString) {
        this.urlString = urlString;
    }
}
