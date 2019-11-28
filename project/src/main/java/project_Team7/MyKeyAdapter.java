package project_Team7;

import com.intellij.psi.*;
import com.intellij.ui.KeyStrokeAdapter;

import java.awt.event.KeyEvent;
import java.util.*;

/**
 * This class has methods that overrides methods of KeyStrokeAdapter.
 * We implemented the keyPressed function to get the character that
 * user typed and navigate to the PsiElement.
 * */
public class MyKeyAdapter extends KeyStrokeAdapter {
    private HashMap<String, PsiElement> strToClass;
    private HashMap<PsiElement, String> classToStr;
    private HashMap<String, PsiElement> currentStrToClass;
    private HashMap<PsiElement, String> currentClassToStr;
    private ProjectStructureTree projectTree;

    /** Construct MyKeyAdapter by reference maps of shortcuts and project tree of ProjectStructureTree.java */
    MyKeyAdapter(HashMap <String, PsiElement> strMap,  HashMap <PsiElement, String> classMap,
                 HashMap <String, PsiElement> curStrMap,  HashMap <PsiElement, String> curClassMap,
                 ProjectStructureTree tree) {
        super();
        strToClass = strMap;
        classToStr = classMap;
        currentStrToClass = curStrMap;
        currentClassToStr = curClassMap;
        projectTree = tree;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        super.keyTyped(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        /** This part is for erasing first character of the key if it is same as keyCode */
        if(keyCode >= KeyEvent.VK_A && keyCode <= KeyEvent.VK_M)
        {
            char input = (char) keyCode;
            currentClassToStr.entrySet().removeIf(key -> key.getValue().charAt(0) != input);
            for (PsiElement key : currentClassToStr.keySet()) {
                if (currentClassToStr.get(key).charAt(0) == input) {
                    currentClassToStr.replace(key, currentClassToStr.get(key).substring(1));
                }
            }
            /** This part get first string of the map for render the tree */
            String firstKey = "";
            for (String key: currentClassToStr.values())
            {
                firstKey = key;
                break;
            }
            /** This part recover currentClassToStr if all shortcuts are erased */
            if(currentClassToStr.isEmpty()) {
                for (String key : currentStrToClass.keySet()) {
                    currentClassToStr.put(currentStrToClass.get(key), key);
                }
            }
            /** This part update currentStrToClass if shortcuts are remained */
            else {
                currentStrToClass.clear();
                for (PsiElement key : currentClassToStr.keySet()) {
                    currentStrToClass.put(currentClassToStr.get(key), key);
                }
                /** Call updatetree to render the tree */
                projectTree.publicUpdateTree(currentStrToClass.get(firstKey));
            }

        }

        /** This part is for navigating to the PsiElement */
        else if(keyCode >= KeyEvent.VK_N && keyCode <= KeyEvent.VK_Z)
        {
            PsiElement element = currentStrToClass.get(Character.toString((char) keyCode));
            if(element != null)
            {
                ((PsiDocCommentOwner) element).navigate(true);
                EditorTypedHandler.setStoredChar('x');
                VIMMode.setMode(VIMMode.modeType.NORMAL);

                currentStrToClass.clear();
                currentClassToStr.clear();
                currentStrToClass.putAll(strToClass);
                currentClassToStr.putAll(classToStr);
            }
        }

        /** This part if for returning back to the initial state */
        else if(keyCode == KeyEvent.VK_ESCAPE)
        {
            currentStrToClass.clear();
            currentClassToStr.clear();
            currentStrToClass.putAll(strToClass);
            currentClassToStr.putAll(classToStr);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        super.keyReleased(e);
    }
}
