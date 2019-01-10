package net.alantea.horizon.demos.example;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import net.alantea.horizon.message.Receive;
import net.alantea.horizon.message.Message;
import net.alantea.horizon.message.Messenger;

/**
 * Messenger counting demonstration. Illustrates the use of messages for controller/view interaction.
 */
public class Count2
{
   
   /** The message identifier ADD. */
   private static final String ADD = "Add to Label";
   
   /** The message identifier SUB. */
   private static final String SUB = "Sub to Label";

   /**
    * Creates the and show GUI.
    */
   private static void createAndShowGUI()
   {
      // Create and set up the window panel.
      JFrame frame = new JFrame("Swing messenger demo");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.getContentPane().setLayout(new FlowLayout());
      
      // Add a decrement button. Click will send an ADD message with -1 value.
      JButton decr = new JButton("<");
      frame.getContentPane().add(decr);
      decr.addActionListener((e) -> Messenger.sendMessage(decr, SUB , null));

      // Add a label to show value
      JLabel label = new JLabel("100");
      frame.getContentPane().add(label);
      
      // Create the listeners to wait for messages and register it.
      Messenger.register(new AddSubListener(label));

      // Add a increment button. Click will send an ADD message with 1 value.
      JButton incr = new JButton(">");
      frame.getContentPane().add(incr);
      incr.addActionListener((e) -> Messenger.sendMessage(decr, ADD , null));

      // Display the window.
      frame.pack();
      frame.setVisible(true);
      
   }

   /**
    * The main method. Standard swing code...
    *
    * @param args the arguments
    */
   public static void main(String[] args)
   {
      javax.swing.SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            createAndShowGUI();
         }
      });
   }
   
   /**
    * The listener for receiving add events.
    *
    * @see AddEvent
    */
   private static class AddSubListener
   {
      
      /** The label to modify on events. */
      private JLabel label;
      
      /**
       * Instantiates a new add listener.
       *
       * @param label the label
       */
      AddSubListener(JLabel label)
      {
         this.label = label;
      }
      
      /**
       * ADD messages management (only when an integer is given).
       *
       * @param value the integer value
       */
      @Receive(message=ADD)
      private void onAddAction(Message message)
      {
         // get current count
         int current = Integer.parseInt(label.getText());
         
         // add value to current count
         current ++;
         
         // set label text
         label.setText("" + current);
      }
      
      /**
       * SUB messages management (only when an integer is given).
       *
       * @param value the integer value
       */
      @Receive(message=SUB)
      private void onSubAction(Message message)
      {
         // get current count
         int current = Integer.parseInt(label.getText());
         
         // add value to current count
         current --;
         
         // set label text
         label.setText("" + current);
      }
   }
}
