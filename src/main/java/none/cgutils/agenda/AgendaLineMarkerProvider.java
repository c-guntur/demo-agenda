package none.cgutils.agenda;

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import none.cgutils.agenda.settings.AgendaProjectSettings;
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

	public static final Icon GUTTER_ICON = AllIcons.FileTypes.ContextsModifier;

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
        if (file == null || new AgendaToggleLogic().isNotAgendaFileName(file.getName())) return;

        Project project = file.getProject();
        Document document = file.getViewProvider().getDocument();
        if (document == null) return;

        // Build logic from per-project settings
        var settings = AgendaProjectSettings.getInstance(project);
        AgendaToggleLogic logic = new AgendaToggleLogic(settings);

        int lineCount = document.getLineCount();
        for (int line = 0; line < lineCount; line++) {
			var toggleResult = logic.getAgendaToggleResult(document, line);
			if (toggleResult == null) continue;

            PsiElement anchor = file.findElementAt(toggleResult.start());
            if (anchor == null) anchor = file;

            TextRange range = new TextRange(toggleResult.start(), toggleResult.start());
            GutterIconNavigationHandler<PsiElement> handler = new ToggleHandler(project, document, line, logic);
            LineMarkerInfo<PsiElement> info = new LineMarkerInfo<>(
                    anchor,
                    range,
                    GUTTER_ICON,
					psi -> null,
                    handler,
                    GutterIconRenderer.Alignment.LEFT,
                    () -> "Toggle agenda item"
            );

            result.add(info);
        }
    }

	private record ToggleHandler(Project project, Document document, int line,
								 AgendaToggleLogic logic) implements GutterIconNavigationHandler<PsiElement> {

		@Override
		public void navigate(MouseEvent e, PsiElement elt) {
			this.logic.writeToggledIcon(this.project, this.document, this.line);
		}
	}
}
