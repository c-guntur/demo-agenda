package none.cgutils.agenda.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import none.cgutils.agenda.AgendaToggleLogic;
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
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        XmlSerializerUtil.copyBean(state, this.state);
    }

    public String getIncompleteIcon() {
        String v = state.incompleteIcon;
        if (v == null || v.isBlank()) return AgendaToggleLogic.DEFAULT_INCOMPLETE;
        return v;
    }

    public String getCompletedIcon() {
        String v = state.completedIcon;
        if (v == null || v.isBlank()) return AgendaToggleLogic.DEFAULT_COMPLETED;
        return v;
    }

    public void setIncompleteIcon(@Nullable String value) {
        state.incompleteIcon = (value == null || value.isBlank()) ? null : value;
    }

    public void setCompletedIcon(@Nullable String value) {
        state.completedIcon = (value == null || value.isBlank()) ? null : value;
    }

    public String getAgendaFileName() {
        String agendaFileName = state.agendaFileName;
        if (StringUtils.isNotBlank(agendaFileName)) {
			return agendaFileName;
		}
		return "Agenda.txt";
    }

    public void setAgendaFileName(@Nullable String value) {
        state.agendaFileName = (value == null || value.isBlank()) ? null : value;
    }
}
