<<<<<<< HEAD
# Horizon
##Event message driven tool for applications
The goal of Horizon is to offer tools for exchanging messages between Java objects

## Why using messages ?
Exchanging messages between objects allows the application to avoid setting connections between the corresponding elements. It loosen the links between classes, instances and packages. This is good for application design. It also simplify any further changes in design or implementation, as senders and receivers have not to know each other to exchange messages.

### Message use example
Let us take as example a GUI, with a button in a panel. When the user press the button in the panel, something must append (a data change) in the model.
In a classic design, the button must be linked with the model, knowing the data to be changed. An action listener will then  allow the user pressing the button to change the value.

Using messages, we will just define a specific message identifier, (say : "ChangeMyDataValue") and code a listener for the button to send the message when pressed. On the other end, we will code a listener in the model, that will change the value when receiving a message with the correct identifier. The button do not know the model, neither do the model know the button.

This way allows to write tests easily (by generating "fake" button messages to change the value) and also to add more than one listener (ex: refresh a value elsewhere in the GUI if the value change).

## What is a message ?
A message is an instance of the net.alantea.horizon.Message class, piece of information sent by a source.
This element must have :
- an identifier, stating the type of message that is sent
- a content, containing any type of object needed, obviously coherent with the message type

It may have :
- a context, stating the environment in which the message is to be understood
- a receiver, an object with is the target for the message
- a confidentiality (true or false)

## Who can send a message ?
Any object may send a message. The sending methods are all contained statically in the net.alantea.horizon.Messenger class. A message may be sent specifically to a receiver, or sent world-wide to all enabled receivers, subscribers and listeners.

## How to enable message reception in a class ?
Receiving messages is done through dedicated methods which follow  a semantic scheme.

First, to define a method as to be called when a message is sent, there are two ways :
- any method with name "onXXXXMessage" will be defined as receiver for messages with "XXXX" as identifier.
- any method annotated with "@Listen" will be called when the message identifiers stated in the Listen annotation are sent.

Second : the method must wait for one and only one parameter. This parameter may be of the 'Message" class or any Java object type. If the parameter is not of Message type, then the method will only be called when the message content is of the same type.

Note that there is a special method definition that will be called if there is no other method in the object to catch the message :
    void onMessage(Message message) {}
    
Third : To receive world-wide sent messages, one of the methods "Messenger.register()" must be called on the first object creation (it is not harmfull to recall it any number of time).

It is up to any receiver, listener or subscriber to deal with the message. Nothing is awaited in return.

## Want an example ?
Here is a very basic example, with a source and a target :

The code for the source is :

```java
package net.alantea.horizon.demo;

import net.alantea.horizon.message.Messenger;

public class DemoSource
{
   private String theName;
   
   public DemoSource(String name)
   {
      theName = name;
      Messenger.sendMessage(null, "IveBeenCreated", this);
   }

   public String getName()
   {
      return theName;
   }
}
```

The target will be :

```java
package net.alantea.horizon.demo;

import net.alantea.horizon.message.Listen;
import net.alantea.horizon.message.Messenger;

public class DemoTarget
{
   public DemoTarget()
   {
      Messenger.register(this);
   }
   
   @Listen(message="IveBeenCreated")
   private void onSourceCreation(DemoSource source)
   {
      System.out.println("The source " + source.getName() + " has been created.");
   }
}
```

and the whole session monitor by its main :

```java
package net.alantea.horizon.demo;

import net.alantea.horizon.message.Messenger;
import net.alantea.horizon.message.Messenger.Mode;

public class DemoMain
{
   public static void main(String[] args)
   {
      Messenger.setMode(Mode.SYNCHRONOUS);
      DemoTarget target = new DemoTarget();
      Messenger.register(target);

      DemoSource source = new DemoSource("NewBorn");
   }

}
```

Calling the main method should output :

```
The source NewBorn has been created.
```

#Using contexts
The process shown so far is such that a registered listener will catch all messages provided with the correct identifiers, wherever the message is sent in the application. There are may reasons that the messages to catch should be emitted in a special case, such as a dialog box or an tabbed pane. And any other messages, even those of known and awaited identifiers, should be ignored. In short words : sometimes we need a context for sending and receiving messages.

A context may be inserted in any message before sending. In fact, there are also contexts in messages : messages with "no context" - a null context - are in fact using a special "default context".

All methods in the Messenger class are doubled : one simplified version with no context specification, using the default context, and another with a context as first parameter.
=======
# Horizon
##Event message driven tool for applications
The goal of Horizon is to offer tools for exchanging messages between Java objects

## Why using messages ?
Exchanging messages between objects allows the application to avoid setting connections between the corresponding elements. It loosen the links between classes, instances and packages. This is good for application design. It also simplify any further changes in design or implementation, as senders and receivers have not to know each other to exchange messages.

### Message use example
Let us take as example a GUI, with a button in a panel. When the user press the button in the panel, something must append (a data change) in the model.
In a classic design, the button must be linked with the model, knowing the data to be changed. An action listener will then  allow the user pressing the button to change the value.

Using messages, we will just define a specific message identifier, (say : "ChangeMyDataValue") and code a listener for the button to send the message when pressed. On the other end, we will code a listener in the model, that will change the value when receiving a message with the correct identifier. The button do not know the model, neither do the model know the button.

This way allows to write tests easily (by generating "fake" button messages to change the value) and also to add more than one listener (ex: refresh a value elsewhere in the GUI if the value change).

## What is a message ?
A message is an instance of the net.alantea.horizon.Message class, piece of information sent by a source.
This element must have :
- an identifier, stating the type of message that is sent
- a content, containing any type of object needed, obviously coherent with the message type

It may have :
- a context, stating the environment in which the message is to be understood
- a receiver, an object with is the target for the message
- a confidentiality (true or false)

## Who can send a message ?
Any object may send a message. The sending methods are all contained statically in the net.alantea.horizon.Messenger class. A message may be sent specifically to a receiver, or sent world-wide to all enabled receivers, subscribers and listeners.

## How to enable message reception in a class ?
Receiving messages is done through dedicated methods which follow  a semantic scheme.

First, to define a method as to be called when a message is sent, there are two ways :
- any method with name "onXXXXMessage" will be defined as receiver for messages with "XXXX" as identifier.
- any method annotated with "@Listen" will be called when the message identifiers stated in the Listen annotation are sent.

Second : the method must wait for one and only one parameter. This parameter may be of the 'Message" class or any Java object type. If the parameter is not of Message type, then the method will only be called when the message content is of the same type.

Note that there is a special method definition that will be called if there is no other method in the object to catch the message :
    void onMessage(Message message) {}
    
Third : To receive world-wide sent messages, one of the methods "Messenger.register()" must be called on the first object creation (it is not harmfull to recall it any number of time).

It is up to any receiver, listener or subscriber to deal with the message. Nothing is awaited in return.

# Want an example ?
Here is a very basic example, with a source and a target :

The code for the source is :
...java
package net.alantea.horizon.demo;

import net.alantea.horizon.message.Messenger;

public class DemoSource
{
   private String theName;
   
   public DemoSource(String name)
   {
      theName = name;
      Messenger.sendMessage(null, "IveBeenCreated", this);
   }

   public String getName()
   {
      return theName;
   }
}
...

The target will be :
...java
package net.alantea.horizon.demo;

import net.alantea.horizon.message.Listen;
import net.alantea.horizon.message.Messenger;

public class DemoTarget
{
   public DemoTarget()
   {
      Messenger.register(this);
   }
   
   @Listen(message="IveBeenCreated")
   private void onSourceCreation(DemoSource source)
   {
      System.out.println("The source " + source.getName() + " has been created.");
   }
}
...

and the whole session monitor by its main :
...java
package net.alantea.horizon.demo;

import net.alantea.horizon.message.Messenger;
import net.alantea.horizon.message.Messenger.Mode;

public class DemoMain
{
   public static void main(String[] args)
   {
      Messenger.setMode(Mode.SYNCHRONOUS);
      DemoTarget target = new DemoTarget();
      Messenger.register(target);

      DemoSource source = new DemoSource("NewBorn");
   }

}
...

Calling the main method should output :
...
The source NewBorn has been created.
...
>>>>>>> stash
