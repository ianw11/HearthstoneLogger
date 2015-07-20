import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;
import java.io.*;
import java.util.*;

public class Logger {


   public static void main(String[] args) {

      new Logger().run();

   }

   public void run() {

      System.out.println("Hello, world!");

      final long DELAY = 0;
      final String FILENAME = "/Users/Ian/Library/Logs/Unity/Player.log";

      File file = new File(FILENAME);

      TailerListenerAdapter listener = new HSTailerListenerAdapter();
      Tailer tailer = Tailer.create(file, listener);

      Scanner scanner = new Scanner(System.in);
      
      scanner.next();

      scanner.close();

      tailer.stop();

   }

}
