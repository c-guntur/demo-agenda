package none.cgutils.agenda;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;

/**
 * UI-free logic for determining toggle behavior in Agenda files.
 * Used by both gutter line markers and key action.
 */
public final class AgendaToggleLogic {
	public static final String DEFAULT_PLUGIN_NAME = "Demo Agenda";
	public static final String PLUGIN_ID = "none.cgutils.intellij.plugins.agenda";

	public static final String DEFAULT_INCOMPLETE = "🔵"; // U+1F535
    public static final String DEFAULT_COMPLETED = "✅";   // U+2705
    public static final String DEFAULT_AGENDA_FILENAME = "Agenda.txt";

    public final String incompleteIcon;
    public final String completedIcon;

    public AgendaToggleLogic() {
        this(DEFAULT_INCOMPLETE, DEFAULT_COMPLETED);
    }

    public AgendaToggleLogic(String incompleteIcon, String completed) {
        this.incompleteIcon = incompleteIcon;
        this.completedIcon = completed;
    }

	public static void writeToggledIcon(Project project, Document document, int line, AgendaToggleLogic logic) {
		int start = logic.getLineStartOffset(document, line);
		int end = logic.getLineEndOffset(document, line);

		String text = document.getText(new TextRange(start, end));
		if (!logic.lineStartsWithMarker(text)) return;
		String toggled = logic.toggleLine(text);
		if (toggled.equals(text)) return;

		WriteCommandAction.runWriteCommandAction(project, () ->
				document.replaceString(start, end, toggled)
		);
	}

 public boolean isAgendaFileName(Project project, String fileName) {
        if (fileName == null) return false;
        var settings = none.cgutils.agenda.settings.AgendaProjectSettings.getInstance(project);
        String expected = settings.getAgendaFileName();
        return expected.equalsIgnoreCase(fileName);
    }

    public boolean lineStartsWithMarker(String line) {
        return line != null && (line.startsWith(incompleteIcon) || line.startsWith(completedIcon));
    }

    public boolean lineStartsWithIncomplete(String line) {
        return line != null && line.startsWith(incompleteIcon);
    }

    public String toggleLine(String line) {
        if (line == null || line.isEmpty()) return line;
        if (line.startsWith(incompleteIcon)) {
            return completedIcon + line.substring(incompleteIcon.length());
        }
        if (line.startsWith(completedIcon)) {
            return incompleteIcon + line.substring(completedIcon.length());
        }
        return line;
    }

    public int getLineStartOffset(Document document, int line) {
        return document.getLineStartOffset(line);
    }

    public int getLineEndOffset(Document document, int line) {
        return document.getLineEndOffset(line);
    }
}
