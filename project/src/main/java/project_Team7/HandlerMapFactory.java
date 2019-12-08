package project_Team7;

import javafx.util.Pair;
import project_Team7.TypedHandler.*;

import java.util.HashMap;
import java.util.Map;

public class HandlerMapFactory {
    static public Map<Pair<String, Character>, TypedHandler> createHandlerMap() {
        Map<Pair<String, Character>, TypedHandler> handlers = new HashMap<>();
        TypedHandler handler;
        handlers.put(new Pair<>("NORMAL MODE",'t'), new TreeHandler());
        handlers.putAll(createPartMap("NORMAL MODE",
                new char[]{'i', 'I','a','A','o','O'}, new NormalToInsertHandler()));

        handler = new CursorVisualHandler();
        handlers.putAll(createPartMap("NORMAL MODE", new char[]{'v', 'V','h','j','k','l','$','w','b'}, handler));
        handlers.putAll(createPartMap("VISUAL MODE", new char[]{'h','j','k','l','$','w','b'}, handler));

        handler = new TextEditHandler();
        handlers.putAll(createPartMap("NORMAL MODE", new char[]{'d','y','p','P'}, handler));
        handlers.putAll(createPartMap("VISUAL MODE", new char[]{'d','y','p','P'}, handler));

        handlers.putAll(createPartMap("NORMAL MODE", new char[]{'J','K'}, new MoveOpenTabHandler()));
        handlers.putAll(createPartMap("NORMAL MODE", new char[]{':','/','n','N'}, new CommandHandler()));
        return handlers;
    }


    private static Map<Pair<String, Character>, TypedHandler> createPartMap(String mode, char[] chars, TypedHandler handler) {
        Map<Pair<String, Character>, TypedHandler> handlers = new HashMap<>();
        for(char key:chars) {
            handlers.put(new Pair<>(mode, key), handler);
        }
        return handlers;
    }
}
