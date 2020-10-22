package il.technion.ewolf.kbr.openkad.single;

import com.google.inject.Guice;
import com.google.inject.Injector;
import il.technion.ewolf.kbr.KeybasedRouting;
import il.technion.ewolf.kbr.Node;
import il.technion.ewolf.kbr.openkad.KadNetModule;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

/**
 * @ClassName: KeybasedRoutingOne
 * @Description:
 * @Author: VT
 * @Date: 2020-10-22 17:28
 */
@Slf4j
public class KeybasedRoutingOne {
    public static void main(String[] args) throws IOException {
        int basePort = 10000;
        Injector injector = Guice.createInjector(new KadNetModule()
                .setProperty("openkad.keyfactory.keysize", "1")
                .setProperty("openkad.bucket.kbuckets.maxsize", "3")
                .setProperty("openkad.seed", ""+(basePort))
                .setProperty("openkad.net.udp.host", "127.0.0.1")
                .setProperty("openkad.net.udp.port", ""+(basePort)));

        KeybasedRouting kbr = injector.getInstance(KeybasedRouting.class);
        kbr.create();
        List<Node> neighbours = kbr.getNeighbours();
        log.info("neighbours:{}",neighbours);
    }
}
