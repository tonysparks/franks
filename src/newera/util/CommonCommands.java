/*
 * see license.txt 
 */
package newera.util;

/**
 * @author Tony
 *
 */
public class CommonCommands {

	public static void addCommonCommands(Console console) {
		console.addCommand(new ExecCommand());
	}
}
