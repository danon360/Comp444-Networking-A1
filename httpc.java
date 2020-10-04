
import java.io.*;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.net.URL;
import java.util.Scanner;

public class httpc {

    public static void printUsage(String errMsg) {
        if(errMsg.isEmpty()) {
            System.out.println("Usage:\n" +
                    "httpc command [arguments]\n" +
                    "The commands are:\n" +
                    "get\t executes a HTTP GET request and prints the response.\n" +
                    "post\t executes a HTTP POST request and prints the response.\n" +
                    "help\t prints this screen.\n" +
                    "\nUse \"httpc help [command]\" for more information about a command.");
        }else {
            System.err.println(errMsg);
            System.out.println("Usage:\n" +
                    "httpc command [arguments]\n" +
                    "The commands are:\n" +
                    "get\t executes a HTTP GET request and prints the response.\n" +
                    "post\t executes a HTTP POST request and prints the response.\n" +
                    "help\t prints this screen.\n" +
                    "\nUse \"httpc help [command]\" for more information about a command.");

        }
    }

    //TODO finish help
    public static void help(String comd) {
        switch(comd) {
            case "get":
                System.out.println(
                        "usage: httpc get [-v] [-h key:value] URL\n" +
                                "Get executes a HTTP GET request for a given URL.\n" +
                                "-v\t Prints the detail of the response such as protocol, status, and headers.\n" +
                                "-h key:value\t Associates headers to HTTP Request with the format 'key:value'\n"+
                                 "-o /path/filename\t specifies the path where the output should be stored\n");
                break;
            case "post":
                System.out.println("usage: httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL\n" +
                        "Post executes a HTTP POST request for a given URL with inline data or from file.\n" +
                        "-v\t Prints the detail of the response such as protocol, status,and headers.\n" +
                        "-h key:value\t Associates headers to HTTP Request with the format 'key:value'.\n" +
                        "-d string\t Associates an inline data to the body HTTP POST request.\n" +
                        "-f file\t Associates the content of a file to the body HTTP POST request.\n" +
                        "\nEither [-d] or [-f] can be used but not both.\n"+
                        "-o /path/filename\t specifies the path where the output should be stored\n");
                break;
            default:
                System.out.println("httpc is a curl-like application but supports HTTP protocol only.\n");
                printUsage("");
        }
        System.exit(0);
    }

    public static String GET(Socket s,List headers, URL url) throws IOException {
        String path = url.getPath();
        String host = url.getHost();
        String query = url.getQuery();
        String requestLine;
        String request = "";
        Boolean redirect;

        //if the url does not include path, assume '/'
        if(path.isEmpty()) path = "/";

        if(query == null || query.isEmpty()) {
            requestLine = "GET " +path+" HTTP/1.0\r\n";
        }else {
            requestLine =  "GET " +path+"?"+query+" HTTP/1.0\r\n";
        }
        // Send headers
        BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), "UTF8"));
        wr.write(requestLine);
        request+=requestLine;
        wr.write("Host: "+host+"\r\n");
        request+="Host: "+host+"\r\n";
        //wr.write("Content-Length: "+params.length()+"rn");
        //wr.write("Content-Type: application/x-www-form-urlencodedrn");
        for(int i =0;headers != null && i <= headers.size() - 1;i = i+2) {
            wr.write(headers.get(i) + ": " + headers.get(i+1) + "\r\n");
            request+=headers.get(i) + ": " + headers.get(i+1) + "\r\n";
        }
        wr.write("\r\n");
        request+="\r\n";

        if(headers != null && headers.contains("-t")) System.out.println(request);
        // Send parameters
        //wr.write(params);
        wr.flush();
        String response = read(s.getInputStream());
        wr.close();
        /*
        URL redirectString = checkRedirect(response);
        if(redirectString != null){
            response = GET(s,headers,redirectString);
        }*/
        return response;
    }

    public static String post(Socket s,List headers, URL url, String message ) {
        String path = url.getPath();
        String host = url.getHost();
        String requestLine;
        String request = "";
        String response = "";
        if(path == null || path.isEmpty()) path = "/";
        requestLine = "POST " + path + " HTTP/1.0\r\n";
        try {
            // Send headers
            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), "UTF8"));

            wr.write(requestLine);
            request+=requestLine;
            wr.write("Host: "+host+"\r\n");
            request+="Host: "+host+"\r\n";
            wr.write("Content-Length: "+message.length()+"\r\n");
            request+="Content-Length: "+message.length()+"\r\n";
            //wr.write("Content-Type: application/x-www-form-urlencodedrn");
            for(int i =0;headers != null && i <= headers.size() - 1;i = i+2) {
                wr.write(headers.get(i) + ": " + headers.get(i+1) + "\r\n");
                request+=headers.get(i) + ": " + headers.get(i+1) + "\r\n";
            }
            wr.write("\r\n");
            request+="\r\n";

            if(headers != null && headers.contains("-r")) message.replaceAll(":","=");

            // Send parameters
            wr.write(message);
            request+=message;

            if(headers != null && headers.contains("-t")) System.out.println(request);

            wr.flush();
            response = read(s.getInputStream());
            wr.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return response;

    }
    public static String read(InputStream IS) {
        // Get response
        BufferedReader rd = new BufferedReader(new InputStreamReader(IS));
        String line = "";
        Scanner in = new Scanner(IS).useDelimiter("(\\b|\\B)");
        /*
        try {
            while ((line += rd.readLine()) != null) {
                line +=" ";
            }
            rd.close();
        } catch (IOException e) {

            e.printStackTrace();
        }*/
        String temp;
        while(in.hasNext()){
             temp = in.next();
            line+=temp ;
           //googlgdg424hhgrg44fffffw1 System.out.println(temp);
        }

        return line;
    }



    public static List processHeaders(List<String> input) {
        int Findex = input.indexOf("-h");
        int Lindex = input.lastIndexOf("-h");
        List<String> headers = new ArrayList<String>();
        String temp = null;

        //ifno headers are given, return null
        if (Findex == -1) return null;

        //place the header in the list, followed by the value of that header in the next index
        for(int i =Findex; i <= Lindex ; i = i+2) {
            temp = input.get(i + 1);
            headers.add(temp.substring(0, temp.indexOf(':')));	//adding the header
            headers.add(temp.substring(temp.indexOf(':') +1));		//adding the value
        }

        return headers;
    }

    private static String extractFile(List<String> arg) {

        String msg = "";
        BufferedReader reader = null;
        int position = arg.indexOf("-f") +1;

        File f = new File(arg.get(position));
        try {
            reader = new BufferedReader(new FileReader(f));
        } catch (FileNotFoundException e) {
            System.err.println("File does not exists");
            System.exit(1);
        }

        try {
            int i;
            while((i= reader.read()) != -1) {
                msg += (char)i;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return msg;
    }


    private static String extractInline(List<String> arg) {
        int position = arg.indexOf("-d") +1;

        return arg.get(position);
    }

    private static String[] splitResponse(String in){
        String[] out = new String[2];
        int split;
        split = in.indexOf("\r\n\r\n");

        //adding the headers into the 1st index
        out[0] = in.substring(0,split);
        out[1] = in.substring(split + 4,in.length());   //+4 is to account for \r\n\rn\
        return out;
    }

    private static void printResponse(String response, boolean verbose, PrintWriter outFile,String Bmsg, String Emsg){

        if(!Bmsg.isBlank()){
            response = Bmsg + response;
        }

        if(!Emsg.isBlank()){
            response = response + Emsg;
        }

        if(verbose){
            System.out.println(response);
            if(outFile != null) {
                outFile.println(response);
                outFile.flush();
                outFile.close();
            }
        }else{
            response = splitResponse(response)[1];
            System.out.println(response);
            if(outFile != null) {
                outFile.println(response);
                outFile.flush();
                outFile.close();
            }
        }
    }
    private static URL checkRedirect(String in){
        String statusLine = in.substring(0,in.indexOf("\r\n")).trim();
        String responseCode = in.substring(statusLine.indexOf(" ") ,statusLine.indexOf(" ")+4).trim();
        String newLocation;
        URL url = null;

       //if response code is in the 300 range
        if(responseCode.charAt(0) == '3'){

            //if could not find location header, return null
            if(in.indexOf("Location:") == -1) return null;

            int index = in.indexOf("Location:") +10;
            newLocation = in.substring(index, in.indexOf("\r\n",index));    //if newLocation does not contain http (or HTTP) return null
            if(!(newLocation.contains("http:") || newLocation.contains("HTTP:"))){
                return  null;
            }else{
                //getting the url
                try {
                    url = new URL(newLocation);
                } catch (MalformedURLException e1) {
                    System.err.println("Invalid redirect URl");
                    System.exit(1);
                    //e1.printStackTrace();
                }
                return url;
            }

        }

        return null;
    }
    private static URL replaceHost(String url,int host_length, URL newHost){
        int offset = 7 + host_length;
        URL ret = null;

        String newUrl = newHost.getHost() + url.substring(offset);

        try {
           ret = new URL(newUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static void main(String[] args) {

        if(args.length < 1) {
            printUsage("no commands given");
            System.exit(1);
        }

        String command = args[0].toLowerCase();

        //if the user input "help"
        if(command.equals("help")) {
            if(args.length > 1) {
                help(args[1]);
            }else {
                help("");
            }
        }

        if(args.length < 2) {
            printUsage("not enough arguments");
            System.exit(1);
        }

        //args[1] = "http://192.168.0.2/get";

        boolean verbose = false;
        boolean inline = false;
        boolean file = false;
        URL url = null;
        String hostname = null;
        String path = null;
        List headers = null;
        PrintWriter outFile = null;

        //if the 1st command is neither of those 3, exit
        if((command.equals("get") || command.equals("post") || command.equals("help")) == false) {
            printUsage("Invalid argument!");
            System.exit(1);
        }

        //converting the args array to list for more functionality
        List<String> arguments = Arrays.asList(args);

        //checking for verbose option
        if(arguments.contains("-v")) verbose = true;

        //checking for -d flag
        if(arguments.contains("-d")) inline = true;

        //checking for -f flag
        if(arguments.contains("-f")) file = true;

        // if -o is set we open a stream to outFile
        if(arguments.contains("-o")){
            int index = arguments.indexOf("-o") +1;
            String location = arguments.get(index);
            try {
                outFile = new PrintWriter(location, "UTF-8");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                System.err.println("invalid file location");
                System.exit(1);
            }
        }

        if(file&&inline) {
            printUsage("Cannot use both -f and -d at the same time");
            System.exit(1);
        }

        String string_url = arguments.get(arguments.size() -1).trim();

        boolean redirect;
        String response = "";

        //getting the url
        try {
            url = new URL(string_url);
        } catch (MalformedURLException e1) {
            System.err.println("Invalid URl");
            System.exit(1);
            //e1.printStackTrace();
        }

        do{

            redirect = false;
            //setting the hostname
            hostname = url.getHost();

            path = url.getPath();

            //get a list of headers, if there are non null is returned
            headers = processHeaders(arguments);
            try {

                /*
                String params = URLEncoder.encode("param1", "UTF-8") + "=" + URLEncoder.encode("value1", "UTF-8");
                params += "&" + URLEncoder.encode("param2", "UTF-8") + "=" + URLEncoder.encode("value2", "UTF-8");
                */
                int port = 80;

                InetAddress IP = InetAddress.getByName(hostname);
                Socket socket = new Socket(IP, port);



                switch (command) {
                    case "get":
                        response = GET(socket, headers, url);
                        break;
                    case "post":
                        String msg = "";
                        if (file) {
                            msg = extractFile(arguments);
                        }
                        if (inline) {
                            msg = extractInline(arguments);
                        }
                        response = post(socket, headers, url, msg);
                        break;
                    case "help":
                        help("");
                    default:
                        printUsage("");
                        System.exit(1);
                }

                if (checkRedirect(response) != null) {
                    redirect = true;
                    url = checkRedirect(response);
                    //replaceHost(string_url,url.getHost().length(),checkRedirect(response));
                    printResponse(response,verbose,outFile,"","\nRedirecting to: "+url.toString()+"\n\n");

                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }while(redirect);
            //printing the result
            /*if(verbose){
                System.out.println(response);
                if(outFile != null) {
                    outFile.println(response);
                    outFile.flush();
                    outFile.close();
                }
            }else{
                response = splitResponse(response)[1];
                System.out.println(response);
                if(outFile != null) {
                    outFile.println(response);
                    outFile.flush();
                    outFile.close();
                }
            }*/
        printResponse(response,verbose,outFile,"","");



     }

}
