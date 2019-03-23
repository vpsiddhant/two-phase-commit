import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

import java.util.LinkedList;

public class MultiThreadedServer {
  public static ServerHandler handler;
  public static ServerService.Processor processor;

  public static void main(String [] args) {
    int port = Integer.parseInt(args[0]);
    try{
      handler = new ServerHandler();


      processor = new ServerService.Processor(handler);
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



  public static void simple(ServerService.Processor processor, int port) {
    try {
      TServerTransport serverTransport = new TServerSocket(port);
      TServer server;
      server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));

      System.out.println("Starting the multi threaded server... at port " + port);
      server.serve();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }





}
