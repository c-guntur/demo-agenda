package none.cgutils.agenda;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import none.cgutils.agenda.settings.AgendaProjectSettings;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

/**
 * UI-free logic for determining toggle behavior in Agenda files.
 * Used by both gutter line markers and key action.
 */
public final class AgendaToggleLogic {

	public final String incompleteIcon;
    public final String completedIcon;
	public final String agendaFileName;

	record AgendaToggleResult(int start, int end, String toggled) {}

    public AgendaToggleLogic() {
        this(AgendaConstants.DEFAULT_INCOMPLETE, AgendaConstants.DEFAULT_COMPLETED, AgendaConstants.DEFAULT_AGENDA_FILENAME);
    }

    public AgendaToggleLogic(String incompleteIcon, String completed, String agendaFileName) {
        this.incompleteIcon = incompleteIcon;
        this.completedIcon = completed;
		this.agendaFileName = agendaFileName;
    }

    public AgendaToggleLogic(AgendaProjectSettings settings) {
		if (settings != null) {
			this.incompleteIcon = settings.getIncompleteIcon();
			this.completedIcon = settings.getCompletedIcon();
			this.agendaFileName = settings.getAgendaFileName();
		} else {
			this.incompleteIcon = AgendaConstants.DEFAULT_INCOMPLETE;
			this.completedIcon = AgendaConstants.DEFAULT_COMPLETED;
			this.agendaFileName = AgendaConstants.DEFAULT_AGENDA_FILENAME;
		}
    }

	public boolean isNotAgendaFileName(String fileName) {
		if (fileName == null) return true;
		return !StringUtils.equalsIgnoreCase(this.agendaFileName, fileName);
	}

	public boolean lineStartsWithMarker(String line) {
		return line != null && (line.startsWith(this.incompleteIcon) || line.startsWith(this.completedIcon));
	}

	public void writeToggledIcon(Project project, Document document, int line) {
		AgendaToggleResult result = getAgendaToggleResult(document, line);
		if (result == null) return;

		WriteCommandAction.runWriteCommandAction(project, () ->
				document.replaceString(result.start(), result.end(), result.toggled())
		);
	}

	@Nullable AgendaToggleResult getAgendaToggleResult(Document document, int line) {
		int start = this.getLineStartOffset(document, line);
		int end = this.getLineEndOffset(document, line);

		String text = document.getText(new TextRange(start, end));
		if (!this.lineStartsWithMarker(text)) return null;
		String toggled = this.toggleLine(text);
		if (toggled.equals(text)) return null;
		AgendaToggleResult result = new AgendaToggleResult(start, end, toggled);
		return result;
	}

	int getLineStartOffset(Document document, int line) {
		return document.getLineStartOffset(line);
	}

	int getLineEndOffset(Document document, int line) {
		return document.getLineEndOffset(line);
	}

    String toggleLine(String line) {
        if (line == null || line.isEmpty()) return line;
        if (line.startsWith(this.incompleteIcon)) {
            return this.completedIcon + line.substring(this.incompleteIcon.length());
        }
        if (line.startsWith(this.completedIcon)) {
            return this.incompleteIcon + line.substring(this.completedIcon.length());
        }
        return line;
    }
}
