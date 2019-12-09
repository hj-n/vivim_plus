package project_Team7.Handlers;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;
import project_Team7.VIMMode;

import java.util.ArrayList;

import static project_Team7.EditorTypedHandler.getMultiExecute;
import static project_Team7.EditorTypedHandler.setMultiExecute;

public class SearchStringHandler implements TypedHandler,CommandHandler {
    private ArrayList<Integer> searchList = new ArrayList<>();
    private String currentSearchingString = null;
    private int currentIndex;

    @Override
    public void executeCommand(String currentCommandInput, @NotNull Editor editor) {
        currentSearchingString = currentCommandInput;
        String text = editor.getDocument().getText();
        searchList.clear();
        while(text.contains(currentSearchingString)){
            int slicedIndex = text.indexOf(currentSearchingString);
            int beginIndex = slicedIndex + (editor.getDocument().getText().length() - text.length());
            searchList.add(beginIndex);
            text = text.substring(slicedIndex + currentSearchingString.length());
        }
        currentIndex = searchList.size() - 1;
        VIMMode.setMode(VIMMode.modeType.NORMAL);
        focusNextSearchString(editor, true);
    }

    @Override
    public void execute(@NotNull Editor editor, char charTyped, @NotNull DataContext dataContext) {
        switch (charTyped) {
            case 'n':
                if (VIMMode.getModeToString().equals("NORMAL MODE")) {
                    focusNextSearchString(editor, true);
                }
                break;
            case 'N':
                if (VIMMode.getModeToString().equals("NORMAL MODE")) {
                    focusNextSearchString(editor, false);
                }
                break;
        }
        VIMMode.setMode(VIMMode.modeType.NORMAL);
        modeViewer(editor);
    }

    /**
     * When the user searches something, he or she can easily access to the searched
     * strings by pressing 'n' and 'N' command. This method implements those functionality.
     * @param editor Opened Editor
     * @param isSearchingNext if true, searches forward. Else, searches backward.
     */
    private void focusNextSearchString(Editor editor, boolean isSearchingNext) {
        int start, end;
        int exeNum = 0;
        if(getMultiExecute() == 0) exeNum = 1;
        else exeNum = getMultiExecute();
        for(int i = 0; i < exeNum; i++) {

            if (searchList.size() > 0) {
                if (isSearchingNext) {
                    if (currentIndex == searchList.size() - 1)
                        currentIndex = 0;
                    else
                        currentIndex++;
                } else {
                    if (currentIndex == 0)
                        currentIndex = searchList.size() - 1;
                    else
                        currentIndex--;
                }
                start = searchList.get(currentIndex);
                end = start + currentSearchingString.length();
                editor.getCaretModel().getCurrentCaret().setSelection(start, end);
                editor.getCaretModel().getCurrentCaret().moveToOffset(start);
            }
        }
        setMultiExecute(0);
    }

}
