package project_Team7.HandlerMap;

import com.intellij.openapi.util.Pair;
import project_Team7.Handlers.*;

import java.util.HashMap;
import java.util.Map;


public class TypedHandlerMap extends BaseHandlerMap {

    private Map<Pair<String, Character>, TypedHandler> typedHandlerMap;
    private static TypedHandlerMap uniqueInstance = new TypedHandlerMap();

    private TypedHandlerMap() {
        typedHandlerMap = new HashMap<>();
        typedHandlerMap.put(new Pair<>("NORMAL MODE",'t'), treeHandler);
        typedHandlerMap.put(new Pair<>("NORMAL MODE",'e'), terminalHandler);
        typedHandlerMap.putAll(createPartMap("NORMAL MODE", new char[]{'i', 'I','a','A','o','O'}, normalToInsertHandler));
        typedHandlerMap.putAll(createPartMap("NORMAL MODE", new char[]{'v', 'V','h','j','k','l','$','w','b'}, cursorVisualHandler));
        typedHandlerMap.putAll(createPartMap("VISUAL MODE", new char[]{'h','j','k','l','$','w','b'}, cursorVisualHandler));
        typedHandlerMap.putAll(createPartMap("NORMAL MODE", new char[]{'d','y','p','P'}, textEditHandler));
        typedHandlerMap.putAll(createPartMap("VISUAL MODE", new char[]{'d','y','p','P'}, textEditHandler));
        typedHandlerMap.putAll(createPartMap("NORMAL MODE", new char[]{'J','K'}, moveOpenTabHandler));
        typedHandlerMap.putAll(createPartMap("NORMAL MODE", new char[]{':','/'}, commandMapHandler));
        typedHandlerMap.putAll(createPartMap("NORMAL MODE", new char[]{'n','N'}, searchStringHandler));
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
