package none.cgutils.agenda;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * Keymap action to toggle the agenda marker on the current caret line.
 */
public class AgendaKeyAction extends AnAction implements DumbAware {

    // Logic is built from per-project settings at runtime

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // Avoid deprecated API: use getData and handle null editor context
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) return;
        Project project = e.getProject();
        if (project == null) return;
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
        if (file == null || !new AgendaToggleLogic().isAgendaFileName(project, file.getName())) return;

        Document document = editor.getDocument();
        CaretModel caretModel = editor.getCaretModel();
        int caretOffset = caretModel.getOffset();
        int line = document.getLineNumber(caretOffset);
        var settings = none.cgutils.agenda.settings.AgendaProjectSettings.getInstance(project);
        AgendaToggleLogic logic = new AgendaToggleLogic(settings.getIncompleteIcon(), settings.getCompletedIcon());
        AgendaToggleLogic.writeToggledIcon(project, document, line, logic);
    }
}
