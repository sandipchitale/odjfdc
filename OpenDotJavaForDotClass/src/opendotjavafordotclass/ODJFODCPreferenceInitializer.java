package opendotjavafordotclass;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class ODJFODCPreferenceInitializer extends AbstractPreferenceInitializer {

	public ODJFODCPreferenceInitializer() {
	}

	@Override
	public void initializeDefaultPreferences() {
		ScopedPreferenceStore scopedPreferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, "com.vogella.preferences.page");
        scopedPreferenceStore.setDefault(ODJFODCPreferencesPage.DO_NOT_CLOSE_DOT_CLASS,
        		false);
        scopedPreferenceStore.setDefault(ODJFODCPreferencesPage.DO_NOT_OPEN_DOT_JAVA,
        		false);
    }

}
