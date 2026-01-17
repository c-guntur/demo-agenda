package none.cgutils.agenda.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.extensions.PluginId;

import static none.cgutils.agenda.AgendaToggleLogic.DEFAULT_AGENDA_FILENAME;
import static none.cgutils.agenda.AgendaToggleLogic.DEFAULT_COMPLETED;
import static none.cgutils.agenda.AgendaToggleLogic.DEFAULT_INCOMPLETE;
import static none.cgutils.agenda.AgendaToggleLogic.DEFAULT_PLUGIN_NAME;
import static none.cgutils.agenda.AgendaToggleLogic.PLUGIN_ID;

public class AgendaProjectSettingsConfigurable implements Configurable {

	public static final String INCOMPLETE_ICON_LABEL = "Incomplete icon:";
	public static final String COMPLETED_ICON_LABEL = "Completed icon:";
	public static final String AGENDA_FILE_NAME_LABEL = "Agenda file name:";
	public static final String DEFAULT_SETTINGS_HINT = "Leave blank to use defaults (" +
			DEFAULT_INCOMPLETE + ", " +
			DEFAULT_COMPLETED + ", and file:" +
			DEFAULT_AGENDA_FILENAME + ")";
	public static final String KEYMAP_HINT = "Find the KeyMap for this plugin in " +
			"Settings > Keymap > Toggle Agenda Item";
	private final AgendaProjectSettings settings;
    private final Project project;
    private JPanel panel;
    private JTextField incompleteField;
    private JTextField completedField;
    private JTextField fileNameField;

    public AgendaProjectSettingsConfigurable(Project project) {
        this.project = project;
        this.settings = AgendaProjectSettings.getInstance(project);
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) String getDisplayName() {
        return getPluginDisplayName();
    }

    @Override
    public @Nullable JComponent createComponent() {
        if (panel == null) {
            panel = new JPanel(new GridBagLayout());
            GridBagConstraints gc = new GridBagConstraints();
            gc.insets = JBUI.insets(4);
            gc.anchor = GridBagConstraints.WEST;
            gc.fill = GridBagConstraints.HORIZONTAL;
            gc.weightx = 0.0;

            // Header label with bold font showing project name
            String projectName = project != null ? project.getName() : "";
            String headerText = projectName.isEmpty() ?
                    getPluginDisplayName() :
                    getPluginDisplayName() + " for [" + projectName + "]";
            JLabel headerLabel = new JLabel(headerText);
            headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD));
            gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2; gc.weightx = 1.0;
            panel.add(headerLabel, gc);

            // Reset gridwidth for subsequent components
            gc.gridwidth = 1; gc.weightx = 0.0;

            JLabel incompleteLabel = new JLabel(INCOMPLETE_ICON_LABEL);
            gc.gridx = 0; gc.gridy = 1;
            panel.add(incompleteLabel, gc);

            incompleteField = new JTextField(10);
            gc.gridx = 1; gc.gridy = 1; gc.weightx = 1.0;
            panel.add(incompleteField, gc);

            JLabel completedLabel = new JLabel(COMPLETED_ICON_LABEL);
            gc.gridx = 0; gc.gridy = 2; gc.weightx = 0.0;
            panel.add(completedLabel, gc);

            completedField = new JTextField(10);
            gc.gridx = 1; gc.gridy = 2; gc.weightx = 1.0;
            panel.add(completedField, gc);

            JLabel fileNameLabel = new JLabel(AGENDA_FILE_NAME_LABEL);
            gc.gridx = 0; gc.gridy = 3; gc.weightx = 0.0; gc.gridwidth = 1;
            panel.add(fileNameLabel, gc);

            fileNameField = new JTextField(20);
            gc.gridx = 1; gc.gridy = 3; gc.weightx = 1.0;
            panel.add(fileNameField, gc);

            JLabel hint = new JLabel(DEFAULT_SETTINGS_HINT);
            gc.gridx = 0; gc.gridy = 4; gc.gridwidth = 2; gc.weightx = 1.0;
            panel.add(hint, gc);

            JLabel keymapHint = new JLabel(KEYMAP_HINT);
            gc.gridx = 0; gc.gridy = 5; gc.gridwidth = 2; gc.weightx = 1.0;
            panel.add(keymapHint, gc);
        }
        reset();
        return panel;
    }

    @Override
    public boolean isModified() {
        if (incompleteField == null || completedField == null) return false;
        String inc = incompleteField.getText();
        String comp = completedField.getText();
        String fname = fileNameField != null ? fileNameField.getText() : null;
        // Settings store null when blank
        assert settings.getState() != null;
        String currentInc = settings.getState().incompleteIcon;
        String currentComp = settings.getState().completedIcon;
        String currentFname = settings.getState().agendaFileName;
        boolean incBlank = inc == null || inc.isBlank();
        boolean compBlank = comp == null || comp.isBlank();
        boolean fnameBlank = fname == null || fname.isBlank();
        boolean currentIncBlank = currentInc == null || currentInc.isBlank();
        boolean currentCompBlank = currentComp == null || currentComp.isBlank();
        boolean currentFnameBlank = currentFname == null || currentFname.isBlank();
        if (incBlank != currentIncBlank) return true;
        if (!incBlank && !inc.equals(currentInc)) return true;
        if (compBlank != currentCompBlank) return true;
        if (!compBlank && !comp.equals(currentComp)) return true;
        if (fileNameField != null) {
            if (fnameBlank != currentFnameBlank) return true;
            if (!fnameBlank && !fname.equals(currentFname)) return true;
        }
        return false;
    }

    @Override
    public void apply() {
        String inc = incompleteField.getText();
        String comp = completedField.getText();
        settings.setIncompleteIcon(inc);
        settings.setCompletedIcon(comp);
        if (fileNameField != null) {
            settings.setAgendaFileName(fileNameField.getText());
        }
    }

    @Override
    public void reset() {
        if (incompleteField != null) {
            assert settings.getState() != null;
            String raw = settings.getState().incompleteIcon;
            incompleteField.setText(raw == null ? "" : raw);
        }
        if (completedField != null) {
            assert settings.getState() != null;
            String raw = settings.getState().completedIcon;
            completedField.setText(raw == null ? "" : raw);
        }
        if (fileNameField != null) {
            assert settings.getState() != null;
            String raw = settings.getState().agendaFileName;
            fileNameField.setText(raw == null ? "" : raw);
        }
    }

    private static String getPluginDisplayName() {
        try {
            var descriptor = PluginManagerCore.getPlugin(PluginId.getId(PLUGIN_ID));
            if (descriptor != null && descriptor.getName() != null && !descriptor.getName().isBlank()) {
                return descriptor.getName();
            }
        } catch (Throwable ignored) {
            // Fallback below
        }
        return DEFAULT_PLUGIN_NAME;
    }
}
