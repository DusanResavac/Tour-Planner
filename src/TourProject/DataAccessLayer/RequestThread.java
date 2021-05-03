package TourProject.DataAccessLayer;

import java.io.*;
import java.net.HttpURLConnection;


public class RequestThread implements Runnable {

    CallbackRequestBody c;
    HttpURLConnection con;
    String filePath = null;

    public RequestThread(CallbackRequestBody c, HttpURLConnection con, String filePath) {
        this.c = c;
        this.con = con;
        this.filePath = filePath;
    }

    @Override
    public void run() {
        String response = null;
        try {
            int status = con.getResponseCode();
            response = filePath != null ? writeFullResponse(con, filePath) : getFullResponse(con);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        con.disconnect();
        c.callback(response, filePath != null);
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
}
