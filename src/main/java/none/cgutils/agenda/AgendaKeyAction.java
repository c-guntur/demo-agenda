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
import none.cgutils.agenda.settings.AgendaProjectSettings;
import org.jetbrains.annotations.NotNull;

/**
 * Keymap action to toggle the agenda marker on the current caret line.
 */
public class AgendaKeyAction extends AnAction implements DumbAware {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) return;
        Project project = e.getProject();
        if (project == null) return;
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
		AgendaProjectSettings settings = AgendaProjectSettings.getInstance(project);
		AgendaToggleLogic logic = new AgendaToggleLogic(settings);
        if (file == null || logic.isNotAgendaFileName(file.getName())) return;

        Document document = editor.getDocument();
        CaretModel caretModel = editor.getCaretModel();
        int caretOffset = caretModel.getOffset();
        int line = document.getLineNumber(caretOffset);
        logic.writeToggledIcon(project, document, line);
    }
}
