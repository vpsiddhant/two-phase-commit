import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.util.LinkedList;

public class CoordinatorHandler implements CoordinatorService.Iface {

  String getTime(long durationInMillis){
    long millis = durationInMillis % 1000;
    long second = (durationInMillis / 1000) % 60;
    long minute = (durationInMillis / (1000 * 60)) % 60;
    long hour = (durationInMillis / (1000 * 60 * 60)) % 24;

    String time = String.format("%02d:%02d:%02d.%d", hour, minute, second, millis);


    return time;
  }


  static LinkedList<Integer> serverPortList;

  void setPortList(LinkedList ports) {
    serverPortList = ports;
  }


  boolean canCommitHelper(LinkedList ports,Transaction transact) {

    long durationInMillis = System.currentTimeMillis();
    String time = getTime(durationInMillis);
    String message = "[" +  time +"]" + "Two phase commit initiated";
    System.out.println(message);
    boolean flag = true;
    for ( Integer port: serverPortList) {
      System.out.println("can commit for port: " + port);
      TTransport transport;
      transport = new TSocket("localhost", port);
      try {
        transport.open();
      } catch (TTransportException e) {
        return false;
      }

      TProtocol protocol = new TBinaryProtocol(transport);

      ServerService.Client client = new ServerService.Client(protocol);
      try {
        flag = flag && client.canCommit(transact);
      } catch (TException e) {
        return false;
      }
      transport.close();
    }
return flag;
  }


  boolean doCommitHelper(LinkedList ports, Transaction transact) {
    long durationInMillis = System.currentTimeMillis();
    String time = getTime(durationInMillis);
    String message = "[" +  time +"]" + "Two phase commit completed";
    System.out.println(message);
    boolean flag = true;
    for ( Integer port: serverPortList) {
      TTransport transport;
      transport = new TSocket("localhost", port);
      try {
        transport.open();
      } catch (TTransportException e) {
        e.printStackTrace();
        return false;
      }

      TProtocol protocol = new TBinaryProtocol(transport);

      ServerService.Client client = new ServerService.Client(protocol);
      try {
        flag = flag && client.doCommit(transact);
      } catch (TException e) {
        e.printStackTrace();
        return false;
      }
      transport.close();
    }
    return flag;

  }


  boolean doAbortHelper(LinkedList ports, Transaction transaction) {
    long durationInMillis = System.currentTimeMillis();
    String time = getTime(durationInMillis);
    String message = "[" +  time +"]" + "Two phase commit aborted";
    System.out.println(message);
    boolean flag = true;
    for ( Integer port: serverPortList) {
      TTransport transport;
      transport = new TSocket("localhost", port);
      try {
        transport.open();
      } catch (TTransportException e) {
        return false;
      }

      TProtocol protocol = new TBinaryProtocol(transport);

      ServerService.Client client = new ServerService.Client(protocol);
      try {
        flag = flag && client.doAbort(transaction);
      } catch (TException e) {
        return false;
      }
      transport.close();
    }
    return flag;

  }


  @Override
  public synchronized boolean startTwoPhase(Transaction transact) throws TException {

    if(canCommitHelper(serverPortList,transact)){
      if(doCommitHelper(serverPortList,transact)){
        System.out.println("doCommit started");
        return true;
      }else{
        doAbortHelper(serverPortList,transact);
        return false;
      }
    }else{
      doAbortHelper(serverPortList,transact);
      return false;
    }

  }

  @Override
  public boolean getDecision(Transaction transact, String serverInfo) throws TException {
    return true;
  }
}
