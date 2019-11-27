package project_Team7;

import com.intellij.psi.*;
import com.intellij.ui.KeyStrokeAdapter;

import java.awt.event.KeyEvent;
import java.util.*;

/**
 *Create this class because of refactoring
 *
 * */
public class MyKeyAdapter extends KeyStrokeAdapter {
    private HashMap<String, PsiElement> strToClass;
    private HashMap<PsiElement, String> classToStr;
    private HashMap<String, PsiElement> curStrToClass;
    private HashMap<PsiElement, String> curClassToStr;
    private ProjectStructureTree projectTree;


    MyKeyAdapter(HashMap <String, PsiElement> strMap,  HashMap <PsiElement, String> classMap,
                 HashMap <String, PsiElement> curStrMap,  HashMap <PsiElement, String> curClassMap,
                 ProjectStructureTree tree) {
        super();
        strToClass = strMap;
        classToStr = classMap;
        curStrToClass = curStrMap;
        curClassToStr = curClassMap;
        projectTree = tree;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        super.keyTyped(e);

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if(keyCode >= KeyEvent.VK_A && keyCode <= KeyEvent.VK_M)
        {
            char input = (char) keyCode;
            curClassToStr.entrySet().removeIf(key -> key.getValue().charAt(0) != input);
            for (PsiElement key : curClassToStr.keySet()) {
                if (curClassToStr.get(key).charAt(0) == input) {
                    curClassToStr.replace(key, curClassToStr.get(key).substring(1));
                }
            }
            String firstKey = "";
            for (String key: curClassToStr.values())
            {
                firstKey = key;
                break;
            }
            if(curClassToStr.isEmpty()) {
                for (String key : curStrToClass.keySet()) {
                    curClassToStr.put(curStrToClass.get(key), key);
                }
            }
            else {
                curStrToClass.clear();
                for (PsiElement key : curClassToStr.keySet()) {
                    curStrToClass.put(curClassToStr.get(key), key);
                }
                projectTree.publicUpdateTree(curStrToClass.get(firstKey));
            }

        }
        else if(keyCode >= KeyEvent.VK_N && keyCode <= KeyEvent.VK_Z)
        {
            PsiElement element = curStrToClass.get(Character.toString((char) keyCode));
            if(element != null)
            {

                ((PsiDocCommentOwner) element).navigate(true);
                modeEnum.setMode(modeEnum.modeType.NORMAL);

                curStrToClass.clear();
                curClassToStr.clear();
                curStrToClass.putAll(strToClass);
                curClassToStr.putAll(classToStr);
            }
        }
        else if(keyCode == KeyEvent.VK_ESCAPE)
        {
            curStrToClass.clear();
            curClassToStr.clear();
            curStrToClass.putAll(strToClass);
            curClassToStr.putAll(classToStr);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        super.keyReleased(e);
    }
}
