package project_Team7.HandlerMap;

import project_Team7.Handlers.CommandHandler;

import java.util.HashMap;
import java.util.Map;

public class CommandHandlerMap extends BaseHandlerMap {
    private Map<String, CommandHandler> commandHandlerMap;
    private static CommandHandlerMap uniqueInstance = new CommandHandlerMap();

    private CommandHandlerMap() {
        commandHandlerMap = new HashMap<>();
        commandHandlerMap.putAll(createPartMap(new String[]{"move","fold","unfold"}, treeHandler));
        commandHandlerMap.putAll(createPartMap(new String[]{"show","hide"}, codeSegmentHandler));
        commandHandlerMap.putAll(createPartMap(new String[]{"w","q","wq","!q"}, fileHandler));
        commandHandlerMap.putAll(createPartMap(new String[]{"/"}, searchStringHandler));
    }

    static public Map<String, CommandHandler> getMap() {
        return uniqueInstance.commandHandlerMap;
    }


    private Map<String, CommandHandler> createPartMap(String[] commands, CommandHandler handler) {
        Map<String, CommandHandler> handlers = new HashMap<>();
        for(String key:commands) {
            handlers.put(key, handler);
        }
        return handlers;
    }
}
