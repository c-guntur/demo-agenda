package none.cgutils.agenda.settings;

import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.JBUI;
import none.cgutils.agenda.AgendaConstants;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

import static none.cgutils.agenda.AgendaConstants.DEFAULT_PLUGIN_NAME;
import static none.cgutils.agenda.AgendaConstants.PLUGIN_ID;

public class AgendaProjectSettingsConfigurable implements Configurable {

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
        if (this.panel == null) {
            this.panel = new JPanel(new GridBagLayout());
            GridBagConstraints gc = new GridBagConstraints();
            gc.insets = JBUI.insets(4);
            gc.anchor = GridBagConstraints.WEST;
            gc.fill = GridBagConstraints.HORIZONTAL;
            gc.weightx = 0.0;

            // Header label with bold font showing project name
            String projectName = this.project != null ? this.project.getName() : "";
            String headerText = projectName.isEmpty() ?
                    getPluginDisplayName() :
                    getPluginDisplayName() + " for [" + projectName + "]";
            JLabel headerLabel = new JLabel(headerText);
            headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD));
            gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2; gc.weightx = 1.0;
            this.panel.add(headerLabel, gc);

            // Reset gridwidth for subsequent components
            gc.gridwidth = 1; gc.weightx = 0.0;

            JLabel incompleteLabel = new JLabel(AgendaConstants.INCOMPLETE_ICON_LABEL);
            gc.gridx = 0; gc.gridy = 1;
            this.panel.add(incompleteLabel, gc);

            this.incompleteField = new JTextField(10);
            gc.gridx = 1; gc.gridy = 1; gc.weightx = 1.0;
            this.panel.add(this.incompleteField, gc);

            JLabel completedLabel = new JLabel(AgendaConstants.COMPLETED_ICON_LABEL);
            gc.gridx = 0; gc.gridy = 2; gc.weightx = 0.0;
            this.panel.add(completedLabel, gc);

            this.completedField = new JTextField(10);
            gc.gridx = 1; gc.gridy = 2; gc.weightx = 1.0;
            this.panel.add(this.completedField, gc);

            JLabel fileNameLabel = new JLabel(AgendaConstants.AGENDA_FILE_NAME_LABEL);
            gc.gridx = 0; gc.gridy = 3; gc.weightx = 0.0; gc.gridwidth = 1;
            this.panel.add(fileNameLabel, gc);

            this.fileNameField = new JTextField(20);
            gc.gridx = 1; gc.gridy = 3; gc.weightx = 1.0;
            this.panel.add(this.fileNameField, gc);

            JLabel hint = new JLabel(AgendaConstants.DEFAULT_SETTINGS_HINT);
            gc.gridx = 0; gc.gridy = 4; gc.gridwidth = 2; gc.weightx = 1.0;
            this.panel.add(hint, gc);

            JLabel keymapHint = new JLabel(AgendaConstants.KEYMAP_HINT);
            gc.gridx = 0; gc.gridy = 5; gc.gridwidth = 2; gc.weightx = 1.0;
            this.panel.add(keymapHint, gc);
        }
        reset();
        return this.panel;
    }

    @Override
    public boolean isModified() {
        if (this.incompleteField == null || this.completedField == null) return false;
        String inc = this.incompleteField.getText();
        String comp = this.completedField.getText();
        String fname = this.fileNameField != null ? this.fileNameField.getText() : null;
        // Settings store null when blank
        assert this.settings.getState() != null;
        String currentInc = this.settings.getState().incompleteIcon;
        String currentComp = this.settings.getState().completedIcon;
        String currentFname = this.settings.getState().agendaFileName;
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
        if (this.fileNameField != null) {
            if (fnameBlank != currentFnameBlank) return true;
            if (!fnameBlank && !fname.equals(currentFname)) return true;
        }
        return false;
    }

    @Override
    public void apply() {
        String inc = this.incompleteField.getText();
        String comp = this.completedField.getText();
        this.settings.setIncompleteIcon(inc);
        this.settings.setCompletedIcon(comp);
        if (this.fileNameField != null) {
            this.settings.setAgendaFileName(this.fileNameField.getText());
        }
    }

    @Override
    public void reset() {
        if (this.incompleteField != null) {
            assert this.settings.getState() != null;
            String raw = this.settings.getState().incompleteIcon;
            this.incompleteField.setText(raw == null ? "" : raw);
        }
        if (this.completedField != null) {
            assert this.settings.getState() != null;
            String raw = this.settings.getState().completedIcon;
            this.completedField.setText(raw == null ? "" : raw);
        }
        if (this.fileNameField != null) {
            assert this.settings.getState() != null;
            String raw = this.settings.getState().agendaFileName;
            this.fileNameField.setText(raw == null ? "" : raw);
        }
    }

    private String getPluginDisplayName() {
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
