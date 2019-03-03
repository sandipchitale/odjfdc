package opendotjavafordotclass;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.internal.core.NamedMember;
import org.eclipse.jdt.internal.core.SourceType;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.internal.ui.javaeditor.IClassFileEditorInput;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.ITextEditor;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
@SuppressWarnings("restriction")
public class Activator extends AbstractUIPlugin implements IStartup {

	// The plug-in ID
	public static final String PLUGIN_ID = "OpenDotJavaForDotClass"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	@Override
	public void earlyStartup() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		Job uiJob = new UIJob("Register Part Listener") {
			@Override
			public IStatus runInUIThread(final IProgressMonitor monitor) {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService()
						.addPartListener(new IPartListener() {
							@Override
							public void partOpened(final IWorkbenchPart part) {
								if (part instanceof ITextEditor) {
									final ITextEditor[] textEditor = new ITextEditor[1];
									textEditor[0] = (ITextEditor) part;
									IEditorInput editorInput = textEditor[0].getEditorInput();
									if (editorInput instanceof IClassFileEditorInput) {
										IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode("OpenDotJavaForDotClass");
										IClassFileEditorInput classFileEditorInput = (IClassFileEditorInput) editorInput;
										IClassFile classFile = classFileEditorInput.getClassFile();
										@SuppressWarnings("deprecation")
										SearchPattern pattern = SearchPattern.createPattern(
												classFile.getType().getFullyQualifiedName(), IJavaSearchConstants.TYPE,
												IJavaSearchConstants.DECLARATIONS, SearchPattern.R_EXACT_MATCH);
										IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
										SearchRequestor requestor = new SearchRequestor() {
											public void acceptSearchMatch(final SearchMatch match) {
												final IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench()
														.getActiveWorkbenchWindow();
												activeWorkbenchWindow.getShell().getDisplay().asyncExec(new Runnable() {
													@Override
													public void run() {
														Object element = match.getElement();
														if (element instanceof NamedMember) {
															final NamedMember namedElement = (NamedMember) element;
															if (namedElement instanceof SourceType) {
																IWorkbenchPage activePage = activeWorkbenchWindow
																		.getActivePage();
																SourceType sourceType = (SourceType) namedElement;
																int offset = -1;
																if (textEditor[0] != null) {
																	Control control = textEditor[0]
																			.getAdapter(Control.class);
																	if (control instanceof StyledText) {
																		StyledText text = (StyledText) control;
																		offset = text.getCaretOffset();
																	}
																	boolean doNotCloseDotJava = preferences.getBoolean(
																			ODJFODCPreferencesPage.DO_NOT_CLOSE_DOT_CLASS,
																			false);
																	if (!doNotCloseDotJava) {
																		activePage.closeEditor(textEditor[0], false);
																		textEditor[0] = null;
																	}
																}
																try {
																	IEditorPart sourceEditorPart = EditorUtility
																			.openInEditor(sourceType, true);
																	if (sourceEditorPart instanceof ITextEditor) {
																		ITextEditor sourceEditor = (ITextEditor) sourceEditorPart;
																		if (offset != -1) {
																			Control sourceControl = sourceEditor
																					.getAdapter(Control.class);
																			if (sourceControl instanceof StyledText) {
																				StyledText sourceText = (StyledText) sourceControl;
																				sourceText.setSelection(offset);
																				sourceText.showSelection();
																			}
																		}
																	}

																} catch (PartInitException e) {
																}
															}
														}
													}
												});
											}
										};
										SearchEngine searchEngine = new SearchEngine();
										try {
											searchEngine.search(pattern,
													new SearchParticipant[] {
															SearchEngine.getDefaultSearchParticipant() },
													scope, requestor, null /* progress monitor */);
										} catch (CoreException e) {
										}
									}
								}
							}

							@Override
							public void partDeactivated(IWorkbenchPart part) {
							}

							@Override
							public void partClosed(IWorkbenchPart part) {
							}

							@Override
							public void partBroughtToTop(IWorkbenchPart part) {
							}

							@Override
							public void partActivated(IWorkbenchPart part) {
							}
						});
				return Status.OK_STATUS;
			}
		};
		uiJob.schedule();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
}
