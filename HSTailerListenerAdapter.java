import org.apache.commons.io.input.TailerListenerAdapter;
import org.apache.commons.io.input.Tailer;

import java.util.regex.*;

public class HSTailerListenerAdapter extends TailerListenerAdapter {

   private Tailer mTailer;

   private final Pattern PLAY_pattern = Pattern.compile("^\\[Zone.*\\[name=.*zone=(PLAY|HAND).* HAND .*");
   private final Pattern START_pattern = Pattern.compile("^\\[Z.*\\[name=.*zone=PLAY.*Hero.*");
//   private final Pattern END_pattern = Pattern.compile("^\\[Zone.*\\[name=.*zone=GRAVEYARD.*cardId=HERO.*");
   private final Pattern END_pattern = Pattern.compile("^\\[Power.*TAG_CHANGE.*tag=PLAYSTATE.*value=LOST.*");
   private final Pattern DRAW_pattern = Pattern.compile("^\\[Zone.*\\[name=.*zone=HAND.*FRIENDLY DECK.*FRIENDLY HAND");
   private final Pattern INITIAL_DRAW_pattern = Pattern.compile("^\\[Zone.*\\[name=.*zone=HAND.*from  -.*");
   private final Pattern MULLIGAN_pattern = Pattern.compile("^\\[Zone.*\\[name=.*zone=DECK.*HAND -.*");
   private final Pattern TAG_pattern = Pattern.compile("^\\[Zone.*type=TAG_CHANGE.*tag=.*value=.*");

   private final Pattern HERO_POWER_pattern = Pattern.compile("^\\[Power.*ACTION_START.*zone=PLAY.*SubType=PLAY.*Index=0.*");
   private final Pattern TURN_NUM_pattern = Pattern.compile("^\\[Power.*TAG_CHANGE.*Entity=\\[.*cardId=HERO.*tag=NUM_TURNS_IN_PLAY.*");
   
   private final Pattern ATTACKER_pattern = Pattern.compile("^\\[Power.*TAG_CHANGE.*name=.*tag=ATTACKING.*value=0.*");
   private final Pattern DEFENDER_pattern = Pattern.compile("^\\[Power.*TAG_CHANGE.*name=.*tag=DEFENDING.*value=0.*");
   
   private final Pattern ENTERED_pattern = Pattern.compile("^\\[Zone.*\\[name=.*zone from  -> (FRIENDLY|OPPOSING) PLAY.*");
   
   private final Pattern SECRET_TRIGGER_pattern = Pattern.compile("^\\[Power.*ACTION_START.*zone=SECRET.*SubType=TRIGGER.*");
   

   private boolean IS_ME = true;


   private String p1 = null;
   private String p2 = null;


   public HSTailerListenerAdapter() {
   }


   public void init(Tailer tailer) {
      mTailer = tailer;
   }

   public void fileNotFound() {
      System.out.println("File not found!!");
   }

   public void fileRotated() {
      System.err.println("Rotated");
   }


   public void handle(String line) {
      if (line.contains("TRANSITION"))
         return;

      if (PLAY_pattern.matcher(line).matches()) {
         playCard(line);
      } else if (START_pattern.matcher(line).matches()) {
         startGame(line);
      } else if (END_pattern.matcher(line).matches()) {
         endGame(line);
      } else if (DRAW_pattern.matcher(line).matches()) {
         drawCard(line);
      } else if (INITIAL_DRAW_pattern.matcher(line).matches()) {
         drawCard(line);
      } else if (MULLIGAN_pattern.matcher(line).matches()) {
         mulliganCard(line);
     /* 
      } else if (TAG_pattern.matcher(line).matches()) {
         tagUpdate(line);
      */
      } else if (HERO_POWER_pattern.matcher(line).matches()) {
         heroPower(line);
      } else if (TURN_NUM_pattern.matcher(line).matches()) {
         turnNumber(line);
      } else if (ATTACKER_pattern.matcher(line).matches()) {
         attacker(line);
      } else if (DEFENDER_pattern.matcher(line).matches()) {
         defender(line);
      } else if (ENTERED_pattern.matcher(line).matches()) {
         enteredPlay(line);
      } else if (SECRET_TRIGGER_pattern.matcher(line).matches()) {
         secretTriggered(line);
      }

   }




   public void secretTriggered(String line) {
      //[Power] GameState.DebugPrintPower() -     ACTION_START Entity=[name=Mirror Entity id=6 zone=SECRET zonePos=0 cardId=EX1_294 player=1] SubType=TRIGGER Index=0 Target=0
      
      String[] split = line.split(" ");
      
      int i = 2;
      while (i < split.length && !split[i].contains("name=")) {
         ++i;
      }
      String[] cardNameArr = split[i].split("=");
      String cardName = cardNameArr[cardNameArr.length - 1];
      ++i;
      while (!split[i].startsWith("id")) {
         cardName += " " + split[i++];
      }
      
      
      while (!split[i].startsWith("player"))
         ++i;
         
      String playerName = "";
      
      String pNum = split[i].split("=")[1].split("\\]")[0];
      if (pNum.equals("1")) {
         playerName = p1;
      } else {
         playerName = p2;
      }
      
      
      
      System.out.println("SECRET: " + playerName + "'s secret (" + cardName + ") has triggered");
      
      
   }

   public void enteredPlay(String line) {
      //[Zone] ZoneChangeList.ProcessChanges() - id=40 local=False [name=One-eyed Cheat id=75 zone=PLAY zonePos=1 cardId=GVG_025 player=1] zone from  -> FRIENDLY PLAY
      String[] split = line.split(" ");
      
      int i = 2;
      while (i < split.length && !split[i].contains("name=")) {
         ++i;
      }
      String cardName = split[i].split("=")[1];
      ++i;
      while (!split[i].startsWith("id")) {
         cardName += " " + split[i++];
      }
      
      
      while (!split[i].startsWith("player"))
         ++i;
         
      String playerName = "";
      
      String pNum = split[i].split("=")[1].split("\\]")[0];
      if (pNum.equals("1")) {
         playerName = p1;
      } else {
         playerName = p2;
      }
      
      
      System.out.println(cardName + " has entered " + playerName + "'s field");
      
   }
   
   
   public void defender(String line) {
      String[] split = line.split(" ");
      
      int i = 2;
      while(i < split.length && !split[i].contains("name=")) {
         ++i;
      }
      if (i == split.length)
         return;
      
      String[] cardNameArr = split[i].split("=");
      String cardName = cardNameArr[cardNameArr.length - 1];
      ++i;
      while (!split[i].startsWith("id")) {
         cardName += " " + split[i++];
      }
      
      System.out.println(cardName);
   }

   public void attacker(String line) {
      String[] split = line.split(" ");
      
      int i = 2;
      while(i < split.length && !split[i].contains("name=")) {
         ++i;
      }
      if (i == split.length)
         return;
      
      String[] cardNameArr = split[i].split("=");
      String cardName = cardNameArr[cardNameArr.length - 1];
      ++i;
      while (!split[i].startsWith("id")) {
         cardName += " " + split[i++];
      }
      
      System.out.print(cardName + " is attacking ");
      
      
   }


   public void turnNumber(String line) {
      String[] split = line.split(" ");

      int i = 0;
      while(i < split.length && !split[i].contains("name="))
         ++i;
      if (i == split.length)
         return;

      String[] pNameArr = split[i].split("=");
      String pName = pNameArr[pNameArr.length - 1];
      ++i;
      while (!split[i].startsWith("id")) {
         pName += " " + split[i++];
      }
      
      while(!split[i].startsWith("player"))
         ++i;

      if (p1 == null || p2 == null) {

         String pNum = split[i].split("=")[1].split("\\]")[0];
         if (pNum.equals("1")) {
            p1 = pName;
         } else {
            p2 = pName;
         }

      }

      while (!split[i].startsWith("value"))
         ++i;

      String turnNum = split[i].split("=")[1];

      System.out.println();
      System.out.println("**Start of Turn #" + turnNum  +" for " + pName + "**");


   }

   public void heroPower(String line) {
      String[] split = line.split(" ");

      int i = 0;
      while (i < split.length && !split[i].startsWith("player")) {
         ++i;
      }

      if (i == split.length)
         return;

      if(split[i].split("=")[1].charAt(0) == '1') {
         System.out.print(p1 + " used Hero Power");
      } else {
         System.out.print(p2 + " used Hero Power");
      }
      
      while (i < split.length && !split[i].startsWith("Target"))
         ++i;
      
      String[] targetNameArr = split[i].split("=");
      String targetName = targetNameArr[targetNameArr.length - 1];
      ++i;
      if (!targetName.equals("0")) {
         while (!split[i].startsWith("id")) {
            targetName += " " + split[i++];
         }
         
         System.out.print(" on " + targetName);
      }
      
      
      System.out.println();
   }


   public void tagUpdate(String line) {
      String[] split = line.split(" ");

      //System.out.println(line);

      int i = 0;
      while (i < split.length && !split[i].startsWith("tag=")) {
         ++i;
      }
      if (i == split.length)
         return;

      String tag = split[i].split("=")[1];

      if (tag.startsWith("EXHAUSTED") ||
          //tag.startsWith("ZONE") ||
          tag.startsWith("MULLIGAN") ||
          tag.startsWith("ATTACHED") )
         return;

      System.out.print(tag + ": ");
      ++i;
      System.out.print(split[i].split("=")[1]);

      System.out.println();
   }

   public void mulliganCard(String line) {
      String[] split = line.split(" ");

      System.out.print("MULLIGANED BACK: ");

      System.out.print(split[5].split("=")[1] + " ");
      int i = 6;
      while (!split[i].startsWith("id")) {
         System.out.print(split[i++] + " ");
      }

      System.out.println();
   }

   public void drawCard(String line) {
      String[] split = line.split(" ");

      System.out.print("DREW: ");

      System.out.print(split[5].split("=")[1] + " ");
      int i = 6;
      while (!split[i].startsWith("id")) {
         System.out.print(split[i++] + " ");
      }

      System.out.println();
   }

   public void endGame(String line) {
      String[] split = line.split(" ");

      int i = 2;
      while (i < split.length && !split[i].startsWith("Entity"))
         ++i;
      
      String playerName = split[i].split("=")[1];

      System.out.println("\n" + playerName + " LOST\n");

/*
      if (split[split.length - 2].equals("FRIENDLY"))
         System.out.println("YOU LOSE!");
      else
         System.out.println("YOU WIN!");
*/

      System.out.println("Reminder: ~/Library/Logs/Unity has the console file and ~/Library/Preferences/Blizzard/Hearthstone has the log.config file");
      System.out.println("\n-----------------------------\n\n");
   }

   public void startGame(String line) {

      String[] split = line.split(" ");

      if (!(split[9].split("=")[1].startsWith("HERO") || split[10].split("=")[1].startsWith("HERO"))) {
         return;
      }

      p1 = null;
      p2 = null;

      /*
      if (IS_ME)
         System.out.print("\nYou: ");
      else
         System.out.print("Them: ");

      IS_ME = !IS_ME;
      */

      /*
      System.out.print("PLAYER: " );

      String firstName = split[5].split("=")[1];
      System.out.print(firstName + " ");
      int i = 6;
      while (!split[i].startsWith("id")) {
         System.out.print(split[i++] + " ");
      }
      System.out.println();
      */
   }

   public void playCard(String line) {

      String[] split = line.split(" ");


      /*
      String[] local = split[4].split("=");
      if (local[1].equals("True"))
         System.out.print("ME: ");
      else
         System.out.print("THEM: ");
      */
      System.out.print("PLAYED: ");


      System.out.print(split[5].split("=")[1] + " ");
      int i = 6;
      while (!split[i].startsWith("id")) {
         System.out.print(split[i++] + " ");
      }
      System.out.println();


   }


}
