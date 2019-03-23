import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.util.Scanner;

public class Client {


  public static void main(String [] args) {
    int port = 9091;
    if (args.length == 1) {
      try {
        port = Integer.parseInt(args[0]);
      } catch (Exception e) {
        System.out.println("Unable to parse an integer for port");
        System.exit(1);
      }
    } else {
      System.out.println("Default port is 9090");
    }

    try {
      TTransport transport;
      transport = new TSocket("localhost", port);
      transport.open();

      TProtocol protocol = new TBinaryProtocol(transport);

      ServerService.Client client = new ServerService.Client(protocol);
      String clientInfo = "localhost:" + port;
      perform(client, clientInfo);

      transport.close();
    } catch (TException x) {
      x.printStackTrace();
    }
  }


  private static void perform(ServerService.Client client, String clientInfo) throws TException {
    while (true) {
      boolean optionFlag = true;
      int opt = 0;
      while (optionFlag) {
        System.out.println("Press 1 TO PUT\nPress 2 TO GET\n Press 3 to DELETE\nPress 4 to exit");
        Scanner sc = new Scanner(System.in);
        String option = sc.nextLine();
        try {
          opt = Integer.parseInt(option);
        } catch (Exception E) {
          continue;
        }
        if (opt == 1 || opt == 2 || opt == 3 || opt == 4) {
          optionFlag = false;
        }
      }
      int keyInt = 0;
      switch (opt) {
        case 1:

          System.out.println("PUT REQUEST: KEY AND VALUE CAN BE ANYTHING");
          System.out.println("Enter Key:");
          Scanner sc = new Scanner(System.in);
          String key = sc.nextLine();

          System.out.println("Enter Value:");
          sc = new Scanner(System.in);
          String value = sc.nextLine();
          String output = client.put(key, value, clientInfo);
          System.out.println(output);
          break;
        case 2:
          System.out.println("GET REQUEST: KEY CAN BE ANYTHING");
          System.out.println("Enter Key:");
          sc = new Scanner(System.in);
          key = sc.nextLine();

          output = client.get(key, clientInfo);
          System.out.println(output);

          break;
        case 3:
          System.out.println("DELETE REQUEST: KEY CAN BE ANYTHING");
          System.out.println("Enter Key:");
          sc = new Scanner(System.in);
          key = sc.nextLine();

          output = client.remove(key, clientInfo);
          System.out.println(output);

          break;
        case 4:
          System.out.println("Exiting.....");
          return;

      }

    }
  }


}
