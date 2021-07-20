/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.webthings.webthing.server;

import io.webthings.webthing.affordances.InteractionAffordance;
import io.webthings.webthing.common.ThingData;
import io.webthings.webthing.forms.Form;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Lorenzo
 */
public class ManagedThingsCollection {
    private static ManagedThingsCollection  __inst;
    
    public static ManagedThingsCollection getInstance() {
        if (__inst == null)
            __inst = new ManagedThingsCollection();
        
        return __inst;
    }
    private ManagedThingsCollection() {
        __registered_urls = new TreeMap<>();
        
        __actions_urls = new TreeMap<>();
        __events_urls = new TreeMap<>();
        __properties_urls = new TreeMap<>();
        __form_metadata_urls = new TreeMap<>();
        __registered_root_forms = new TreeMap<>();
    }
    private class InteractionData {
        public final InteractionAffordance  Affordance;
        public final String                 Name;
        public final ThingObject            Owner;
        public InteractionData(ThingObject to, InteractionAffordance ia, String n ) {
            Affordance = ia;
            Name = n;
            Owner = to;
        }
    }
    
    private class FormData {
        public final Form               FormData;
        public final ThingObject        Owner;
        
        public FormData(Form f, ThingObject o ) {
           FormData = f;
           Owner = o;
        }
    }
    
    private final Map<String, InteractionData>          __registered_urls ;
    private final Map<String,Class>                     __actions_urls;
    private final Map<String,Class>                     __events_urls;
    private final Map<String,Class>                     __properties_urls;
    private final Map<String,Class>                     __form_metadata_urls;
    private final Map<String,FormData>                  __registered_root_forms;
    
    public void add(ThingObject to) {
        final ThingData td = to.getData();
        
        final java.net.URI  baseURI = td.getBase();
        String sBaseURI = "";
        if (baseURI != null)
            sBaseURI = baseURI.toString();
        
        
        //load actions
        //Set<String> actionURLs =  new TreeSet<>();
        
        loadMap(new ActionHandlerGetter(), to,td.getActions(), sBaseURI, __actions_urls);
        //loadMap(to,td.getEvents(), sBaseURI,__events_urls);
        loadMap(new PropertyHandlerGetter(), to,td.getProperties(), sBaseURI,__properties_urls);
        
        loadMap(new EventHandlerGetter(), to,td.getEvents(), sBaseURI, __events_urls);
        

        //load root level forms
        if (td.getForm() != null) {
            for(final Form f : td.getForm()) {
                final String path = addForm(f, FormMetadataHandler.class, __form_metadata_urls);
                __registered_root_forms.put(path,new FormData(f, to));
            }
        }
    }
    
    public Form getRootForm(String url) {
        Form ret = null;
        final FormData id = __registered_root_forms.get(url);
        if (id != null)
            ret = id.FormData;
        return ret;
            
    }
    
    public ThingObject getRootFormOwner(String url) {
        ThingObject ret = null;
        final FormData id = __registered_root_forms.get(url);
        if (id != null)
            ret = id.Owner;
        return ret;
            
    }
    
    public InteractionAffordance getInteraction(String url) {
        InteractionAffordance ret = null;
        final InteractionData id = __registered_urls.get(url);
        if (id != null)
            ret = id.Affordance;
        
        return ret;
            
    }
    public String  getInteractionName(String url) {
        String ret = null;
        final InteractionData id = __registered_urls.get(url);
        if (id != null)
            ret = id.Name;
        
        return ret;

    }
    public ThingObject  getInteractionOwner(String url) {
        ThingObject ret = null;
        final InteractionData id = __registered_urls.get(url);
        if (id != null)
            ret = id.Owner;
        
        return ret;

    }
    private interface HandlerGetter {
        public Class   getHandler(ThingObject to, String name);
    }
    
    private class PropertyHandlerGetter implements HandlerGetter {

        @Override
        public Class   getHandler(ThingObject to, String name) {
            final Property p = to.getProperty(name);
            return p.getHandler();
        }
        
    }
    
    private class ActionHandlerGetter implements HandlerGetter {
        @Override
        public Class   getHandler(ThingObject to, String name) {
            final Action  a = to.getAction(name);
            return a.getHandler();
        }
        
    }
    
    private class EventHandlerGetter implements HandlerGetter {
        @Override
        public Class   getHandler(ThingObject to, String name) {
            final Event  a = to.getEvent(name);
            return a.getHandler();
        }
        
    }
   
    
    private String addForm(Form f,Class handler,Map<String,Class> storedURLs) {
        final java.net.URI u = f.getHref();
        String path = "";
        if (u.isAbsolute()) {
            path = u.getPath();
        } else {
            path = u.toString();
        }
        storedURLs.put(path,handler);
        
        return path;
    }
    private <__T extends InteractionAffordance,__GETTER extends HandlerGetter> void   loadMap(__GETTER g,ThingObject to, Map<String,__T> m, String base,Map<String,Class> storedURLs) {
        if (m == null)
            return;
        for(final Map.Entry<String, __T> e : m.entrySet() ) {
            final String key = e.getKey();
            final __T   data = e.getValue();
            for(final Form f : data.getForms()) {
                final String path = addForm(f, g.getHandler(to, key), storedURLs);
                __registered_urls.put(path, new InteractionData(to,data,key));
                
                
                
            }
        }
    }
    
    public Map<String,Class> getActionsURL() {
        return __actions_urls;
    }
    public Map<String,Class> getEventsURL() {
        return __events_urls;
    }
    public Map<String,Class>getPropertiesURL() {
        return __properties_urls;
    }
    public Map<String,Class> getRootFormsURL() {
        return __form_metadata_urls;
    }
}