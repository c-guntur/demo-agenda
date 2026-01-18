package none.cgutils.agenda.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import none.cgutils.agenda.AgendaConstants;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "AgendaProjectSettings", storages = @Storage("agenda_project_settings.xml"))
public final class AgendaProjectSettings implements PersistentStateComponent<AgendaProjectSettings.State> {

    public static final class State {
        public String incompleteIcon = null;
        public String completedIcon = null;
        public String agendaFileName = null;
    }

    private final Project project;
    private State state = new State();

    public AgendaProjectSettings(Project project) {
        this.project = project;
    }

    public static AgendaProjectSettings getInstance(@NotNull Project project) {
        return project.getService(AgendaProjectSettings.class);
    }

    @Override
    public @Nullable State getState() {
        return this.state;
    }

    @Override
    public void loadState(@NotNull State state) {
        XmlSerializerUtil.copyBean(state, this.state);
    }

    public String getIncompleteIcon() {
        String icon = this.state.incompleteIcon;
        if (StringUtils.isBlank(icon)) return AgendaConstants.DEFAULT_INCOMPLETE;
        return icon;
    }

	public void setIncompleteIcon(@Nullable String value) {
		this.state.incompleteIcon = (StringUtils.isBlank(value)) ? null : value;
	}

	public String getCompletedIcon() {
        String icon = this.state.completedIcon;
		if (StringUtils.isBlank(icon)) return AgendaConstants.DEFAULT_COMPLETED;
        return icon;
    }

    public void setCompletedIcon(@Nullable String value) {
		this.state.completedIcon = (StringUtils.isBlank(value)) ? null : value;
    }

    public String getAgendaFileName() {
        String agendaFileName = this.state.agendaFileName;
        if (StringUtils.isNotBlank(agendaFileName)) {
			return agendaFileName;
		}
		return AgendaConstants.DEFAULT_AGENDA_FILENAME;
    }

    public void setAgendaFileName(@Nullable String value) {
		this.state.agendaFileName = (StringUtils.isBlank(value)) ? null : value;
    }
}
