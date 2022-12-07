package rocks.zipcode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

public class SimpleShell {

    public static <T> void prettyPrint(String output, Class<T> data) throws IOException {
        //System.out.println("?? " + output);
        ObjectMapper mapper = new ObjectMapper();
        if (data.getSimpleName().equals("User")) {
            List<User> myObjects = mapper.readValue(output, new TypeReference<List<User>>() {
            });
            for (User e : myObjects) {
                e.print();
            }
        }
        if (data.getSimpleName().equals("Message")) {
            if (output.startsWith("[{")) {
                List<Message> myObjects = mapper.readValue(output, new TypeReference<List<Message>>() {
                });
                for (Message e : myObjects) {
                    e.print();
                }
            }
            if (output.startsWith("{")) {
                Message m = mapper.readValue(output, new TypeReference<Message>() {
                });
                m.print();
            }
        }
    }

    public static void main(String[] args) throws java.io.IOException {

        YouAreEll yae = new YouAreEll();
        String commandLine;
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

        ProcessBuilder pb = new ProcessBuilder();
        List<String> history = new ArrayList<String>();
        int index = 0;
        while (true) {
            System.out.println("cmd? ");

            commandLine = console.readLine();
            List<String> list = new ArrayList<String>();
            Pattern regex = Pattern.compile("[^\\s\"']+|\"[^\"]*\"|'[^']*'");
            // regex pattern breaks input into things sep by spaces and quotes
            Matcher regexMatcher = regex.matcher(commandLine);
            while (regexMatcher.find()) {
                list.add(regexMatcher.group());
            }

            history.addAll(list);

            if (commandLine.equals(""))
                continue;
            if (commandLine.equals("exit")) {
                System.out.println("bye!");
                break;
            }

            try {
                if (list.get(list.size() - 1).equals("history")) {
                    for (String s : history)
                        System.out.println((index++) + " " + s);
                    continue;
                }

                if (list.get(0).equals("ids")) {
                    if (list.size() > 1) {
                        User u = new User(list.get(1), list.get(2));
                        prettyPrint(yae.idController().postOrPutIds(u), User.class);
                        continue;
                    }
                    prettyPrint(yae.idController().getIds(), User.class);
                    continue;
                }

                if (list.get(0).equals("messages")) {
                    if (list.size() == 2) {
                        prettyPrint(yae.mesgController().getMessagesMine(list.get(1)), Message.class);
                        continue;
                    }
                    prettyPrint(yae.mesgController().getMessages(), Message.class);
                    continue;
                }

                if (list.get(0).equals("send")) {
                    if (list.size() == 3) {
                        Message m = new Message(list.get(1), list.get(2));
                        prettyPrint(yae.mesgController().postMessages(m), Message.class);
                        continue;
                    } else {
                        Message m = new Message(list.get(1), list.get(4), list.get(2));
                        prettyPrint(yae.mesgController().postMessages(m), Message.class);
                        continue;
                    }
                }

                // !! command returns the last command in history
                if (list.get(list.size() - 1).equals("!!")) {
                    pb.command(history.get(history.size() - 2));

                } // !<integer value i> command
                else if (list.get(list.size() - 1).charAt(0) == '!') {
                    int b = Character.getNumericValue(list.get(list.size() - 1).charAt(1));
                    if (b <= history.size())// check if integer entered isn't bigger than history size
                        pb.command(history.get(b));
                } else {
                    pb.command(list);
                }

                // wait, wait, what curiousness is this?
                Process process = pb.start();

                // obtain the input stream
                InputStream is = process.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);

                // read output of the process
                String line;
                while ((line = br.readLine()) != null)
                    System.out.println(line);
                br.close();

            }

            // catch ioexception, output appropriate message, resume waiting for input
            catch (IOException e) {
                System.out.println("Input Error, Please try again!");
                System.out.println(e);
                e.printStackTrace();
            }
            // So what, do you suppose, is the meaning of this comment?
            /**
             * The steps are:
             * 1. parse the input to obtain the command and any parameters
             * 2. create a ProcessBuilder object
             * 3. start the process
             * 4. obtain the output stream
             * 5. output the contents returned by the command
             */

        }

    }

}