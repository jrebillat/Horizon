package net.alantea.horizon.event;


/**
 * The Class HorizonEvent.
 */
public class HorizonEvent
{
   
   /** The Constant serialVersionUID. */
   public static final long serialVersionUID = 1L;
   
   /** The source. */
   private HorizonSource source;
   
   /** The id. */
   private String id;
   
   /** The parameters. */
   private Object parameters;

   /**
    * Instantiates a new horizon event without parameters.
    *
    * @param source the source
    * @param id the id
    */
   public HorizonEvent(HorizonSource source, String id)
   {
      this(source, id, null);
   }
   
   /**
    * Instantiates a new horizon event.
    *
    * @param source the source
    * @param id the id
    * @param parameters the parameters
    */
   public HorizonEvent(HorizonSource source, String id, Object parameters)
   {
      super();
      this.source = source;
      this.id = id;
      this.parameters = parameters;
   }

   /**
    * Gets the source.
    *
    * @return the source
    */
   public HorizonSource getSource()
   {
      return source;
   }

   /**
    * Gets the id.
    *
    * @return the id
    */
   public final String getId()
   {
      return id;
   }

   /**
    * Gets the parameters.
    *
    * @return the parameters
    */
   public final Object getParameters()
   {
      return parameters;
   }
}
