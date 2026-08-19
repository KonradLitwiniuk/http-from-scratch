import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HttpServer {
    private final LinkService linkService;
    private final int port;
    public HttpServer(LinkService linkService, int port){
        this.linkService = linkService;
        this.port = port;
    }
    public void startServer() throws IOException{
        System.out.println("Starting server");
        ServerSocket ss = new ServerSocket(port);
        System.out.println("Server started. Listening on port " + port);
        while (true) {
            try(Socket socket = ss.accept(); BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream())); PrintWriter pw = new PrintWriter(socket.getOutputStream());) {
                String line;
                int cntLength = 0;
                String[] lengthSplited;
                String firstLine = reader.readLine();
                System.out.println(firstLine);
                while ((line = reader.readLine()) != null && !line.isEmpty()) {
                    System.out.println(line);
                    if(line.toLowerCase().contains("content-length")){
                        lengthSplited = line.trim().split(":");
                        cntLength = Integer.parseInt(lengthSplited[1].trim());
                        System.out.println(cntLength);
                    }
                }
                char[] buffer = new char[cntLength];
                reader.read(buffer, 0, cntLength);
                String body = new String(buffer);
                System.out.println("BODY: " + body);
                String[] splittedFirstLine = firstLine.split("[\\s]+");

                pw.print(getResponse(splittedFirstLine[0], splittedFirstLine[1], body));
                pw.flush();
            }
        }
    }
    private String getResponse(String method, String path, String body){
        if (path.equals("/links") && method.equals("POST")) {
            String code = linkService.shortenLink(body);
            return buildResponse("201 Created", "Short code: " + code);
        }
        if(path.equals("/"))
            return buildResponse("200 OK", "<h1>Hello from Java</h1>");
        else if(path.equals("/about"))
            return buildResponse("200 OK", "<h1>About</h1>");
        else if(path.startsWith("/links/"))
        {
            String code = path.substring("/links/". length());

            try
            {
                String originalUrl =  linkService.findOriginalUrl(code);
                return buildRedirectResponse("302 Found", originalUrl);
            }
            catch(LinkNotFoundException e)
            {
                return buildResponse("404 Not Found", "<h1>Link not found</h1>");
            }
            catch(LinkStorageException e) {
                return buildResponse("500 Internal Server Error", "<h1>Connection failed</h1>");
            }
        }
        else
            return buildResponse("404 Not Found", "<h1>404 Not Found</h1>");
    }
    private static String buildResponse(String status, String body){
        return "HTTP/1.1 "+ status +"\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: "+ body.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                "\r\n" + body;
    }
    private static String buildRedirectResponse(String status, String Location)
    {
        return "HTTP/1.1 " + status + "\r\n" +
                "Location: " + Location + "\r\n" + "\r\n";
    }
}
