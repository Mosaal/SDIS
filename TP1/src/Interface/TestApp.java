package Interface;

import RMI.RMIInterface;
import Utils.Utils;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

public class TestApp
{
	public TestApp() {}

	private static void printUsage()
	{
		System.out.println("Usage: java -cp ./bin Interface.TestApp <hostname>:<remote_object_name> <sub_protocol> <opnd_1> <opnd_2>");
		System.out.println("Where:");
		System.out.println("\t<hostname>:<remote_object_name> is the name of the host where the server runs followed by the name the server bound the remote object to");
		System.out.println("\t<sub_protocol> is the operation the peer of the backup service must execute (BACKUP, RESTORE, DELETE, RECLAIM, STATE)");
		System.out.println("\t<opnd_1> is either the path name of the file to BACKUP/RESTORE/DELETE or the amount of space to reclaim (in KByte)");
		System.out.println("\t<opnd_2> is an integer that specifies the desired replication degree and applies only to the BACKUP protocol");
	}





	private static boolean procArgs(String[] paramArrayOfString)
	{
		if (paramArrayOfString.length < 2) {
			System.out.println("ERROR: Wrong number of arguments.");
			return false;
		}


		if (!paramArrayOfString[0].contains(":")) {
			System.out.println("ERROR: The first argument has an incorrect format.");
			return false;
		}


		switch (paramArrayOfString.length) {
		case 2: 
			if (!paramArrayOfString[1].equals("STATE")) {
				System.out.println("ERROR: '" + paramArrayOfString[1] + "' is not a valid argument.");
				return false;
			}
			break;
		case 3: 
			if ((paramArrayOfString[1].equals("RESTORE")) || (paramArrayOfString[1].equals("DELETE")) || (paramArrayOfString[1].equals("RECLAIM"))) {
				if (!Utils.fileExists(paramArrayOfString[2])) {
					System.out.println("ERROR: The file '" + paramArrayOfString[2] + "' does not exist.");
					return false;
				}
			} else {
				System.out.println("ERROR: '" + paramArrayOfString[1] + "' is not a valid argument.");
				return false;
			}
			break;
		case 4: 
			if (paramArrayOfString[1].equals("BACKUP")) {
				if (!Utils.fileExists(paramArrayOfString[2])) {
					System.out.println("ERROR: The file '" + paramArrayOfString[2] + "' does not exist.");
					return false;
				}

				if (!Utils.isStringInteger(paramArrayOfString[3])) {
					System.out.println("ERROR: Argument '" + paramArrayOfString[3] + "' is not an integer.");
					return false;
				}
			} else {
				System.out.println("ERROR: '" + paramArrayOfString[1] + "' is not a valid argument.");
				return false;
			}
			break;
		default: 
			System.out.println("ERROR: Wrong number of arguments.");
			return false;
		}

		return true;
	}





	public static void main(String[] paramArrayOfString)
	{
		try
		{
			Registry localRegistry = java.rmi.registry.LocateRegistry.getRegistry("localhost");
			RMIInterface localRMIInterface = (RMIInterface)localRegistry.lookup("kappa");


			String str = localRMIInterface.sayHello();
			System.out.println(str);
		} catch (RemoteException|java.rmi.NotBoundException localRemoteException) {
			localRemoteException.printStackTrace();

			printUsage();
			System.exit(-1);
		}
	}
}
