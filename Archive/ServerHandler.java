import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.util.HashMap;

public class ServerHandler implements ServerService.Iface {
  int port = 9090;
  HashMap<String,String> map = new HashMap<>();
  HashMap<String,String> getMap = new HashMap<>();

  String getTime(long durationInMillis){
    long millis = durationInMillis % 1000;
    long second = (durationInMillis / 1000) % 60;
    long minute = (durationInMillis / (1000 * 60)) % 60;
    long hour = (durationInMillis / (1000 * 60 * 60)) % 24;

    String time = String.format("%02d:%02d:%02d.%d", hour, minute, second, millis);


    return time;
  }

  boolean twoPhaseCallHelper(Transaction transaction){

    TTransport transport;
    transport = new TSocket("localhost", port);
    try {
      transport.open();
    } catch (TTransportException e) {
      return false;
    }

    TProtocol protocol = new TBinaryProtocol(transport);

    CoordinatorService.Client client = new CoordinatorService.Client(protocol);

    boolean flag = false;
    try {
      flag = client.startTwoPhase(transaction);
    } catch (TException e) {
      return false;
    }
    transport.close();
    return flag;
  }

  @Override
  public String put(String key, String value, String clientinfo) throws org.apache.thrift.TException {
    try {
      long durationInMillis = System.currentTimeMillis();
      String time = getTime(durationInMillis);
      String message = "[" +  time +"]" + "Put request recieved for Key: " + key + "Value: " + value;
      System.out.println(message);
      String clientInfo = "localhost:" + port;
      Transaction transaction = new Transaction();
      transaction.key = key;
      transaction.value = value;
      transaction.type = "PUT";

      if(twoPhaseCallHelper(transaction)){

        durationInMillis = System.currentTimeMillis();
        time = getTime(durationInMillis);
        message = "[" +  time +"]" + "PUT SUCCESSFULL";
        System.out.println(message);
        return message;
      }
      else{
        durationInMillis = System.currentTimeMillis();
        time = getTime(durationInMillis);
        message = "[" +  time +"]" + "PUT UNSUCCESSFULL";
        System.out.println(message);
        return message;
      }

    } catch (Exception x) {
      long durationInMillis = System.currentTimeMillis();
      String time = getTime(durationInMillis);
      String message = "[" +  time +"]" + "PUT UNSUCCESSFULL";
      System.out.println(message);
      return message;
    }

  }

  @Override
  public String get(String key, String clientinfo) throws org.apache.thrift.TException {
    if(getMap.get(key) == null) {
      return "Key doesnt exist";
    }else{
      return getMap.get(key);
    }


  }

  @Override
  public String remove(String key, String clientinfo) throws org.apache.thrift.TException {
    try {
      long durationInMillis = System.currentTimeMillis();
      String time = getTime(durationInMillis);
      String message = "[" +  time +"]" + "DELETE request recieved for Key: " + key;
      System.out.println(message);
      String clientInfo = "localhost:" + port;
      Transaction transaction = new Transaction();
      transaction.key = key;
      transaction.value = "NULL";
      transaction.type = "DELETE";

      if(twoPhaseCallHelper(transaction)){
        durationInMillis = System.currentTimeMillis();
        time = getTime(durationInMillis);
        message = "[" +  time +"]" + "DELETE SUCCESSFULL";
        System.out.println(message);
        return message;

      }
      else{
        durationInMillis = System.currentTimeMillis();
        time = getTime(durationInMillis);
        message = "[" +  time +"]" + "DELETE UNSUCCESSFULL";
        System.out.println(message);
        return message;
      }

    } catch (Exception x) {
      String message =  "Delete SUCCESSFULL";
      System.out.println(message);
      return "Delete UNSUCCESSFULL";
    }

  }

  @Override
  public boolean canCommit(Transaction transact) throws org.apache.thrift.TException {
    return true;
  }

  @Override
  public boolean doCommit(Transaction transact) throws org.apache.thrift.TException {
    if(transact.type.equals("PUT")) {
      map.put(transact.key,transact.value);
      for (String keyva: getMap.keySet()
      ) {
        getMap.remove(keyva);
      }
      for (String keyva: map.keySet()
      ) {
        getMap.put(keyva,map.get(keyva));
      }
    }
    else if (transact.type.equals("DELETE")){
      if(map.get(transact.key) == null){
        return true;
      }else{
        map.remove(transact.key);
        for (String keyva: getMap.keySet()
        ) {
          getMap.remove(keyva);
        }
        for (String keyva: map.keySet()
        ) {
          getMap.put(keyva,map.get(keyva));
        }
      }

    }
    return true;
  }

  @Override
  public boolean doAbort(Transaction transact) throws org.apache.thrift.TException {
    return true;
  }



}
