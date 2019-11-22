package project_Team7;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;

public class vimCommandBarAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        Messages.showInputDialog("짜장면이 좋아요 짬뽕이 좋아요?", "당신의 선택은", Messages.getQuestionIcon());

    }

    public void actionPerformed() {
        // TODO: insert action logic here
        Messages.showInputDialog("짜장면이 좋아요 짬뽕이 좋아요?", "당신의 선택은", Messages.getQuestionIcon());

    }
}
