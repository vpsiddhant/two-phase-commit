import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

import java.util.LinkedList;

public class MultiThreadedCoordinator {
  public static CoordinatorHandler handler;
  public static CoordinatorService.Processor processor;
  public static void main(String[] args) {
    int port = 9090;
    if (args.length <= 1) {
      System.out.println("Error in argument");
      System.exit(0);
    }
    try{
      handler = new CoordinatorHandler();
      LinkedList<Integer> ports  = new LinkedList<Integer>();
      for(int i = 0; i <args.length; i++){
        ports.add(Integer.parseInt(args[i]));
      }
      handler.setPortList(ports);
      processor = new CoordinatorService.Processor(handler);
      Runnable simple = new Runnable() {
        public void run() {
          simple(processor, port);
        }
      };

      new Thread(simple).start();

    } catch (Exception x) {
      x.printStackTrace();
    }
  }


  public static void simple(CoordinatorService.Processor processor, int port) {
    try {
      TServerTransport serverTransport = new TServerSocket(port);
      TServer server;
      server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));

      System.out.println("Starting the multi threaded coordinator...");
      server.serve();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
