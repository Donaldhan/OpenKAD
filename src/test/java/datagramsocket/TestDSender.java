package datagramsocket;
import java.net.*;
/**
 * @ClassName: TestDSender
 * @Description:
 * @Author: VT
 * @Date: 2020-10-22 10:38
 */
public class TestDSender{
    public static void main(String[] args) throws Exception {
        DatagramSocket ds = new DatagramSocket();
        String str = "Welcome java";
        InetAddress ip = InetAddress.getByName("127.0.0.1");

        DatagramPacket dp = new DatagramPacket(str.getBytes(), str.length(), ip, 3000);
        ds.send(dp);
        ds.close();
    }
}