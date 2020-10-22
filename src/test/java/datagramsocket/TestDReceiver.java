package datagramsocket;
import java.net.*;
/**
 * @ClassName: TestDReceiver
 * @Description:
 * @Author: VT
 * @Date: 2020-10-22 10:37
 */

public class TestDReceiver{
    public static void main(String[] args) throws Exception {
        DatagramSocket ds = new DatagramSocket(3000);
        byte[] buf = new byte[1024];
        DatagramPacket dp = new DatagramPacket(buf, 1024);
        ds.receive(dp);
        String str = new String(dp.getData(), 0, dp.getLength());
        System.out.println(str);
        ds.close();
    }
}