package net.alantea.horizon.testng.model;

import org.testng.Assert;

import net.alantea.horizon.message.Receive;
import net.alantea.horizon.message.Message;
import net.alantea.horizon.message.MessageSubscriber;

public 
class TheListener implements MessageSubscriber
{
   public static final String FIRSTID = "FirstSimpleTest";
   public static final String SECONDID = "SecondSimpleTest";
   public static final String SPECIFICID = "Specific";
   public static final String SPECIALID = "Special";
   public static final String ANOTHERID = "Another";
   public static final String UNKNOWNID = "Unknown";
   
   private String id = null;
   private Object content = null;
   private String name;
   private boolean specific = false;
   private boolean special = false;
   private boolean backup = false;
   
   public TheListener(String name)
   {
      this.name = name;
   }
   
   public void onMessage(Message message)
   {
      id = message.getIdentifier();
      content = message.getContent();
      specific = false;
      special  = false;
      backup  = true;
      System.out.println("I am the listener " + name + " for " + id + ", I got " + content + " !");
   }
   
   public void onSpecificMessage(Message message)
   {
      id = message.getIdentifier();
      content = message.getContent();
      specific = true;
      special  = false;
      backup  = false;
      Assert.assertEquals(id, SPECIFICID);
      System.out.println("I am the listener " + name + " for " + id + ", I specifally got " + content + " !");
   }
   
   public void onSpecificMessage(String value)
   {
      id = SPECIFICID;
      content = value;
      specific = true;
      special  = false;
      backup  = false;
      System.out.println("I am the listener " + name + " for " + id + ", I specifally got " + content + " !");
   }
   
   @Receive(message=ANOTHERID)
   public void onAnotherMessage(Message message)
   {
      id = message.getIdentifier();
      content = message.getContent();
      specific = false;
      special  = false;
      backup  = false;
      System.out.println("I am the listener " + name + " for " + id + ", I unknownly got " + content + " !");
   }
   
   @Receive(message=SPECIALID)
   public void onSpecialMessage(Message message)
   {
      id = message.getIdentifier();
      content = message.getContent();
      specific = false;
      special  = true;
      backup  = false;
      System.out.println("I am the listener " + name + " for " + id + ", I specially got " + content + " !");
   }

   public boolean isSpecific()
   {
      return specific;
   }

   public boolean isSpecial()
   {
      return special;
   }

   public boolean isBackup()
   {
      return backup;
   }

   public String getName()
   {
      return name;
   }

   public String getId()
   {
      return id;
   }

   public Object getContent()
   {
      return content;
   }

   @Override
   public String toString()
   {
      return "TheListener [id=" + id + ", content=" + 
            ((content == null) ? "null" : content.getClass().getName())
            + ", name=" + name + ", specific=" + specific
            + ", special=" + special + ", backup=" + backup + "]";
   }
   
}