package project_Team7.HandlerMap;

import javafx.util.Pair;
import project_Team7.TypedHandler.*;

import java.util.HashMap;
import java.util.Map;

public class TypedHandlerMap extends BaseHandlerMap {

    private Map<Pair<String, Character>, TypedHandler> typedHandlerMap;
    private static TypedHandlerMap uniqueInstance = new TypedHandlerMap();

    private TypedHandlerMap() {
        typedHandlerMap = new HashMap<>();
        TypedHandler handler;
        typedHandlerMap.put(new Pair<>("NORMAL MODE",'t'), treeHandler);
        typedHandlerMap.putAll(createPartMap("NORMAL MODE",
                new char[]{'i', 'I','a','A','o','O'}, new NormalToInsertHandler()));

        handler = new CursorVisualHandler();
        typedHandlerMap.putAll(createPartMap("NORMAL MODE", new char[]{'v', 'V','h','j','k','l','$','w','b'}, handler));
        typedHandlerMap.putAll(createPartMap("VISUAL MODE", new char[]{'h','j','k','l','$','w','b'}, handler));

        handler = new TextEditHandler();
        typedHandlerMap.putAll(createPartMap("NORMAL MODE", new char[]{'d','y','p','P'}, handler));
        typedHandlerMap.putAll(createPartMap("VISUAL MODE", new char[]{'d','y','p','P'}, handler));

        typedHandlerMap.putAll(createPartMap("NORMAL MODE", new char[]{'J','K'}, new MoveOpenTabHandler()));
        typedHandlerMap.putAll(createPartMap("NORMAL MODE", new char[]{':','/','n','N'}, new CommandMapHandler()));
    }

    static public Map<Pair<String, Character>, TypedHandler> getMap() {

        return uniqueInstance.typedHandlerMap;
    }


    private Map<Pair<String, Character>, TypedHandler> createPartMap(String mode, char[] chars, TypedHandler handler) {
        Map<Pair<String, Character>, TypedHandler> handlers = new HashMap<>();
        for(char key:chars) {
            handlers.put(new Pair<>(mode, key), handler);
        }
        return handlers;
    }
}
