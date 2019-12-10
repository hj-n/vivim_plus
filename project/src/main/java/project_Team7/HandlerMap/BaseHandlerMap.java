package project_Team7.HandlerMap;

import project_Team7.Handlers.*;

public class BaseHandlerMap {
    protected static final MoveLineHandler moveLineHandler = new MoveLineHandler();
    protected static final TreeHandler treeHandler = new TreeHandler();
    protected static final CodeSegmentHandler codeSegmentHandler = new CodeSegmentHandler();
    protected static final FileHandler fileHandler = new FileHandler();
    protected static final SearchStringHandler searchStringHandler = new SearchStringHandler();
    protected static final CommandMapHandler commandMapHandler = new CommandMapHandler();
    protected static final NormalToInsertHandler normalToInsertHandler = new NormalToInsertHandler();
    protected static final CursorVisualHandler cursorVisualHandler = new CursorVisualHandler();
    protected static final TextEditHandler textEditHandler = new TextEditHandler();
    protected static final MoveOpenTabHandler moveOpenTabHandler = new MoveOpenTabHandler();
    protected static final TerminalHandler terminalHandler = new TerminalHandler();
    protected static final UMLHandler umlHandler = new UMLHandler();
    protected static final MultiExecuteHandler multiExecuteHandler = new MultiExecuteHandler();
}
