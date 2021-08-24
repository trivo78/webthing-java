package io.webthings.webthing.example;


import io.webthings.webthing.affordances.ActionAffordance;
import io.webthings.webthing.affordances.EventAffordance;
import io.webthings.webthing.affordances.PropertyAffordance;
import io.webthings.webthing.common.SecurityScheme;
import io.webthings.webthing.common.ExposeThingInit;
import io.webthings.webthing.exceptions.WoTException;
import io.webthings.webthing.forms.Form;
import io.webthings.webthing.forms.Operation;
import io.webthings.webthing.server.Action;
import io.webthings.webthing.server.Event;
import io.webthings.webthing.server.SyncActionHandler;
import io.webthings.webthing.server.ExposedWebThing;
import io.webthings.webthing.server.ThingServer;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class SingleThing {
    public static class ToggleEvent extends Event {
        public ToggleEvent(String name, EventAffordance p) {
            super(name, p);
        }
        public ToggleEvent(String name, EventAffordance p,Class h) {
            super(name, p, h);

        }

        @Override
        protected JSONObject makeEventData() {
            final JSONObject ret = new JSONObject();
            ret.put("toggled", "Changed state !!");
            
            return ret;
        }
        
    }
    public static class ToggleAction extends Action {
        private boolean  __state = false;
        public ToggleAction(String name, ActionAffordance data,Class h) {
            super(name, data, h);
        }

        public void run() {
            System.out.println("Current state : " + __state);
            __state = !__state;
            System.out.println("New  state : " + __state);
            
            //fire toggled event
            final Event e = getOwner().getEvent("toggled");
            if (e != null)
                e.notifyEvent();
            
        }

    }
    
    private static void addProperty(String title, String desc, String href, Operation.id op,
                                    ExposeThingInit tgt)throws URISyntaxException,WoTException {
        final PropertyAffordance pa  = new PropertyAffordance();
        pa.setDefaultTitle(title);
        pa.setDefaultDescription(desc);
        final Form f = new Form(href);
        
       
        if (op != null)
            f.setOperation(op);
        
        pa.addForm(f);
        tgt.addProperty("name", pa);
        
    }
    private static void addMetadataForm(String href, Operation.id op,
                                        ExposeThingInit tgt)throws URISyntaxException,WoTException {
        final Form f = new Form(href);
       
        if (op != null)
            f.setOperation(op);
        
        tgt.addForm(f);
        
        
    }
    
    public static List<ExposedWebThing> makeThing() throws URISyntaxException,WoTException{
        final List<ExposedWebThing> ret = new ArrayList<>();
        final ExposeThingInit td = new ExposeThingInit();
        addProperty("name","The real name","/single/name",Operation.id.readproperty,td);
        addMetadataForm("/single/allprops",Operation.id.readallproperties,td);
        
        final ActionAffordance aa_toggle = new ActionAffordance();
        aa_toggle.setDefaultTitle("Toggle");
        aa_toggle.setDefaultDescription("Toggle Action");
        final Form aa_form = new Form("/single/toggle");
        aa_form.setHTTPMethodName("GET");
        aa_toggle.addForm(aa_form);
        
        td.addAction("toggle", aa_toggle);
        
        final EventAffordance ee_onoff = new EventAffordance();
        ee_onoff.addForm(new Form("/single/toggled"));
        ee_onoff.setDefaultTitle("Toggled");
        ee_onoff.setDefaultDescription("Toggled Action");
        td.addEvent("toggled", ee_onoff);
        
        td.addSecurity("basic_sc");
        final SecurityScheme sc = SecurityScheme.newInstance(SecurityScheme.typeId.siBasic);
        sc.setType("basic");
        sc.setDefaultDescription("Basic Security");
        td.addSecurityDefinition("basic_sc", sc);
        
        final ExposedWebThing to = new ExposedWebThing(td);
        to.addAction(new ToggleAction("toggle",aa_toggle,SyncActionHandler.class));
        to.addEvent(new ToggleEvent("toggled",ee_onoff));
        
        ret.add(to);
        return ret;
    }

    public static void main(String[] args) {
        
        

        try {
            // If adding more than one thing, use MultipleThings() with a name.
            // In the single thing case, the thing's name will be broadcast.
            final List<ExposedWebThing> thing = makeThing();
            final ThingServer server = new ThingServer(thing, 8888);

            Runtime.getRuntime().addShutdownHook(new Thread(server::stop));

            server.start(false);
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }
}
