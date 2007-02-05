package org.sakaiproject.metaobj.registry;

import org.sakaiproject.content.api.ResourceType;
import org.sakaiproject.content.api.ResourceToolAction;
import org.sakaiproject.content.api.ContentEntity;
import org.sakaiproject.content.api.ResourceTypeRegistry;
import org.sakaiproject.content.cover.ContentHostingService;
import org.sakaiproject.content.util.BaseInteractionAction;
import org.sakaiproject.content.util.BaseServiceLevelAction;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.user.api.User;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: johnellis
 * Date: Jan 26, 2007
 * Time: 10:02:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class FormResourceType implements ResourceType {

   private EnumMap<ResourceToolAction.ActionType, List<ResourceToolAction>> actionMap =
      new EnumMap<ResourceToolAction.ActionType, List<ResourceToolAction>>(ResourceToolAction.ActionType.class);
   private Map<String, ResourceToolAction> actions = new Hashtable<String, ResourceToolAction>();
   private ResourceTypeRegistry resourceTypeRegistry;

   public static final String FORM_TYPE_ID = ResourceType.TYPE_METAOBJ;

   private static final String CREATE_HELPER = "sakai.metaobj.form.resourceCreateHelper";
   private static final String REVISE_HELPER = "sakai.metaobj.form.resourceEditHelper";

   public void init() {
      List requiredKeys = new ArrayList();
      requiredKeys.add(ResourceProperties.PROP_STRUCTOBJ_TYPE);
      requiredKeys.add(ContentHostingService.PROP_ALTERNATE_REFERENCE);
      ResourceToolAction create = new BaseInteractionAction(ResourceToolAction.CREATE,
         ResourceToolAction.ActionType.CREATE, FORM_TYPE_ID, CREATE_HELPER, requiredKeys);
      ResourceToolAction revise = new BaseInteractionAction(ResourceToolAction.REVISE_CONTENT,
         ResourceToolAction.ActionType.REVISE_CONTENT, FORM_TYPE_ID, REVISE_HELPER, null);
      ResourceToolAction copy = new BaseServiceLevelAction(ResourceToolAction.COPY,
         ResourceToolAction.ActionType.COPY, FORM_TYPE_ID, true);
      ResourceToolAction delete = new BaseServiceLevelAction(ResourceToolAction.DELETE,
         ResourceToolAction.ActionType.DELETE, FORM_TYPE_ID, true);
      ResourceToolAction reviseMetadata = new BaseServiceLevelAction(ResourceToolAction.REVISE_METADATA,
         ResourceToolAction.ActionType.REVISE_METADATA, FORM_TYPE_ID, false);
      ResourceToolAction move = new BaseServiceLevelAction(ResourceToolAction.MOVE,
         ResourceToolAction.ActionType.MOVE, FORM_TYPE_ID, true);

      actionMap.put(create.getActionType(), makeList(create));
      actionMap.put(revise.getActionType(), makeList(revise));
      actionMap.put(copy.getActionType(), makeList(copy));
      actionMap.put(delete.getActionType(), makeList(delete));
      actionMap.put(reviseMetadata.getActionType(), makeList(reviseMetadata));
      actionMap.put(move.getActionType(), makeList(move));

      actions.put(create.getId(), create);
      actions.put(revise.getId(), revise);
      actions.put(copy.getId(), copy);
      actions.put(delete.getId(), delete);
      actions.put(reviseMetadata.getId(), reviseMetadata);
      actions.put(move.getId(), move);

      getResourceTypeRegistry().register(this);
   }

   protected List<ResourceToolAction> makeList(ResourceToolAction create) {
      List returned = new ArrayList<ResourceToolAction>();
      returned.add(create);
      return returned;
   }


   /* (non-Javadoc)
    * @see org.sakaiproject.content.api.ResourceType#getActions(org.sakaiproject.content.api.ResourceType.ActionType)
    */
   public List<ResourceToolAction> getActions(ResourceToolAction.ActionType type)
   {
      List<ResourceToolAction> list = actionMap.get(type);
      if(list == null)
      {
         list = new Vector<ResourceToolAction>();
         actionMap.put(type, list);
      }
      return new Vector<ResourceToolAction>(list);
   }

   /* (non-Javadoc)
    * @see org.sakaiproject.content.api.ResourceType#getActions(java.util.List)
    */
   public List<ResourceToolAction> getActions(List<ResourceToolAction.ActionType> types)
   {
      List<ResourceToolAction> list = new Vector<ResourceToolAction>();
      if(types != null)
      {
         Iterator<ResourceToolAction.ActionType> it = types.iterator();
         while(it.hasNext())
         {
            ResourceToolAction.ActionType type = it.next();
            List<ResourceToolAction> sublist = actionMap.get(type);
            if(sublist == null)
            {
               sublist = new Vector<ResourceToolAction>();
               actionMap.put(type, sublist);
            }
            list.addAll(sublist);
         }
      }
      return list;
   }

   /**
    * @param actionId
    * @return
    */
   public ResourceToolAction getAction(String actionId) {
      return actions.get(actionId);
   }

   /**
    * Retrieve a reference for the location of the icon for this type.
    * If null, the mimetype of the resource will be used to find an icon.
    * The reference should refer to an icon in the l
    *
    * @return
    */
   public String getIconLocation() {
      // todo icon
      return null;
   }

   /**
    * Access the identifier for this type (which must be unique within the registry and must be limited to alphnumeric characters).
    *
    * @return
    */
   public String getId() {
      return FORM_TYPE_ID;
   }

   /**
    * @return
    */
   public String getLabel() {
      // todo i18n
      return "Form Item";
   }

   /**
    * Access a text string suitable for use as a very brief description of a particular resource.
    * If the string is more than about 40 or 50 characters, it may be truncated at an arbitrary
    * length.  The string may identify the type of this resource or more specific information.
    * The string should be localized.  If no value is supplied, a default hover-string will be
    * used.
    *
    * @param member The resource that's being displayed
    * @return
    */
   public String getLocalizedHoverText(ContentEntity member) {
      // todo implement
      return "Form Item";
   }

   /**
    * Should the Resources tool support hiding and scheduled release and/or retraction for items of this type?
    *
    * @return true if availability is included among the resource properties in the UI, false otherwise.
    */
   public boolean hasAvailabilityDialog() {
      return false;
   }

   /**
    * Should the Resources tool elicit a description for items of this type?
    *
    * @return true if a description is included among the resource properties in the UI, false otherwise.
    */
   public boolean hasDescription() {
      return true;
   }

   /**
    * Should the Resources tool support access by groups for items of this type?
    *
    * @return true if access by groups is included among the resource properties in the UI, false otherwise.
    */
   public boolean hasGroupsDialog() {
      return false;
   }

   /**
    * Should the Resources tool support optional email notification for items of this type?
    *
    * @return true if email-notification is included among the resource properties in the UI, false otherwise.
    */
   public boolean hasNotificationDialog() {
      return false;
   }

   /**
    * Should the Resources tool allow specification of "optional properties" (usually Dublin Core tags) for items of this type?
    *
    * @return true if optional properties form is included among the resource properties in the UI, false otherwise.
    */
   public boolean hasOptionalPropertiesDialog() {
      return false;
   }

   /**
    * Should the Resources tool support making items of this type public?
    *
    * @return true if public access is included among the resource properties in the UI, false otherwise.
    */
   public boolean hasPublicDialog() {
      return false;
   }

   /**
    * Should the Resources tool elicit copyright/licensing for items of this type?
    *
    * @return true if the copyright/licensing dialog is included among the resource properties in the UI, false otherwise.
    */
   public boolean hasRightsDialog() {
      return false;
   }

   public ResourceTypeRegistry getResourceTypeRegistry() {
      return resourceTypeRegistry;
   }

   public void setResourceTypeRegistry(ResourceTypeRegistry resourceTypeRegistry) {
      this.resourceTypeRegistry = resourceTypeRegistry;
   }

}