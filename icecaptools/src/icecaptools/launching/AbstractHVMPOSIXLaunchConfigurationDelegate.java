package icecaptools.launching;

import icecaptools.actions.ConvertJavaFileAction;
import icecaptools.debugging.DebugChannel;
import icecaptools.debugging.HVMPOSIXDebugTarget;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public abstract class AbstractHVMPOSIXLaunchConfigurationDelegate extends LaunchConfigurationDelegate {

    private static final int COMPILATION_TIMEOUT = 20;

    @Override
    public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
        monitor.beginTask("Launch HVM application", 20);
        monitor.subTask("Retreiving launch parameters");
        String sourceFolder;
        sourceFolder = configuration.getAttribute(TargetSpecificLauncherTab.SOURCE_FOLDER, "");
        if (sourceFolder.trim().length() > 0) {
            @SuppressWarnings("unused")
            boolean natives = configuration.getAttribute(TargetSpecificLauncherTab.ENABLE_NATIVE_IMPLEMENTATION, false);

            @SuppressWarnings("unused")
            String implementationFile = configuration.getAttribute(TargetSpecificLauncherTab.IMPLEMENTATION_FILE, "");

            monitor.worked(1);

            monitor.subTask("clean up");

            StringBuffer path = new StringBuffer();
            path.append(sourceFolder);
            if (!sourceFolder.endsWith("" + File.separatorChar)) {
                path.append(File.separatorChar);
            }

            path.append("main.exe");
            try {
                File leftOver = new File(path.toString());
                if (leftOver.exists()) {
                    if (leftOver.isFile()) {
                        leftOver.delete();
                    }
                }
            } catch (Throwable t) {
            }

            StringBuffer compilerCommand = getCompilerCommand(configuration);

            int requestResponseChannel = -1;
            int eventChannel = -1;

            compilerCommand.append("-DJAVA_HEAP_SIZE=" + getHeapSize(configuration.getAttribute(TargetSpecificLauncherTab.HEAPSIZE, 0)) + " ");
            if (mode.equals(ILaunchManager.DEBUG_MODE)) {
                requestResponseChannel = getRequestResponseChannel();
                eventChannel = getEventChannel();

                compilerCommand.append("-DENABLE_DEBUG -DREQUESTRESPONSECHANNEL=" + requestResponseChannel + " ");
                compilerCommand.append("-DEVENTCHANNEL=" + eventChannel + " ");
            }

            compilerCommand.append("classes.c icecapvm.c methodinterpreter.c methods.c gc.c print.c natives_allOS.c rom_heap.c allocation_point.c rom_access.c ");
            addTargetSpecificFiles(compilerCommand, configuration);

            compilerCommand.append(" -o main.exe");

            PrintStream consoleOutputStream = ConvertJavaFileAction.getConsolePrintStream();
            ConvertJavaFileAction.bringConsoleToFront(false);

            int exitValue = ShellCommand.executeCommand(compilerCommand.toString(), consoleOutputStream, true, sourceFolder, null, COMPILATION_TIMEOUT, monitor);

            switch (exitValue) {
            case ShellCommand.PROCESS_START_FAILED:
                processStartFailed(getCompilerExecutable());
                break;
            case ShellCommand.ILLEGAL_WORKINGDIRECTORY:
                illegalWorkingDirectory(sourceFolder);
                break;
            case ShellCommand.PROCESS_HANGED:
                processHanged(COMPILATION_TIMEOUT);
                break;
            default:
                if (exitValue == 0) {

                    consoleOutputStream.println("Compilation succeeded");

                    monitor.subTask("Stripping executable");

                    stripExecutable(path, consoleOutputStream, sourceFolder, monitor);

                    monitor.subTask("Executing application");

                    Process process;

                    process = startProcessOnTarget(launch, configuration, path, sourceFolder, consoleOutputStream, monitor);

                    if (process != null) {
                        if (mode.equals(ILaunchManager.DEBUG_MODE)) {
                            try {
                                IDebugTarget target;
                                DebugChannel channel = getChannel(process, requestResponseChannel, eventChannel, getTargetIPAddress(configuration));
                                IProcess p = DebugPlugin.newProcess(launch, process, "program");
                                target = new HVMPOSIXDebugTarget(launch, p, channel, monitor);
                                launch.addDebugTarget(target);
                            } catch (Exception e) {
                                consoleOutputStream.println("Attaching to debug process failed: ");
                                consoleOutputStream.println(e.getMessage());
                                ConvertJavaFileAction.bringConsoleToFront(false);
                            }
                        } else {
                            DebugPlugin.newProcess(launch, process, "program");
                        }

                        return;
                    }
                } else {
                    consoleOutputStream.println("Compilation failed for unknown reason :-(");
                    consoleOutputStream.println("Check the logs above.");
                }
            }
        } else {
        }

        IStatus status = new IStatus() {

            private static final String message = "Could not launch application";

            @Override
            public IStatus[] getChildren() {
                return null;
            }

            @Override
            public int getCode() {
                return IStatus.ERROR;
            }

            @Override
            public Throwable getException() {
                return new Exception(message);
            }

            @Override
            public String getMessage() {
                return message;
            }

            @Override
            public String getPlugin() {
                return DebugPlugin.getUniqueIdentifier();
            }

            @Override
            public int getSeverity() {
                return IStatus.ERROR;
            }

            @Override
            public boolean isMultiStatus() {
                return false;
            }

            @Override
            public boolean isOK() {
                return false;
            }

            @Override
            public boolean matches(int severityMask) {
                return false;
            };
        };
        throw new CoreException(status);
    }

    private void addTargetSpecificFiles(StringBuffer compilerCommand, ILaunchConfiguration configuration) throws CoreException {
        boolean enableNatives = configuration.getAttribute(TargetSpecificLauncherTab.ENABLE_NATIVE_IMPLEMENTATION, false);
        if (enableNatives)
        {
            String implementationFile = configuration.getAttribute(TargetSpecificLauncherTab.IMPLEMENTATION_FILE, "");

            if ((implementationFile != null) && implementationFile.trim().length() > 0) {
                compilerCommand.append(implementationFile.trim());
                compilerCommand.append(" ");
            }
        }

        addAdditionalFiles(compilerCommand, configuration);
    }

    protected abstract int getEventChannel();

    protected abstract int getRequestResponseChannel();

    protected abstract DebugChannel getChannel(Process p, int requestResponseChannel, int eventChannel, String targetIPAddress) throws IOException;

    protected abstract String getHeapSize(int string);

    protected abstract String getTargetIPAddress(ILaunchConfiguration configuration) throws CoreException;

    protected void stripExecutable(StringBuffer path, PrintStream consoleOutputStream, String sourceFolder, IProgressMonitor monitor) {
        ShellCommand.executeCommand(getStripper() + " " + path, consoleOutputStream, true, sourceFolder, null, COMPILATION_TIMEOUT, monitor);
    }

    protected abstract String getStripper();

    protected abstract void addAdditionalFiles(StringBuffer compilerCommand, ILaunchConfiguration configuration) throws CoreException;

    public static Shell getShell() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
            if (windows.length > 0) {
                return windows[0].getShell();
            }
        } else {
            return window.getShell();
        }
        return null;
    }

    public static void notify(final String message) {
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                Shell shell = getShell();
                MessageDialog dialog = new MessageDialog(shell, "Icecap Tools", null, message, MessageDialog.ERROR, new String[] { "OK" }, 0);
                dialog.open();
            }
        });
    }

    private void processHanged(final int compilationTimeout) {
        notify("Compilation did not finish within timeout (" + compilationTimeout + ") sec?");
    }

    private void illegalWorkingDirectory(final String sourceFolder) {
        notify("Could not compile in folder:\n\n\t\t" + sourceFolder + "\n\nDoes this folder exist?");
    }

    private void processStartFailed(final String compilerExecutable) {
        notify("Failed to compile executable\nIs the compiler '" + compilerExecutable + "' in your path?");
    }

    protected String getOptimizationLevel(ILaunchConfiguration configuration) throws CoreException {
        int optimLevel = configuration.getAttribute(TargetSpecificLauncherTab.GCC_OPTIMIZATION_LEVEL, 0);
        StringBuffer optim = new StringBuffer();
        switch (optimLevel) {
        case 1:
            optim.append("-O1");
            break;
        case 2:
            optim.append("-O2");            
            break;
        case 3:
            optim.append("-O3");            
            break;
        case 4:
            optim.append("-Os");            
            break;
        case 0:
        default:
            optim.append("-O0");            
            break;
        }
        return optim.toString();
    }
    
    protected abstract String getCompilerExecutable();

    protected abstract StringBuffer getCompilerCommand(ILaunchConfiguration configuration) throws CoreException;

    protected abstract Process startProcessOnTarget(ILaunch launch, ILaunchConfiguration configuration, StringBuffer path, String sourceFolder, PrintStream consoleOutputStream, IProgressMonitor monitor) throws CoreException;

}
