package none.cgutils.agenda;

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.List;

/**
 * Adds a gutter icon to lines starting with the agenda markers and toggles them on click.
 */
public class AgendaLineMarkerProvider implements LineMarkerProvider {

    private static final Icon GUTTER_ICON = AllIcons.FileTypes.ContextsModifier;

    @Override
    public @Nullable LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
        // We do bulk work in collectSlowLineMarkers
        return null;
    }

    @Override
    public void collectSlowLineMarkers(@NotNull List<? extends PsiElement> elements,
                                       @NotNull Collection<? super LineMarkerInfo<?>> result) {
        if (elements.isEmpty()) return;
        PsiFile file = elements.getFirst().getContainingFile();
        if (file == null || !new AgendaToggleLogic().isAgendaFileName(file.getProject(), file.getName())) return;

        Project project = file.getProject();
        Document document = file.getViewProvider().getDocument();
        if (document == null) return;

        // Build logic from per-project settings
        var settings = none.cgutils.agenda.settings.AgendaProjectSettings.getInstance(project);
        AgendaToggleLogic logic = new AgendaToggleLogic(settings.getIncompleteIcon(), settings.getCompletedIcon());

        int lineCount = document.getLineCount();
        for (int line = 0; line < lineCount; line++) {
            int start = document.getLineStartOffset(line);
            int end = document.getLineEndOffset(line);
            if (end <= start) continue;
            String text = document.getText(new TextRange(start, end));
            if (!logic.lineStartsWithMarker(text)) continue;

            PsiElement anchor = file.findElementAt(start);
            if (anchor == null) anchor = file;

            TextRange range = new TextRange(start, start);
            GutterIconNavigationHandler<PsiElement> handler = new ToggleHandler(project, document, line, logic);
            LineMarkerInfo<PsiElement> info = new LineMarkerInfo<>(
                    anchor,
                    range,
                    GUTTER_ICON,
					psi -> null,
                    handler,
                    com.intellij.openapi.editor.markup.GutterIconRenderer.Alignment.LEFT,
                    () -> "Toggle agenda item"
            );

            result.add(info);
        }
    }

	private record ToggleHandler(Project project, Document document, int line,
								 AgendaToggleLogic logic) implements GutterIconNavigationHandler<PsiElement> {

		@Override
		public void navigate(MouseEvent e, PsiElement elt) {
			AgendaToggleLogic.writeToggledIcon(project, document, line, logic);
		}
	}
}
