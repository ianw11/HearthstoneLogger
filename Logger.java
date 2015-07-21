import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;
import java.io.*;
import java.util.*;

public class Logger {

   private enum OS {
      WIN, MAC
   }


   public static void main(String[] args) {
      
      File file = null;
      
      OS os;
      
      String homeDir = System.getProperty("user.home");
      
      // Verify OS will work with code
      if (System.getProperty("os.name").equals("Mac OS X")) {
         file = new File(homeDir + "/Library/Logs/Unity/Player.log");
         os = OS.MAC;
      } else if (System.getProperty("os.name").toLowerCase().contains("win")) {
         file = new File("C:\\Program Files (x86)\\Hearthstone\\Hearthstone_Data\\output_log.txt");
         os = OS.WIN;
      } else {
         System.out.println("This system is not yet supported");
         return;
      }
      
      try {
      
         verifyLogFileExists(homeDir, os);
         
      } catch (IOException e) {
         System.out.println("Unexpected error encountered creating log.config.  Exiting...");
         return;
      }
      
      new Logger().run(file);

   }
   
   private static void verifyLogFileExists(String homeDir, OS os) throws IOException {
      String current = System.getProperty("user.dir");
      
      File original = null;
      File file = null;
      
      switch (os) {
      case WIN:
         
         file = new File("\\AppData\\Local\\Blizzard\\Hearthstone\\log.config");
         original = new File(current + "\\log.config");
         break;
         
      case MAC:
         
         file = new File(homeDir + "/Library/Preferences/Blizzard/Hearthstone/log.config");
         original = new File(current + "/log.config");
         break;
         
      default:
         break;
      }
      
      
      // If the file in the Hearthstone directory is newer (up to date), do nothing
      if (file.exists() && FileUtils.isFileNewer(file, original))
         return;
      
      
      System.out.println("Creating/updating log.config file");
      
      
      FileUtils.copyFile(original, file, false);
   }

   public void run(File file) {

      System.out.println("Ready to begin!");

      final long DELAY = 0;
      

      TailerListenerAdapter listener = new HSTailerListenerAdapter();
      Tailer tailer = Tailer.create(file, listener);

      Scanner scanner = new Scanner(System.in);
      scanner.next();
      scanner.close();

      tailer.stop();
      
   }

}
